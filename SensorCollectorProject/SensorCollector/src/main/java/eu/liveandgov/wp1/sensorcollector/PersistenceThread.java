package eu.liveandgov.wp1.sensorcollector;

import android.util.Log;

import java.util.concurrent.BlockingQueue;

/**
 * Created by hartmann on 8/30/13.
 */
public class PersistenceThread implements Runnable {
    static String LOG_TAG = "PersistenceThread";

    BlockingQueue<String> sensorLog;

    public PersistenceThread(BlockingQueue<String> sensorLog){
        this.sensorLog = sensorLog;
    }

    public void run() {
        String out = "";
        while(true) {
            // Flush out buffer
            out = "";

            Log.i(LOG_TAG, "Reading " + sensorLog.size() + " elements.");

            // Fill buffer
            while(! sensorLog.isEmpty()) {
                out += sensorLog.poll() + "\n";
            }

            // Write to file
            // Log.i(this.getClass().getName(), out);

            // Sleep 1sec
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
