package eu.liveandgov.wp1.sensorcollector;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by hartmann on 8/30/13.
 */
public class RecordingService extends Service implements SensorEventListener {
    // Constants
    private final String LOG_TAG;

    // Sensors
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;

    // Buffers
    private BlockingQueue<String> sensorLog = new LinkedBlockingQueue<String>();

    public RecordingService(){
        LOG_TAG = this.getClass().getSimpleName();
    }


    public void onCreate(){
        mSensorManager = (SensorManager) getSystemService(getBaseContext().SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        super.onCreate();

        new Thread(new PersistenceThread(sensorLog)).start();
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();

        if (action.equals(IntentConstants.ACTION_SAMPLING_ENABLE)) {
            enableSampling();
        }
        if (action.equals(IntentConstants.ACTION_SAMPLING_DISABLE)) {
            disableSampling();
        }
        return 0;
    }

    private void enableSampling(){
        Log.i(this.getClass().getName(), "Enable sampling");
        mSensorManager.registerListener(this, mAccelerometer,SensorManager.SENSOR_DELAY_GAME);
    }

    private void disableSampling(){
        Log.i(this.getClass().getName(), "Disable sampling");
        mSensorManager.unregisterListener(this, mAccelerometer);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        sensorLog.add(String.format("%s,%d,%f %f %f", event.sensor.getName(), event.timestamp, event.values[0], event.values[1], event.values[2]));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }
}
