package eu.liveandgov.sensorcollectorv3.Transfer;

import eu.liveandgov.sensorcollectorv3.Monitor.Monitorable;

/**
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
}
