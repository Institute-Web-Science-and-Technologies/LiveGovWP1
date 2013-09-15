package eu.liveandgov.wp1.collector;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import eu.liveandgov.wp1.collector.persistence.MockPersister;
import eu.liveandgov.wp1.collector.persistence.PersistenceInterface;
import eu.liveandgov.wp1.collector.sensor.SensorProducer;
import eu.liveandgov.wp1.collector.sensor.SensorSink;
import eu.liveandgov.wp1.collector.transfer.SimpleTransferManagerThread;
import eu.liveandgov.wp1.collector.transfer.TransferManagerInterface;

/**
 * Created by cehlen on 9/12/13.
 */
public class ZmqRecordingService extends Service implements Runnable {
    private static Integer nextPort = 5000;
    public static final String LOG_TAG = "ZRS";

    private SensorManager sensorManager;

    public void onCreate() {
        Log.i(LOG_TAG, "Started ZmqRecordingService");

        Context context = getBaseContext();
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

        // do networking tasks in new Thread
        new Thread(this).start();
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private SensorProducer setupSensorProducer(int sensorType){
        Sensor sensor = sensorManager.getDefaultSensor(sensorType);

        Log.i(LOG_TAG, "Registering Listener for " + sensor.getName());

        SensorProducer SP = new SensorProducer(nextPort);
        nextPort += 1;
        sensorManager.registerListener(SP, sensor, SensorManager.SENSOR_DELAY_GAME, new Handler());

        return  SP;
    }

    @Override
    public void run() {
        Looper.prepare();

        // Register SensorProducer
        SensorProducer ASP = setupSensorProducer(Sensor.TYPE_ACCELEROMETER);
        SensorProducer LSP = setupSensorProducer(Sensor.TYPE_LINEAR_ACCELERATION);

        SensorSink SSK = new SensorSink();
        SSK.subscribe(ASP);
        SSK.subscribe(LSP);

        new Thread(SSK).start();

        Looper.loop();

    }
}
