package eu.liveandgov.wp1.collector;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.util.Log;

import eu.liveandgov.wp1.collector.persistence.MockPersister;
import eu.liveandgov.wp1.collector.persistence.PersistenceInterface;
import eu.liveandgov.wp1.collector.sensor.SensorListener;
import eu.liveandgov.wp1.collector.transfer.SimpleTransferManagerThread;
import eu.liveandgov.wp1.collector.transfer.TransferManagerInterface;
import eu.liveandgov.wp1.collector.transfer.TransferManagerThread;

/**
 * Created by cehlen on 9/12/13.
 */
public class SimpleRecordingService extends Service implements SensorEventListener {
    public static final String LOG_TAG = "SRS";

    private PersistenceInterface PI;
    private TransferManagerInterface TM;


    public void onCreate() {
        Log.i(LOG_TAG, "Started RecordingService");

        Context context = getBaseContext();
        SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        if (accelerometer != null) {
            Log.i(LOG_TAG, "Registering ACC Listener");
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        }

        PI = new MockPersister();

        TM = new SimpleTransferManagerThread(
                (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE),
                PI);

        new Thread(TM).start();

        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        PI.save(sensorEvent.toString());
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
