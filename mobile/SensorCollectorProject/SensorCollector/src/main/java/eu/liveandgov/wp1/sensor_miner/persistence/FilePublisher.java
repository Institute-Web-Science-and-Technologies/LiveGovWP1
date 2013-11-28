package eu.liveandgov.wp1.sensor_miner.persistence;

import android.util.Log;

import java.io.File;
import java.io.IOException;

/**
 * Created by hartmann on 11/12/13.
 */
public class FilePublisher extends FilePersistor {

    public FilePublisher(File logFile) {
        super(logFile);
    }

    @Override
    public synchronized void push(String s) {
        super.push(s);
        Log.i("PUBF", "Writing messsage:" + s);

        try {
            super.fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e){
            e.printStackTrace();
        }
    }
}
