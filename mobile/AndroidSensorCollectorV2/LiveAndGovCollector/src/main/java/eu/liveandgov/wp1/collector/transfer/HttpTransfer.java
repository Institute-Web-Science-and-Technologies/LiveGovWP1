package eu.liveandgov.wp1.collector.transfer;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by hartmann on 9/15/13.
 */
public class HttpTransfer implements TransferInterface {

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
        HttpPost httppost = new HttpPost(TransferManagerConfig.UPLOAD_URL);
        MultipartEntity mEntity = new MultipartEntity();

        try {
            mEntity.addPart(TransferManagerConfig.FIELD_NAME, new StringBody(content));
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

    @Override
    public void setup() {
    }


}
