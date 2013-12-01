package eu.liveandgov.wp1.sensor_miner.sensors.sensor_producers;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.util.Log;

import org.apache.commons.lang3.text.StrBuilder;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.TreeMap;

import eu.liveandgov.wp1.sensor_miner.GlobalContext;
import eu.liveandgov.wp1.sensor_miner.connectors.sensor_queue.SensorQueue;

/**
 * Created by lukashaertel on 30.11.13.
 */
public class BluetoothHolder implements SensorHolder {
    public static String LOG_TAG = "BLTH";

    /**
     * This TreeMap maps the integer representing the device class to its name.
     */
    private static final Map<Integer, String> DEVICE_CLASS_MAP = new TreeMap<Integer, String>();

    static
    {
        final Map<Integer, String> deviceMajorClassName = new TreeMap<Integer, String>();

        for(Field field : BluetoothClass.Device.Major.class.getFields())
        {
            // We are looking for public static fields type int
            if((field.getModifiers() & Modifier.STATIC) == 0)continue;
            if((field.getModifiers() & Modifier.PUBLIC) == 0)continue;

            if( field.getType().isAssignableFrom(int.class))
            {
                try
                {
                    // If found, add name to the map
                    deviceMajorClassName.put(field.getInt(null), field.getName().replace('_', ' ').toLowerCase());
                }
                catch(IllegalAccessException e)
                {
                    // Should not happen
                    continue;
                }
            }
        }

        // Apply the same pattern to the composed class
        for(Field field : BluetoothClass.Device.class.getFields())
        {
            if((field.getModifiers() & Modifier.STATIC) == 0)continue;
            if((field.getModifiers() & Modifier.PUBLIC) == 0)continue;

            if( field.getType().isAssignableFrom(int.class))
            {
                try
                {
                    // Get the combined device class code
                    final int majorMinor = field.getInt(null);

                    // Retrieve name of the field and the name of the major device class
                    final String completeName = field.getName();
                    final String majorName =  deviceMajorClassName.get(Integer.highestOneBit(majorMinor));

                    // Insert a more readable version of the name into the map
                    DEVICE_CLASS_MAP.put(majorMinor, majorName + ":" + completeName.substring(majorName.length() + 1).replace('_', ' ').toLowerCase());
                }
                catch(IllegalAccessException e)
                {
                    continue;
                }
            }
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
            lastScanRequest = System.currentTimeMillis();
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
                    pushBuilder.append("<SEPARATOR>");
                }

                pushBuilder.append("<REPRESENTATION OF" + intent + ">");
            }
            else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(intent.getAction()))
            {
                // Get receive-time of the intent
                long timestamp_ms = System.currentTimeMillis();

                // On end of the disvocery, push the results to the pipeline
                Log.d(LOG_TAG,  "<HEAD> " + pushBuilder.toString());

                // If results are on time, schedule the next scan at the handler with the given delay
                if(lastScanRequest + delay > timestamp_ms)
                {
                    handler.postAtTime(new Runnable() {
                        @Override
                        public void run() {
                            startNextScan();
                        }
                    }, lastScanRequest + delay);
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
