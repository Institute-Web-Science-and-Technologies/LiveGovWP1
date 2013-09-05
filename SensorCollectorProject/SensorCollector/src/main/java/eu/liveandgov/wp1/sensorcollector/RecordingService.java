package eu.liveandgov.wp1.sensorcollector;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by hartmann on 8/30/13.
 */
public class RecordingService extends Service implements SensorEventListener {
    // Constants
    public static final String LOG_TAG = "RecordingService";
    public static String SENSOR_FILENAME = "sensor.log";

    // Sensors
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;

    // Buffers
    public Queue<String> pQ;


    //
    // Android Lifecycle
    //

    public void onCreate() {
        mSensorManager = (SensorManager) getSystemService(getBaseContext().SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // InitQ
        try {
//            File sensorLogFile = new File(getBaseContext().getFilesDir(),SENSOR_FILENAME);
//            pQ = new PersistenceQueue<String>(sensorLogFile.getAbsolutePath());
//            Log.i(LOG_TAG, "Created persistenceQueue");
            pQ = new LinkedList<String>();
        } catch (IOException e){
            Log.e(LOG_TAG, "Error creating persistenceQueue");
            e.printStackTrace();
            return;
        }

        // start monitoring thread
        new Thread( new MonitorThread(this) ).start();

        // start transfer thread
        new Thread( new TransferThread(this) ).start();

        super.onCreate();
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();

        if (action.equals(Constants.ACTION_SAMPLING_ENABLE)) {
            enableSampling();
        }
        if (action.equals(Constants.ACTION_SAMPLING_DISABLE)) {
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
        String logString = "";

        if (event.sensor.getName().equals(mAccelerometer.getName())) {
            // Accelerometer
            logString = String.format("ACC,%d,%f %f %f\n", event.timestamp / 1000, event.values[0], event.values[1], event.values[2]);
        }

        try {
        // sensorFileWriter.write(logString);
            pQ.add(logString);
        } catch (IOException e) {
            e.printStackTrace();
        }

//        sensorLog.add();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }
}
