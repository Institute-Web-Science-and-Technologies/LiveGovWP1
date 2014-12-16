package eu.liveandgov.wp1.sensor_collector.components;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

import eu.liveandgov.wp1.data.impl.Bluetooth;
import eu.liveandgov.wp1.sensor_collector.api.MoraConfig;
import eu.liveandgov.wp1.sensor_collector.config.Configurator;
import eu.liveandgov.wp1.sensor_collector.logging.LogPrincipal;
import eu.liveandgov.wp1.sensor_collector.util.MoraConstants;
import eu.liveandgov.wp1.sensor_collector.util.Threaded;

/**
 * <p>Sample source for bluetooth state</p>
 * Created by lukashaertel on 05.12.14.
 */
@Singleton
public class BluetoothSource extends RegularSampleSource {
    private static final Logger log = LogPrincipal.get();


    @Inject
    Context context;

    /**
     * Central credentials store
     */
    @Inject
    Credentials credentials;

    /**
     * Central item buffer
     */
    @Inject
    ItemBuffer itemBuffer;

    /**
     * Message handler
     */
    @Inject
    @Threaded
    Handler handler;

    /**
     * The adapter this bluetooth source is attached to
     */
    private final BluetoothAdapter bluetoothAdapter;

    /**
     * The items in the current recording
     */
    private final List<Bluetooth.Item> items;

    /**
     * The last time a scan has been requested
     */
    private long lastScanRequest;

    @Inject
    public BluetoothSource(Configurator configurator) {
        super(configurator);

        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.items = new ArrayList<Bluetooth.Item>();

        lastScanRequest = Long.MIN_VALUE;
    }

    private void startNextScan() {
        if (bluetoothAdapter != null && bluetoothAdapter.startDiscovery()) {
            lastScanRequest = SystemClock.uptimeMillis();

            log.debug("Scan successfully started");
        } else {
            log.warn("Bluetooth scan could not be started (Is bluetooth activated?)");
        }
    }

    @Override
    protected boolean isAvailable() {
        return bluetoothAdapter == null || !bluetoothAdapter.isEnabled();
    }

    @Override
    protected Integer getDelay(MoraConfig config) {
        return config.bluetooth;
    }

    @Override
    protected void handleActivation() {
        // Register listeners
        context.registerReceiver(bluetoothEndpoint, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED), null, handler);
        context.registerReceiver(bluetoothEndpoint, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED), null, handler);
        context.registerReceiver(bluetoothEndpoint, new IntentFilter(BluetoothDevice.ACTION_FOUND), null, handler);

        // Start first scan immediately
        startNextScan();
    }

    @Override
    protected void handleDeactivation() {
        try {
            context.unregisterReceiver(bluetoothEndpoint);
        } catch (IllegalArgumentException e) {
            log.warn("Receiver already unregistered");
        }
    }


    @Override
    public Bundle getReport() {
        Bundle report = super.getReport();
        report.putInt("items", items.size());
        report.putLong("lastScanRequest", lastScanRequest);
        return report;
    }


    /**
     * <p>Delegate handling updates of the bluetooth state</p>
     */
    private final BroadcastReceiver bluetoothEndpoint = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(intent.getAction())) {
                // On start of the discovery, reset the pending push
                items.clear();
            } else if (BluetoothDevice.ACTION_FOUND.equals(intent.getAction())) {// Extract the stored data from the bundle of the intent
                final BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                final BluetoothClass bluetoothClass = intent.getParcelableExtra(BluetoothDevice.EXTRA_CLASS);
                final String name = intent.hasExtra(BluetoothDevice.EXTRA_NAME) ? intent.getStringExtra(BluetoothDevice.EXTRA_NAME) : null;
                final Short rssi = intent.hasExtra(BluetoothDevice.EXTRA_RSSI) ? intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE) : null;

                final Bluetooth.BondState bondState;
                switch (bluetoothDevice.getBondState()) {
                    case BluetoothDevice.BOND_NONE:
                        bondState = Bluetooth.BondState.NONE;
                        break;

                    case BluetoothDevice.BOND_BONDING:
                        bondState = Bluetooth.BondState.BONDING;
                        break;

                    case BluetoothDevice.BOND_BONDED:
                        bondState = Bluetooth.BondState.BONDED;
                        break;

                    default:
                        bondState = Bluetooth.BondState.UNKNOWN;
                        break;
                }

                items.add(new Bluetooth.Item(
                        bluetoothDevice.getAddress(),
                        MoraConstants.getDeviceMajorClassName(bluetoothClass.getMajorDeviceClass()),
                        MoraConstants.getDeviceClassName(bluetoothClass.getDeviceClass()),
                        bondState,
                        name,
                        rssi
                ));

            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(intent.getAction())) {
                // Get receive-time of the intent in system up-time
                long scanEndTime = SystemClock.uptimeMillis();

                // On end of the discovery, push the results to the pipeline
                itemBuffer.offer(new Bluetooth(
                        System.currentTimeMillis(),
                        credentials.user,
                        items.toArray(new Bluetooth.Item[items.size()])
                ));

                // If results are on time, schedule the next scan at the handler with the given delay
                if (lastScanRequest + getCurrentDelay() > scanEndTime) {
                    if (!handler.postAtTime(new Runnable() {
                        @Override
                        public void run() {
                            startNextScan();
                        }
                    }, lastScanRequest + getCurrentDelay())) {
                        // If failed to schedule, scan immediately
                        startNextScan();
                    }
                } else {
                    // Else, scan immediately
                    startNextScan();
                }
            } else {
                throw new IllegalStateException("Illegal configuration of broadcast receiver");
            }
        }
    };
}
