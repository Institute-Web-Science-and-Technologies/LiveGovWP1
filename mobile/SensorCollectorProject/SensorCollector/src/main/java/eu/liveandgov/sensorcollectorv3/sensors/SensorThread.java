package eu.liveandgov.sensorcollectorv3.sensors;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.HashSet;
import java.util.Set;

import eu.liveandgov.sensorcollectorv3.configuration.SensorCollectionOptions;
import eu.liveandgov.sensorcollectorv3.GlobalContext;
import eu.liveandgov.sensorcollectorv3.connectors.sensor_queue.SensorQueue;
import eu.liveandgov.sensorcollectorv3.sensors.sensor_producers.ActivityHolder;
import eu.liveandgov.sensorcollectorv3.sensors.sensor_producers.LocationHolder;
import eu.liveandgov.sensorcollectorv3.sensors.sensor_producers.MotionSensorHolder;
import eu.liveandgov.sensorcollectorv3.sensors.sensor_producers.SensorHolder;


/**
 * Singleton class that holds the sensor thread.
 *
 * This thread is responsible for:
 * startRecording / unregister individual sensors
 *
 * The singleton property makes is easy to start/stop recording via static method calls.
 *
 * Created by hartmann on 9/22/13.
 */
public class SensorThread implements Runnable {
    private static final String LOG_TAG = "SensorThread";

    private Set<SensorHolder> activeSensors = new HashSet<SensorHolder>();
    private Handler sensorHandler;
    private SensorQueue sensorQueue;
    private Thread thread;

    /* Private Singleton */
    private static SensorThread instance;

    private SensorThread(SensorQueue sensorQueue){
        this.sensorQueue = sensorQueue;
        this.thread = new Thread(this);
    }

    /*  Static Methods */
    public static void setup(SensorQueue sensorQueue){
        instance = new SensorThread(sensorQueue);
    }

    /**
     * Starts SensorThread
     * Configuration obtained from {@link .Configuration.SensorCollectionOptions}
     * Need to call setup first.
     */
    public static void start() {
        instance.thread.start();
    }

    public static void stopAllRecording(){
        for (SensorHolder p : instance.activeSensors){
            p.stopRecording();
        }
    }

    public static void startAllRecording(){
        for (SensorHolder p : instance.activeSensors){
            p.startRecording();
        }
    }


    // Runnable
    @Override
    public void run() {
        Log.i(LOG_TAG, "Starting Sensorloop");
        // Register MotionSensorHolder
        Looper.prepare();

        // setup message handler
        sensorHandler = new Handler();

        setupSensorHolder();

        // wait for messages
        Looper.loop();
    }


    private void setupSensorHolder() {
        if (SensorCollectionOptions.REC_ACC)     setupMotionSensor(Sensor.TYPE_ACCELEROMETER);
        if (SensorCollectionOptions.REC_LINEAR_ACC) setupMotionSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        if (SensorCollectionOptions.REC_GRAVITY_ACC)    setupMotionSensor(Sensor.TYPE_GRAVITY );
        if (SensorCollectionOptions.REC_GPS)     setupLocationUpdate();
        if (SensorCollectionOptions.REC_G_ACT)   setupActivityUpdate();
    }


    private void setupMotionSensor(int sensorType){
        Sensor sensor = GlobalContext.getSensorManager().getDefaultSensor(sensorType);

        if (sensor == null) {
            Log.i(LOG_TAG,"Sensor " + sensorType + " not available.");
            // sensor not found
            return;
        }

        Log.i(LOG_TAG, "Registering Listener for " + sensor.getName());
        MotionSensorHolder holder = new MotionSensorHolder(sensorQueue, sensor,  SensorManager.SENSOR_DELAY_GAME, sensorHandler);
        activeSensors.add(holder);
    }

    private void setupLocationUpdate() {
        Log.i(LOG_TAG, "Registering Listener for GPS");
        LocationHolder holder = new LocationHolder(sensorQueue, Looper.myLooper());
        activeSensors.add(holder);
    }

    private void setupActivityUpdate() {
        Log.i(LOG_TAG, "Registering Listener for ACTIVITY");
        ActivityHolder holder = new ActivityHolder();
        activeSensors.add(holder);
    }
}
