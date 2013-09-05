package eu.liveandgov.wp1.sensorcollector;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by hartmann on 8/30/13.
 */
public class TransferThread implements Runnable {
    static String LOG_TAG = "TransferThread";
    private static Integer no = 0;

    private RecordingService service;

    public TransferThread(RecordingService service){
        this.service = service;
    }

    public void run() {

        while (true) {
            Log.i(LOG_TAG, "Transfering Samples");

            String out = "";

            int i = 0;
            while (! service.pQ.isEmpty() && i < Constants.MAX_TRANSFER_SAMPLES ) {
                i++;
                try {
                    out += service.pQ.remove();
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }

//            transferFile(new ByteArrayInputStream( out.getBytes() ));
            transferFile(out);

            // Sleep 1sec
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void transferFile(String out){
        try {
            // Create a new HttpClient and Post Header
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(Constants.UPLOAD_URL);

            MultipartEntity mEntity = new MultipartEntity();

            mEntity.addPart("upfile", new StringBody(out));
            mEntity.addPart("index", new StringBody(no.toString()));
            no += 1;

            httppost.setEntity(mEntity);

            httpclient.execute(httppost);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

