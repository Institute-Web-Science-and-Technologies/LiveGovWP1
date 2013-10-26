package eu.liveandgov.sensorcollectorv3.transfer;

import eu.liveandgov.sensorcollectorv3.monitor.Monitorable;

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
}
