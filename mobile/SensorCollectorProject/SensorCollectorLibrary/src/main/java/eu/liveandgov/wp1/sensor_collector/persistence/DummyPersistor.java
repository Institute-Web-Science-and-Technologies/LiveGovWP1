package eu.liveandgov.wp1.sensor_collector.persistence;

import java.io.File;

import eu.liveandgov.wp1.data.Item;

/**
 * Dummy class that returns non-null values to all methods.
 *
 * Created by hartmann on 11/27/13.
 */
public class DummyPersistor implements Persistor {

    static final DummyPersistor instance = new DummyPersistor();

    @Override
    public boolean exportSamples(File stageFile) {
        return true;
    }

    @Override
    public boolean hasSamples() {
        return false;
    }

    @Override
    public void deleteSamples() {}

    @Override
    public void close() {}

    @Override
    public void push(Item item) {}

    @Override
    public String getStatus() {
        return "DUMMY";
    }
}
