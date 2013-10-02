package eu.liveandgov.sensorcollectorv3.Sensors;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.HashSet;
import java.util.Set;

import eu.liveandgov.sensorcollectorv3.Configuration.SensorCollectionOptions;
import eu.liveandgov.sensorcollectorv3.Sensors.SensorProducers.ActivityHolder;
import eu.liveandgov.sensorcollectorv3.Sensors.SensorProducers.LocationHolder;
import eu.liveandgov.sensorcollectorv3.Sensors.SensorProducers.MotionSensorHolder;
import eu.liveandgov.sensorcollectorv3.Sensors.SensorProducers.SensorHolder;


/**
 * Singleton class that holds the sensor thread.
 *
 * This thread is responsible for:
 * * recieving sensor callbacks
 * * startRecording / unregister individual sensors
 *
 * Created by hartmann on 9/22/13.
 */
public class SensorThread implements Runnable {
    private static final String LOG_TAG = "SensorThread";

    private Set<SensorHolder> activeSensors = new HashSet<SensorHolder>();
    private Handler sensorHandler;

    private Thread thread;

    /* Singleton Pattern */
    private static SensorThread instance;

    // private constructor - cannot be called outside of this class
    private SensorThread(){
        this.thread = new Thread(this);
    }

    public static SensorThread getInstance(){
        if (instance == null) {
            instance = new SensorThread();
        }
        return instance;
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


    // Start Thread
    public void start() {
        thread.start();
    }

    public void setupSensorHolder() {
        if (SensorCollectionOptions.REC_ACC)     setupMotionSensor(Sensor.TYPE_ACCELEROMETER);
        if (SensorCollectionOptions.REC_LIN_ACC) setupMotionSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        if (SensorCollectionOptions.REC_GRAV)    setupMotionSensor(Sensor.TYPE_GRAVITY );
        if (SensorCollectionOptions.REC_GPS)     setupLocationUpdate();
        if (SensorCollectionOptions.REC_G_ACT)   setupActivityUpdate();
//        if (SensorCollectionOptions.REC_GPS)     activeSensors.add( setupLocationProducer() );
//        if (SensorCollectionOptions.REC_GOOGLE_API)
//        activeSensors.add( setupActivityProducer() );
    }

    public void stopAllRecording(){
        for (SensorHolder p : activeSensors){
            p.stopRecording();
        }
    }

    public void startAllRecording(){
        for (SensorHolder p : activeSensors){
            p.startRecording();
        }
    }

    private void setupMotionSensor(int sensorType){
        Sensor sensor = GlobalContext.sensorManager.getDefaultSensor(sensorType);

        if (sensor == null) {
            Log.i(LOG_TAG,"Sensor " + sensorType + " not available.");
            // sensor not found
            return;
        }

        Log.i(LOG_TAG, "Registering Listener for " + sensor.getName());
        MotionSensorHolder holder = new MotionSensorHolder(sensor,  SensorManager.SENSOR_DELAY_GAME, sensorHandler);
        activeSensors.add(holder);
    }

    private void setupLocationUpdate() {
        Log.i(LOG_TAG, "Registering Listener for GPS");
        LocationHolder holder = new LocationHolder(Looper.myLooper());
        activeSensors.add(holder);
    }

    private void setupActivityUpdate() {
        Log.i(LOG_TAG, "Registering Listener for ACTIVITY");
        ActivityHolder holder = new ActivityHolder();
        activeSensors.add(holder);
    }
}
