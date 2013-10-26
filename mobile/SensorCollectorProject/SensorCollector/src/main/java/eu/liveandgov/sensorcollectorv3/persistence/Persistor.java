package eu.liveandgov.sensorcollectorv3.persistence;

import java.io.File;

import eu.liveandgov.sensorcollectorv3.connectors.Consumer;
import eu.liveandgov.sensorcollectorv3.monitor.Monitorable;

/**
 * Interface for persistence providers.
 *
 * Created by hartmann on 9/20/13.
 */
public interface Persistor extends Monitorable, Consumer<String> {
    /**
     * Exports all stored samples into a given File in the ssf format.
     *
     * Implementations may return null in case, they do not want to export samples.
     *
     * @return successFlag
     */
    boolean exportSamples(File stageFile);

    /**
     * Return true if there are samples in store.
     */
    boolean hasSamples();
}
