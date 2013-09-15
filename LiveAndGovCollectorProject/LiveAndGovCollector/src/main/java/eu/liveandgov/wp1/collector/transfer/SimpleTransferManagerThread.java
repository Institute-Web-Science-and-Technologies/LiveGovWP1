package eu.liveandgov.wp1.collector.transfer;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import eu.liveandgov.wp1.collector.persistence.PersistenceInterface;

/**
 * Transfer available data to server when conncetion available.
 *
 * Created by hartmann on 9/12/13.
 */
public class SimpleTransferManagerThread implements TransferManagerInterface {
    private static final String LOG_TAG = "TRANSFER_THREAD";

    private TransferInterface TE = new TransferZMQ();

    // Persistence
    private PersistenceInterface persister;


    /**
     * Constructs transfer thread.
     * Needs:
     * - connManager for network state information.
     * - persister for loading data
     *
     * @param persister
     * @param connManager
     */
    public SimpleTransferManagerThread(ConnectivityManager connManager, PersistenceInterface persister){
        this.persister = persister;
    }

    /**
     * Main methods that controls the flow of data.
     */
    @Override
    public void run() {
        boolean emptyWait = false;
        Log.i(LOG_TAG,"Setting up network connection");
        TE.setup();

        Log.i(LOG_TAG,"Listening for events");
        while (true) {
            if (persister.getRecordCount() > 0) {
                TE.transferData(persister.pull());
            } else {
                emptyWait = true;
            }

            if (emptyWait) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                emptyWait = false;
            }
        }
    }
}
