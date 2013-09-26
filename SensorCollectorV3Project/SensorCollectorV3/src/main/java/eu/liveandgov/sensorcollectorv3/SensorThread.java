package eu.liveandgov.sensorcollectorv3;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

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
//        SensorProducer LSP = setupSensorProducer(Sensor.TYPE_LINEAR_ACCELERATION);
        LocationProducer LP = setupLocationProducer();
        ActivityProducer AP = new ActivityProducer(nextPort++);
        AP.setContext(context);
        SensorSinkThread SK = new SensorSinkThread();

        SK.subscribe(ASP);
        SK.subscribe(LP);
//        SK.subscribe(LSP);


        new Thread(SK).start();

        Persistor P = MainActivity.P;
        PersistorThread PT = new PersistorThread(P);

        PT.connect(SK);

        new Thread(PT).start();

        Log.i(LOG_TAG, "Sensor Looping");
        Looper.loop();
    }

    private LocationProducer setupLocationProducer() {
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
