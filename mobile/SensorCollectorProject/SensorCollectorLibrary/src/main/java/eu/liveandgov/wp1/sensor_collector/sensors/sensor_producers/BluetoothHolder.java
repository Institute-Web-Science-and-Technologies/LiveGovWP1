package eu.liveandgov.wp1.sensor_collector.sensors.sensor_producers;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.text.StrBuilder;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.TreeMap;

import eu.liveandgov.wp1.sensor_collector.GlobalContext;
import eu.liveandgov.wp1.sensor_collector.configuration.SensorCollectionOptions;
import eu.liveandgov.wp1.sensor_collector.connectors.sensor_queue.SensorQueue;
import eu.liveandgov.wp1.sensor_collector.sensors.SensorSerializer;

/**
 * Created by lukashaertel on 30.11.13.
 */
public class BluetoothHolder implements SensorHolder {
    public static String LOG_TAG = "BLTH";

    /**
     * This TreeMap maps the integer representing the device major class to its name.
     */
    private static final Map<Integer, String> DEVICE_MAJOR_CLASS_MAP = new TreeMap<Integer, String>();

    /**
     * This TreeMap maps the integer representing the device class to its name.
     */
    private static final Map<Integer, String> DEVICE_CLASS_MAP = new TreeMap<Integer, String>();

    static
    {
        for(Field field : BluetoothClass.Device.Major.class.getFields())
        {
            if((field.getModifiers() & Modifier.STATIC) == 0)continue;
            if((field.getModifiers() & Modifier.PUBLIC) == 0)continue;

            if(field.getType().isAssignableFrom(int.class))
            {
                try
                {
                    // Insert inverse mapping
                    DEVICE_MAJOR_CLASS_MAP.put(field.getInt(null), field.getName().replace('_', ' ').toLowerCase());
                }
                catch(IllegalAccessException e)
                {
                    // Do nothing
                }
            }
        }

        // Apply the same pattern to the composed class
        for(Field field : BluetoothClass.Device.class.getFields())
        {
            if((field.getModifiers() & Modifier.STATIC) == 0)continue;
            if((field.getModifiers() & Modifier.PUBLIC) == 0)continue;

            if(field.getType().isAssignableFrom(int.class))
            {
                try
                {
                    // Insert inverse mapping
                    DEVICE_CLASS_MAP.put(field.getInt(null), field.getName().replace('_', ' ').toLowerCase());
                }
                catch(IllegalAccessException e)
                {
                    // Do nothing
                }
            }
        }
    }

    public static String getDeviceMajorClassName(int i)
    {
        return DEVICE_MAJOR_CLASS_MAP.get(i);
    }

    public static String getDeviceClassName(int i)
    {
        return DEVICE_CLASS_MAP.get(i);
    }

    public static String getBondName(int i)
    {
        if(i == BluetoothDevice.BOND_NONE)
        {
            return "none";
        }
        else if(i == BluetoothDevice.BOND_BONDING)
        {
            return "bonding";
        }
        else if(i == BluetoothDevice.BOND_BONDED)
        {
            return "bonded";
        }
        else
        {
            return "unknown";
        }
    }

    private final SensorQueue sensorQueue;
    private final int delay;
    private final Handler handler;
    private final BluetoothAdapter bluetoothAdapter;
    private final StrBuilder bluetoothIntermediateBuilder;
    private long lastScanRequest;

    public BluetoothHolder(SensorQueue sensorQueue,int delay, Handler handler)
    {
        this.sensorQueue = sensorQueue;
        this.delay = delay;
        this.handler = handler;
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.bluetoothIntermediateBuilder = new StrBuilder();
    }

    public static String intermediateFromBTFound(Intent intent) {
        final StringBuilder builder = new StringBuilder();

        // Extract the stored data from the bundle of the intent
        final BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        final BluetoothClass bluetoothClass = intent.getParcelableExtra(BluetoothDevice.EXTRA_CLASS);
        final String name = intent.hasExtra(BluetoothDevice.EXTRA_NAME) ? intent.getStringExtra(BluetoothDevice.EXTRA_NAME) : null;
        final Short rssi = intent.hasExtra(BluetoothDevice.EXTRA_RSSI) ? intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE) : null;

