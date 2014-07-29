package eu.liveandgov.wp1.sensor_collector.persistence;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;

import eu.liveandgov.wp1.data.Item;
import eu.liveandgov.wp1.sensor_collector.logging.LP;

/**
 * Created by hartmann on 11/12/13.
 */
public class FilePublisher extends FilePersistor {
    private final Logger log = LP.get();

    public FilePublisher(File logFile) {
        super(logFile);
    }

    @Override
    public synchronized void push(Item item) {
        if (disabled) return;

        super.push(item);
        log.debug("Writing messsage:" + item);

        try {
            super.fileWriter.flush();
        } catch (IOException e) {
            log.error("IO Exception", e);
        }
    }
}
