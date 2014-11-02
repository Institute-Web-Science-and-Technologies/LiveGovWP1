package eu.liveandgov.wp1.sensor_collector.exint;

import com.google.common.io.Files;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import eu.liveandgov.wp1.sensor_collector.logging.LogPrincipal;

/**
 * Created by lukashaertel on 25.08.2014.
 */
public class PostTransferExecutor implements TransferExecutor {

    private final Logger log = LogPrincipal.get();

    @Override
    public boolean transfer(String target, String id, String secret, boolean compressed, File file) throws IOException {
        try {
            log.info("Transferring to " + target + ", ID " + id + ", Secret " + secret + ", Compressed " + compressed + ", File " + file);

            // Make client and setup post
            DefaultHttpClient httpclient = new DefaultHttpClient();

            // New method uses basic authentication
            httpclient.getCredentialsProvider().setCredentials(
                    AuthScope.ANY,
                    new UsernamePasswordCredentials(id, secret)
            );


            HttpPost httppost = new HttpPost(target);
            httppost.addHeader("COMPRESSED", String.valueOf(compressed));
            httppost.addHeader("CHECKSUM", String.valueOf(calculateChecksum(file)));

            // Execute for response
            HttpResponse response = httpclient.execute(httppost);

            // Get the response status code
            int status = response.getStatusLine().getStatusCode();

            // If the status is not accepted, print it
            if (status != HttpStatus.SC_ACCEPTED)
                log.error("Transfer error, target: " + target + ", status code: " + status + ", response: " + EntityUtils.toString(response.getEntity()));
            else return true;
        } catch (HttpHostConnectException e) {
            log.error("Connection Refused", e);
        } catch (IOException e) {
            log.error("IO exception occurred", e);
        }
        return false;
    }

    private long calculateChecksum(File file) {
        return file.length();
    }
}