        // Write the values as a tuple of Escaped Address/Device Major Class/Device Class/Bond State/Optional Escaped Name/Optional RSSI
        builder.append("\"" + StringEscapeUtils.escapeJava(bluetoothDevice.getAddress()) + "\"");
        builder.append('/');
        builder.append(getDeviceMajorClassName(bluetoothClass.getMajorDeviceClass()));
        builder.append('/');
        builder.append(getDeviceClassName(bluetoothClass.getDeviceClass()));
        builder.append('/');
        builder.append(getBondName(bluetoothDevice.getBondState()));
        builder.append('/');
        if (name != null) {
            builder.append("\"" + StringEscapeUtils.escapeJava(name) + "\"");
        }
        builder.append('/');
        if (rssi != null) {
            builder.append(rssi);
        }

        // Return the created value
        return builder.toString();
    }

    private void startNextScan() {
        if(bluetoothAdapter.startDiscovery())
        {
            lastScanRequest = SystemClock.uptimeMillis();

            Log.d(LOG_TAG, "Scan successfully started");
        }
        else
        {
            Log.w(LOG_TAG, "Bluetooth scan could not be started (Is bluetooth activated?)");
        }
    }

    @Override
    public void startRecording() {
        checkEnableBluetooth();

        // Register listeners
        GlobalContext.context.registerReceiver(bluetoothEndpoint, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED), null, handler);
        GlobalContext.context.registerReceiver(bluetoothEndpoint, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED), null, handler);
        GlobalContext.context.registerReceiver(bluetoothEndpoint, new IntentFilter(BluetoothDevice.ACTION_FOUND), null, handler);

        // Start first scan immediately
        startNextScan();
    }

    @Override
    public void stopRecording() {
        try
        {
            GlobalContext.context.unregisterReceiver(bluetoothEndpoint);
        }
        catch (IllegalArgumentException e)
        {
            Log.w(LOG_TAG, "Receiver already unregistered");
        }
    }

    private final BroadcastReceiver bluetoothEndpoint = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(intent.getAction()))
            {
                // On start of the discovery, reset the pending push
                bluetoothIntermediateBuilder.clear();
            }
            else if(BluetoothDevice.ACTION_FOUND.equals(intent.getAction()))
            {
                if(!bluetoothIntermediateBuilder.isEmpty())
                {
                    // We are operating on a tail element, so separate
                    bluetoothIntermediateBuilder.append(';');
                }

                bluetoothIntermediateBuilder.append(intermediateFromBTFound(intent));
            }
            else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(intent.getAction()))
            {
                // Get receive-time of the intent in system uptime
                long scanEndtime = SystemClock.uptimeMillis();

                // On end of the discovery, push the results to the pipeline
                sensorQueue.push(SensorSerializer.bluetoothIntermediate.toSSFDefault(bluetoothIntermediateBuilder.toString()));

                // If results are on time, schedule the next scan at the handler with the given delay
                if(lastScanRequest + delay > scanEndtime)
                {
                    if(!handler.postAtTime(new Runnable() {
                        @Override
                        public void run() {
                            startNextScan();
                        }
                    }, lastScanRequest + delay))
                    {
                        // If failed to schedule, scan immediately
                        startNextScan();
                    }
                }
                else
                {
                    // Else, scan immediately
                    startNextScan();
                }
            }
            else
            {
                throw new IllegalStateException("Illegal configuration of broadcast receiver");
            }
        }
    };

    private void checkEnableBluetooth(){
        if(!SensorCollectionOptions.ASK_BLT) return;

        if( bluetoothAdapter != null && !bluetoothAdapter.isEnabled()) {
            Toast toast = Toast.makeText(GlobalContext.context, "Please enable Bluetooth.", Toast.LENGTH_SHORT);
            toast.show();

            Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            GlobalContext.context.startActivity(intent);
        }
    }
}
