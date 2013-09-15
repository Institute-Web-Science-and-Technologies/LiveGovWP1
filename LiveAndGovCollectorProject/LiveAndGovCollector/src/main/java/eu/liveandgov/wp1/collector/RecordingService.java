package eu.liveandgov.wp1.collector;

import android.app.Service;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.util.Log;

import eu.liveandgov.wp1.collector.persistence.MockPersister;
import eu.liveandgov.wp1.collector.sensor.SensorListener;
import eu.liveandgov.wp1.collector.transfer.TransferManagerThread;

/**
 * Created by cehlen on 9/12/13.
 */
public class RecordingService extends Service {
    public static final String LOG_TAG = "RecordingService";

    SensorListener listener;

    public void onCreate() {
        Log.i(LOG_TAG, "Started RecordingService");

        listener = new SensorListener(this);
        listener.start();

        TransferManagerThread t = initTransferThread();
        new Thread(t).start();

        super.onCreate();
    }

    private TransferManagerThread initTransferThread() {
        return new TransferManagerThread((ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE), new MockPersister());
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    }
