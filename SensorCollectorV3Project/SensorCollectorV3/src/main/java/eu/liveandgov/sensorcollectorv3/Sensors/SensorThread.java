package eu.liveandgov.sensorcollectorv3.Sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.Set;
import java.util.TreeSet;

import eu.liveandgov.sensorcollectorv3.Configuration.SensorCollectionOptions;
import eu.liveandgov.sensorcollectorv3.Sensors.SensorProducers.ActivityProducer;
import eu.liveandgov.sensorcollectorv3.Sensors.SensorProducers.LocationProducer;
import eu.liveandgov.sensorcollectorv3.Sensors.SensorProducers.Producer;
import eu.liveandgov.sensorcollectorv3.Sensors.SensorProducers.SensorProducer;


/**
 * Singleton class that holds the sensor thread.
 *
 * This thread is responsible for:
 * * recieving sensor callbacks
 * * register / unregister individual sensors
 *
 * Created by hartmann on 9/22/13.
 */
public class SensorThread implements Runnable {
    private static final String LOG_TAG = "SensorThread";

    private SensorManager sensorManager;
    private Context context;
    private int nextPort = 5000;
    private Set<Producer> activeSensors = new TreeSet<Producer>();
    private Handler sensorHandler;

    private Thread thread;

    /* Singleton Pattern */
    private static SensorThread instance;

    // private constructor - cannot be called outside of this class
    private SensorThread(Context context){
        this.context = context;
        this.sensorManager = (SensorManager)context.getSystemService(context.SENSOR_SERVICE);
        this.thread = new Thread(this);
    }

    public static void setupInstance(Context context) {
        instance = new SensorThread(context);
    }

    public static SensorThread getInstance(){
        return instance;
    }


    // Runnable
    @Override
    public void run() {
        Log.i(LOG_TAG, "Starting Sensorloop");
        // Register SensorProducer
        Looper.prepare();

        // setup message handler
        sensorHandler = new Handler();

        // wait for messages
        Looper.loop();
    }


    // Start Thread
    public void start() {
        thread.start();
    }

    public void registerSensors() {
        if (SensorCollectionOptions.REC_ACC)
            activeSensors.add( setupSensorProducer(Sensor.TYPE_ACCELEROMETER) );
        if (SensorCollectionOptions.REC_LIN_ACC)
            activeSensors.add( setupSensorProducer(Sensor.TYPE_LINEAR_ACCELERATION) );
        if (SensorCollectionOptions.REC_GRAV)
           activeSensors.add( setupSensorProducer(Sensor.TYPE_GRAVITY) );
        if (SensorCollectionOptions.REC_GPS)
           activeSensors.add( setupLocationProducer() );
        if (SensorCollectionOptions.REC_GOOGLE_API)
            activeSensors.add( setupActivityProducer() );

        // Filter Sensors that are not available
        activeSensors = removeNullValues(activeSensors);
    }

    public void unregisterSensors(){
        for (Producer p : activeSensors){
            sensorManager.unregisterListener(p);
            activeSensors.remove(p);
        }
    }


    private static Set<Producer> removeNullValues(Set<Producer> activeSensors) {
        Set<Producer> out = new TreeSet<Producer>();
        for(Producer s: activeSensors){
            if (s != null) out.add(s);
        }
        return out;
    }

    private ActivityProducer setupActivityProducer() {
        Log.i(LOG_TAG, "Registering Activity Producer.");
        ActivityProducer AP = new ActivityProducer(nextPort++);
        AP.setContext(context);
        return AP;
    }

    private LocationProducer setupLocationProducer() {
        Log.i(LOG_TAG, "Registering Location Producer.");
        LocationProducer LP = new LocationProducer(nextPort++, Looper.myLooper());
        LP.setContext(context);
        return LP;
    }

    private SensorProducer setupSensorProducer(int sensorType){
        Sensor sensor = sensorManager.getDefaultSensor(sensorType);

        Log.i(LOG_TAG, "Registering Listener for " + sensor.getName());

        SensorProducer SP = new SensorProducer(nextPort);
        nextPort += 1;
        sensorManager.registerListener(SP, sensor, SensorManager.SENSOR_DELAY_GAME, sensorHandler);
        return  SP;
    }
}
