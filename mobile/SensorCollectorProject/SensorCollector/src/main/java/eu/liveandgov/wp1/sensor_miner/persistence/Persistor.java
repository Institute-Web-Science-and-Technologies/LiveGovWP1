package eu.liveandgov.wp1.sensor_miner.persistence;

import java.io.File;

import eu.liveandgov.wp1.human_activity_recognition.connectors.Consumer;
import eu.liveandgov.wp1.sensor_miner.monitor.Monitorable;

/**
 * Interface for persistence providers.
 *
 * Created by hartmann on 9/20/13.
 */
public interface Persistor extends Consumer<String>, Monitorable {
    /**
     * Exports all stored samples into a given File in the ssf format.
     *
     * @return successFlag
     */
    boolean exportSamples(File stageFile);

    /**
     * Return true if there are samples in store.
     */
    boolean hasSamples();

    /**
     * Delete all stored samples
     */
    void deleteSamples();
}
