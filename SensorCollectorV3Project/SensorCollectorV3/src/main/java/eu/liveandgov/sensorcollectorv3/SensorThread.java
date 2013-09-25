package eu.liveandgov.sensorcollectorv3;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.IOException;


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
        Log.i(LOG_TAG, "Starting Sensors");
        SensorProducer ASP = setupSensorProducer(Sensor.TYPE_ACCELEROMETER);
        SensorProducer LSP = setupSensorProducer(Sensor.TYPE_LINEAR_ACCELERATION);

        SensorSinkThread SK = new SensorSinkThread();
        SK.subscribe(ASP);
        SK.subscribe(LSP);

        new Thread(SK).start();

        Persistor P = MainActivity.P;
        PersistorThread PT = new PersistorThread(P);

        PT.connect(SK);

        new Thread(PT).start();

        Log.i(LOG_TAG, "Sensor Looping");
        Looper.loop();
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
