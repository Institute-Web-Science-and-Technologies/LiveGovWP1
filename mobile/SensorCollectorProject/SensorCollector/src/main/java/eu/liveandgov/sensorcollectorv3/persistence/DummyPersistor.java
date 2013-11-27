package eu.liveandgov.sensorcollectorv3.persistence;

import java.io.File;

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
    public void push(String message) {}

    @Override
    public String getStatus() {
        return "DUMMY";
    }
}
