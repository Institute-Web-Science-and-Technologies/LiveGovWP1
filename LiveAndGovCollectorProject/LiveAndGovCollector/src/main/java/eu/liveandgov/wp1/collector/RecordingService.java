package eu.liveandgov.wp1.collector;

import android.app.Service;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.IBinder;

import eu.liveandgov.wp1.collector.persistence.MockPersister;
import eu.liveandgov.wp1.collector.sensor.SensorListener;
import eu.liveandgov.wp1.collector.transfer.TransferThread;

/**
 * Created by cehlen on 9/12/13.
 */
public class RecordingService extends Service {

    SensorListener listener;


    public void onCreate() {
        listener = new SensorListener(this);
        listener.start();

        TransferThread t = initTransferThread();
        new Thread(t).start();

        super.onCreate();
    }

    private TransferThread initTransferThread() {
        return new TransferThread((ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE), new MockPersister());
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    }
