package eu.liveandgov.wp1.sensor_collector.transfer;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import eu.liveandgov.wp1.sensor_collector.GlobalContext;
import eu.liveandgov.wp1.sensor_collector.ServiceSensorControl;
import eu.liveandgov.wp1.sensor_collector.configuration.SensorCollectionOptions;
import eu.liveandgov.wp1.sensor_collector.persistence.Persistor;

/**
 * Transfer sensor.log file to server using HTTP/POST request
 * <p/>
 * Use:
 * * .setup()      - setup instance
 * * .doTransfer() - trigger sample transfer
 * <p/>
 * Created by hartmann on 8/30/13.
 */
public class TransferThreadPost implements Runnable, TransferManager {
    public static String LOG_TAG = "TransferThreadPost";

    private Thread thread;

    private static final String uploadUrl = SensorCollectionOptions.UPLOAD_URL;

    private Persistor persistor;

    private File stageFile;
    //Used to get callback if transfer completed successfully or not
    public ServiceSensorControl.TransferListener listener;

    public TransferThreadPost(Persistor persistor, File stageFile) {
        this.stageFile = stageFile;
        this.persistor = persistor;
        thread = new Thread(this);
    }

    ;

    @Override
    public void doTransfer() {
        if (thread.isAlive()) {
            Log.i(LOG_TAG, "Already running.");
            return;
        }
        if (thread.getState() == Thread.State.TERMINATED) thread = new Thread(this);
        thread.start();
    }

    @Override
    public boolean isTransferring() {
        return thread.isAlive();
    }

    @Override
    public boolean hasStagedSamples() {
        return stageFile.length() > 0;
    }

    @Override
    public void deleteStagedSamples() {
        stageFile.delete();
    }

    @Override
    public String getStatus() {
        return "StageFile: " + stageFile.length() / 1024 + "kb. " +
                (isTransferring() ? "transferring" : "waiting");
    }

    public void run() {
        boolean success;

        try {

            // TODO: Check methods if with success return should rather throw an exception in
            // order to make calleing more uniform.


            // get stage file
            if (stageFile.exists()) {
                Log.i(LOG_TAG, "Found old stage file.");
            } else {
                success = persistor.exportSamples(stageFile);
                if (!success) {
                    Log.i(LOG_TAG, "Staging failed");

                    listener.onTransferCompleted(false);
                    return;
                }
            }

            boolean isCompressed = infereCompressionStatusOf(stageFile);

            // transfer staged File
            success = transferFile(stageFile, isCompressed);
            if (!success) {
                Log.i(LOG_TAG, "Transfer failed");
                listener.onTransferCompleted(false);
                return;
            }

            // delete local copy
            success = stageFile.delete();
            if (!success) {
                Log.i(LOG_TAG, "Deletion failed");
                listener.onTransferCompleted(false);
                return;
            }

            listener.onTransferCompleted(true);
            // terminate
            Log.i(LOG_TAG, "Transfer finished successfully");

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error opening stage file", e);
            listener.onTransferCompleted(false);
        }
    }

    private boolean infereCompressionStatusOf(File stageFile) throws IOException {
        Log.i(LOG_TAG, "Inferring compression of " + stageFile);
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(stageFile);

            // Read files first two bytes and check against the magic-number
            final int first = fileInputStream.read();
            final int second = fileInputStream.read();
            return first == 0x1f && second == 0x8b;
        } finally {
            if (fileInputStream != null) {
                fileInputStream.close();
            }
        }
    }

    public boolean transferFile(File file, boolean compressed) {
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(uploadUrl);

            MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
            multipartEntityBuilder.addBinaryBody("upfile", file);

            httppost.setEntity(multipartEntityBuilder.build());

            httppost.addHeader("COMPRESSED", String.valueOf(compressed));
            httppost.addHeader("CHECKSUM", String.valueOf(file.length()));
            httppost.addHeader("ID", GlobalContext.getUserId());

            HttpResponse response = httpclient.execute(httppost);
            int status = response.getStatusLine().getStatusCode();
            if (status != HttpStatus.SC_ACCEPTED) {
                Log.i(LOG_TAG, "Upload failed w/ Status Code:" + status);
                return false;
            }

        } catch (HttpHostConnectException e) {
            Log.i(LOG_TAG, "Connection Refused");
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean transferFile(File file) {
        return transferFile(file, false);
    }


}

