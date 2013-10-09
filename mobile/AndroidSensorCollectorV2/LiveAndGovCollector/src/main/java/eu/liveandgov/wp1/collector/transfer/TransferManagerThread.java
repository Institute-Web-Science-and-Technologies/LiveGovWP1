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
public class TransferManagerThread implements TransferManagerInterface {
    private static final String LOG_TAG = "TRANSFER_THREAD";

    private TransferInterface TE = new HttpTransfer();

    /*
     *  VARIABLE DECLARATIONS
     */
    private int STATE = 0;
    public static final int STATE_INIT = 0;
    public static final int STATE_IDLE = 1;
    public static final int STATE_TRANSFER = 2;

    // Persistence
    private PersistenceInterface persister;

    // Transfer Cache
    private StringBuilder TransferCache = new StringBuilder(TransferManagerConfig.MAX_TRANSFER_RECORDS * 50);

    // Monitor Connectivity
    private ConnectivityManager connManager;
    private NetworkInfo mWifi;
    private NetworkInfo mMobile;
    private ConnectivityManager connectivityManager;

    /**
     * For Testing
     */
    public TransferManagerThread(){}

    /**
     * Constructs transfer thread.
     * Needs:
     * - connManager for network state information.
     * - persister for loading data
     *
     * @param persister
     * @param connManager
     */
    public TransferManagerThread(ConnectivityManager connManager, PersistenceInterface persister){
        this.connManager = connManager;
        this.mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        this.mMobile = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        this.persister = persister;
    }

    /**
     * Main methods that controls the flow of data.
     */
    @Override
    public void run() {
        while (true) {
            switch (STATE) {
                case STATE_INIT:
                    Log.i(LOG_TAG, "Initial wait.");
                    doSleep(TransferManagerConfig.INITIAL_WAIT);
                    break;

                case STATE_IDLE:
                    Log.i(LOG_TAG, "Waiting for connection/next transfer.");
                    if (isConnected() && haveData()) {
                        STATE = STATE_TRANSFER;
                    }
                    break;

                case STATE_TRANSFER:
                    Log.i(LOG_TAG, "Transferring data");

                    fillBuffer();

                    boolean success = TE.transferData(getBufferContent());

                    if (success) { clearBuffer(); }
                    // else keep buffer content and try again

                    STATE = STATE_IDLE;
                    break;
            }

            doSleep(TransferManagerConfig.UPDATE_INTERVAL);
        }
    }

    /**
     * Causes the thread to sleep for the required time in ms and
     * ignores the InterruptException.
     */
    private void doSleep(long time){
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
        }
    }

    /**
     * Returns true if a network connection is present, which is
     * configured to handle the sensor traffic.
     * @return networkFlag
     */
    private boolean isConnected() {
        return ( TransferManagerConfig.ENABLE_WIFI && mWifi.isConnected() ) || ( TransferManagerConfig.ENABLE_MOBILE && mMobile.isConnected() );
    }

    /**
     * Returns true if the required minimum of samples in available for transfer.
     * @return dataFlag
     */
    private boolean haveData() {
        return persister.getRecordCount() > TransferManagerConfig.MIN_TRANSFER_RECORDS;
    }

    private void fillBuffer() {
        if (TransferCache.length() > 0) return;

        for (String record: persister.readLines(TransferManagerConfig.MAX_TRANSFER_RECORDS)){
            TransferCache.append(record + "\n");
        }
    }

    private String getBufferContent(){
        return TransferCache.toString();
    }

    private void clearBuffer() {
        TransferCache.delete(0, TransferCache.length());
    }

}
