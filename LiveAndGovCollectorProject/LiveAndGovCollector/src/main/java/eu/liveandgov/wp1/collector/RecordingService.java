package eu.liveandgov.wp1.collector;

import android.app.Service;
import android.content.Intent;
import android.hardware.SensorEventListener;
import android.os.IBinder;

import eu.liveandgov.wp1.collector.sensor.SensorListener;

/**
 * Created by cehlen on 9/12/13.
 */
public class RecordingService extends Service {

    SensorListener listener;


    public void onCreate() {
        listener = new SensorListener(this);
        listener.start();

        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
