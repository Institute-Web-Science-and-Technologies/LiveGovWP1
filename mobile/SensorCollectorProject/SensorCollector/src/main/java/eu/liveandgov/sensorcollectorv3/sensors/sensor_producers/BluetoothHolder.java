package eu.liveandgov.sensorcollectorv3.sensors.sensor_producers;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

import eu.liveandgov.wp1.sensor_miner.sensors.sensor_producers.SensorHolder;

/**
 * Created by lukashaertel on 30.11.13.
 */
public class BluetoothHolder implements SensorHolder {

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

    private final BluetoothAdapter bluetoothAdapter;

    BluetoothHolder()
    {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    @Override
    public void startRecording() {

    }

    @Override
    public void stopRecording() {

    }
}
