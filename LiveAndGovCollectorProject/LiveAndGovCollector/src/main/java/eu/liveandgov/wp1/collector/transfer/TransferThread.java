package eu.liveandgov.wp1.collector.transfer;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import eu.liveandgov.wp1.collector.persistence.PersistenceInterface;

/**
 * Transfer available data to server when conncetion available.
 *
 * Created by hartmann on 9/12/13.
 */
public class TransferThread implements TransferInterface {
    private static final String LOG_TAG = "TRANSFER_THREAD";

    /*
     *  GLOBAL CONFIGURATION
     */
    public static final int UPDATE_INTERVAL = 1000; // 1 sec in ms
    public static final int INITIAL_WAIT = 1000; // 10000; // 10 sec in ms
//    public static final String UPLOAD_URL = "http://141.26.71.84:8080/backend/upload";
    public static final String UPLOAD_URL = "http://141.26.71.84:3030/";
    private static final String FIELD_NAME = "upfile";

    // Allowed Transfer option
    public static final boolean ENABLE_WIFI = true;
    public static final boolean ENABLE_MOBILE = false;
    private static final int MIN_TRANSFER_RECORDS = 50;
    private static final int MAX_TRANSFER_RECORDS = 1000;

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
    private StringBuilder TransferCache = new StringBuilder(MAX_TRANSFER_RECORDS * 50);

    // Monitor Connectivity
    private ConnectivityManager connManager;
    private NetworkInfo mWifi;
    private NetworkInfo mMobile;
    private ConnectivityManager connectivityManager;

    /**
     * For Testing
     */
    public TransferThread(){}

    /**
     * Constructs transfer thread.
     * Needs:
     * - connManager for network state information.
     * - persister for loading data
     *
     * @param persister
     * @param connManager
     */
    public TransferThread(ConnectivityManager connManager, PersistenceInterface persister){
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
                    doSleep(INITIAL_WAIT);
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

                    boolean success = transferData(getBufferContent());

                    if (success) { clearBuffer(); }
                    // else keep buffer content and try again

                    STATE = STATE_IDLE;
                    break;
            }

            doSleep(UPDATE_INTERVAL);
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
        return ( ENABLE_WIFI && mWifi.isConnected() ) || ( ENABLE_MOBILE && mMobile.isConnected() );
    }

    /**
     * Returns true if the required minimum of samples in available for transfer.
     * @return dataFlag
     */
    private boolean haveData() {
        return persister.getRecordCount() > MIN_TRANSFER_RECORDS;
    }

    /**
     * Transfers a the content String to the server as HTTP POST request
     * with a FORM/MULTIPART body.
     *
     * Returns True on success, False on error
     * @param content
     * @return successFlag
     */
    public boolean transferData(String content) {
        // Create a new HttpClient and Post Header
        HttpClient httpclient = new DefaultHttpClient();

        // Add POST request with multipart field
        HttpPost httppost = new HttpPost(UPLOAD_URL);
        MultipartEntity mEntity = new MultipartEntity();

        try {
            mEntity.addPart(FIELD_NAME, new StringBody(content));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return false;
        }

        httppost.setEntity(mEntity);

        try {
            httpclient.execute(httppost);
        } catch (IOException e) {
            return false;
        }

        return true;
    }

    private void fillBuffer() {
        if (TransferCache.length() > 0) return;

        for (String record: persister.readLines(MAX_TRANSFER_RECORDS)){
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
