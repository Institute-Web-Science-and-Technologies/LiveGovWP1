package eu.liveandgov.wp1.sensor_collector.rec;

import java.util.List;

import eu.liveandgov.wp1.sensor_collector.api.RecorderConfig;
import eu.liveandgov.wp1.sensor_collector.os.Reporter;
import eu.liveandgov.wp1.sensor_collector.os.SampleTarget;

/**
 * <p>Maintains a timed list of samples that may afterwards be queried by a user</p>
 * <p>The recorder should be a silent sample target</p>
 * Created by lukashaertel on 30.11.2014.
 */
public interface Recorder extends Reporter, SampleTarget {
    /**
     * Registers a sample recorder
     */
    void registerRecorder(RecorderConfig config);

    /**
     * Removes the sample recorder registration
     */
    void unregisterRecorder(RecorderConfig config);

    /**
     * Gets all registered sample recorders
     */
    List<RecorderConfig> getRecorders();

    /**
     * Returns the value of the sample recorder, in serialized form
     */
    List<String> getRecorderItems(RecorderConfig config);
}
