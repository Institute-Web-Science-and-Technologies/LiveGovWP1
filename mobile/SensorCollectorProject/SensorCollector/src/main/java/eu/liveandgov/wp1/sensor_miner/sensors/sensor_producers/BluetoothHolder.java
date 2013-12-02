package eu.liveandgov.wp1.sensor_miner.sensors.sensor_producers;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;

import org.apache.commons.lang3.text.StrBuilder;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import eu.liveandgov.wp1.sensor_miner.GlobalContext;
import eu.liveandgov.wp1.sensor_miner.connectors.sensor_queue.SensorQueue;
import eu.liveandgov.wp1.sensor_miner.sensors.SensorSerializer;

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
    private final StrBuilder pushBuilder;
    private long lastScanRequest;

    public BluetoothHolder(SensorQueue sensorQueue,int delay, Handler handler)
    {
        this.sensorQueue = sensorQueue;
        this.delay = delay;
        this.handler = handler;
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.pushBuilder = new StrBuilder();
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
                pushBuilder.clear();
            }
            else if(BluetoothDevice.ACTION_FOUND.equals(intent.getAction()))
            {
                if(!pushBuilder.isEmpty())
                {
                    // We are operating on a tail element, so separate
                    pushBuilder.append(';');
                }

                pushBuilder.append(SensorSerializer.intermediateFromBTFound(intent));
            }
            else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(intent.getAction()))
            {
                // Get receive-time of the intent in system uptime
                long scanEndtime = SystemClock.uptimeMillis();

                // On end of the discovery, push the results to the pipeline
                sensorQueue.push(SensorSerializer.fromBluetooth(pushBuilder.toString()));

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
}
