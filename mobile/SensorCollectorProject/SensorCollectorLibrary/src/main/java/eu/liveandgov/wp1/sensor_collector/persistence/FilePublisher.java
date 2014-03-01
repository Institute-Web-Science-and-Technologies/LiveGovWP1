package eu.liveandgov.wp1.sensor_collector.persistence;

import android.util.Log;

import java.io.File;
import java.io.IOException;

import eu.liveandgov.wp1.data.Item;

/**
 * Created by hartmann on 11/12/13.
 */
public class FilePublisher extends FilePersistor {

    public FilePublisher(File logFile) {
        super(logFile);
    }

    @Override
    public synchronized void push(Item item) {
        if (disabled) return;

        super.push(item);
        Log.d("PUBF", "Writing messsage:" + item);

        try {
            super.fileWriter.flush();
        } catch (IOException e) {
            Log.e(LOG_TAG, "IO Exception", e);
        }
    }
}
