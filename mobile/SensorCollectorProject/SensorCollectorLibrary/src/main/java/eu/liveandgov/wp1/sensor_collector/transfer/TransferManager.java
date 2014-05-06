package eu.liveandgov.wp1.sensor_collector.transfer;

import eu.liveandgov.wp1.sensor_collector.monitor.Monitorable;

/**
 * Interface that facilitates sample transfer.
 *
 * Created by hartmann on 10/2/13.
 */
public interface TransferManager extends Monitorable {
    /**
     * Transfer samples to server
     */
    void doTransfer();

    /**
     * Returns current transfer status
     *
     * @return isTransferring
     */
    boolean isTransferring();

    /**
     * Returns true if there are samples waiting for transfer.
     *
     * @return samplesStaged
     */
    boolean hasStagedSamples();

    void deleteStagedSamples();
}
