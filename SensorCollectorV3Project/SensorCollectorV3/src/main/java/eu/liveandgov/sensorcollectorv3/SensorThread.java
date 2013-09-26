package eu.liveandgov.sensorcollectorv3;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

import eu.liveandgov.sensorcollectorv3.Configuration.SensorCollectionOptions;
import eu.liveandgov.sensorcollectorv3.SensorProducers.ActivityProducer;
import eu.liveandgov.sensorcollectorv3.SensorProducers.LocationProducer;
import eu.liveandgov.sensorcollectorv3.SensorProducers.Producer;
import eu.liveandgov.sensorcollectorv3.SensorProducers.SensorProducer;


/**
 * Created by hartmann on 9/22/13.
 */
public class SensorThread implements Runnable {
    private static final String LOG_TAG = "SensorThread";
    private final SensorManager sensorManager;
    private int nextPort = 5000;
    private Context context;


    public SensorThread(Context context) {
        this.context = context;
        this.sensorManager = (SensorManager)context.getSystemService(context.SENSOR_SERVICE);
    }

    @Override
    public void run() {
        // Setup EventLoop for sensor events.
        Looper.prepare();

        // Register SensorProducer
        Log.i(LOG_TAG, "Starting SensorProducer");

        List<Producer> activeSensors = registerSensors();

        // Connect Sensor Producers to Sensor Sink
        SensorSinkThread SK = new SensorSinkThread();
        for (Producer p : activeSensors){
            SK.subscribeTo(p);
        }
        new Thread(SK).start();

        // Persistor P = MainActivity.P;
        // PersistorThread PT = new PersistorThread(P);
        // PT.connect(SK);
        // new Thread(PT).start();

        Log.i(LOG_TAG, "Sensor Looping");
        Looper.loop();
    }

    private List<Producer> registerSensors() {
        List<Producer> activeSensors = new LinkedList<Producer>();

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
        return removeNullValues(activeSensors);
    }

    private List<Producer> removeNullValues(List<Producer> activeSensors) {
        List<Producer> out = new LinkedList<Producer>();
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
        LP.setContext(this.context);
        return LP;
    }

    private SensorProducer setupSensorProducer(int sensorType){
        Sensor sensor = sensorManager.getDefaultSensor(sensorType);

        Log.i(LOG_TAG, "Registering Listener for " + sensor.getName());

        SensorProducer SP = new SensorProducer(nextPort);
        nextPort += 1;
        sensorManager.registerListener(SP, sensor, SensorManager.SENSOR_DELAY_GAME, new Handler());
        return  SP;
    }


}
