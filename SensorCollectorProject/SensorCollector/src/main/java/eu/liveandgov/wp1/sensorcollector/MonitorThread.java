package eu.liveandgov.wp1.sensorcollector;

import android.util.Log;

import java.io.File;

/**
 * Created by hartmann on 8/30/13.
 */
public class MonitorThread implements Runnable {
    public static String LOG_TAG = "MonitorThread";

    private RecordingService service;

    public MonitorThread(RecordingService service) {
        this.service = service;
    }

    @Override
    public void run() {
        while (true){
            try{
            Log.i(LOG_TAG, "Queue size " + service.pQ.size() );
            } catch (NullPointerException e){
                Log.i(LOG_TAG, "Queue or file not up.");
            }

            // Sleep 1sec
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
