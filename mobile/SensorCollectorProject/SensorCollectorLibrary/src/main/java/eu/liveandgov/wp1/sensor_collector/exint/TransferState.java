package eu.liveandgov.wp1.sensor_collector.exint;

/**
 * Created by lukashaertel on 25.08.2014.
 */
public enum TransferState {
    /**
     * Transfer is not completed yet
     */
    RUNNING,

    /**
     * Transfer completed successfully
     */
    SUCCESSFUL,

    /**
     * Transfer completed with a failure
     */
    FAILED
}
