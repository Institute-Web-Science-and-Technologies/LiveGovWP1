package eu.liveandgov.sensorcollectorv3.transfer;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;


import java.io.File;
import java.io.IOException;

import eu.liveandgov.sensorcollectorv3.configuration.SensorCollectionOptions;
import eu.liveandgov.sensorcollectorv3.GlobalContext;
import eu.liveandgov.sensorcollectorv3.persistence.Persistor;

/**
 * Transfer sensor.log file to server using HTTP/POST request
 *
 * Use:
 * * .setup()      - setup instance
 * * .doTransfer() - trigger sample transfer
 *
 * Created by hartmann on 8/30/13.
 */
public class TransferThreadPost implements Runnable, TransferManager {
    public static String LOG_TAG = "TransferThreadPost";

    private Thread thread;

    private static final String uploadUrl = SensorCollectionOptions.UPLOAD_URL;

    private Persistor persistor;

    private File stageFile;

    public TransferThreadPost(Persistor persistor, File stageFile) {
        this.stageFile = stageFile;
        this.persistor = persistor;
        thread = new Thread(this);
    };

    @Override
    public void doTransfer(){
        if (thread.isAlive()) { Log.i(LOG_TAG, "Already running."); return; }
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
    public String getStatus() {
        return "StageFile: " + stageFile.length()/1024 + "kb. " +
               (isTransferring() ? "transferring" : "waiting");
    }

    public void run() {
        boolean success;

        // get stage file
        if (stageFile.exists()){
            Log.i(LOG_TAG, "Found old stage file.");
        } else {
            success = persistor.exportSamples(stageFile);
            if (!success) { Log.i(LOG_TAG,"Staging failed");  return; }
        }

        // transfer staged File
        success = transferFile(stageFile);
        if (!success) { Log.i(LOG_TAG,"Transfer failed");  return; }

        // delete local copy
        success = stageFile.delete();
        if (!success) { Log.i(LOG_TAG,"Deletion failed"); return; }

        // terminate
        Log.i(LOG_TAG, "Transfer finished successfully");
    }

    public boolean transferFile(File file, boolean compressed) {
        try {
            HttpClient      httpclient =    new DefaultHttpClient();
            HttpPost        httppost =      new HttpPost(uploadUrl);
            MultipartEntity mEntity =       new MultipartEntity();

            ContentType contentType = compressed ? ContentType.TEXT_PLAIN : ContentType.DEFAULT_BINARY;

            ContentBody fileBody = new FileBody(file, contentType);
            mEntity.addPart("upfile", fileBody);
            httppost.setEntity(mEntity);

            httppost.addHeader("COMPRESSED", String.valueOf(compressed));
            httppost.addHeader("CHECKSUM", String.valueOf(file.length()));
            httppost.addHeader("ID", GlobalContext.getUserId() );

            HttpResponse response = httpclient.execute(httppost);
            int status = response.getStatusLine().getStatusCode();
            if(status != HttpStatus.SC_ACCEPTED) {
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

    public boolean transferFile(File file){
        return transferFile(file, false);
    }


}

