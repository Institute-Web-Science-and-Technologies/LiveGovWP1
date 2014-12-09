package eu.liveandgov.wp1.sensor_collector.transfer;

import android.util.Base64;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.io.ByteSource;
import com.google.common.io.CharStreams;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

import eu.liveandgov.wp1.sensor_collector.api.MoraConfig;
import eu.liveandgov.wp1.sensor_collector.api.Trip;
import eu.liveandgov.wp1.sensor_collector.config.ConfigListener;
import eu.liveandgov.wp1.sensor_collector.config.Configurator;
import eu.liveandgov.wp1.sensor_collector.fs.DataSource;
import eu.liveandgov.wp1.sensor_collector.logging.LogPrincipal;
import eu.liveandgov.wp1.sensor_collector.util.MoraIO;
import eu.liveandgov.wp1.sensor_collector.util.MoraStrings;

/**
 * <p>Transfers a file as multi-part upload via a standard HTTP URL connection</p>
 * Created by lukashaertel on 02.11.2014.
 */
@Singleton
public class PostTransferExecutor implements TransferExecutor {
    /**
     * Logger for the transfer executor
     */
    private static final Logger log = LogPrincipal.get();

    @Inject
    Charset charset;

    /**
     * Holds the upload address
     */
    private String uploadAddress;

    /**
     * Holds if upload should be done compressed
     */
    private boolean uploadCompressed;

    @Inject
    public PostTransferExecutor(Configurator configurator) {
        configurator.initListener(new ConfigListener() {
            @Override
            public void updated(MoraConfig was, MoraConfig config) {
                uploadAddress = config.upload;
                uploadCompressed = config.uploadCompressed;
            }
        }, true);
    }

    /**
     * Encodes a string in default base 64
     *
     * @param s The string to encode
     * @return Returns an encoded string
     */
    private String base64(String s) {
        byte[] bytes = s.getBytes(charset);
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    @Override
    public boolean transfer(Trip trip, DataSource s) throws IOException {

        // Get checksum of file

        HashCode checksum = s.byteSource.hash(Hashing.sha256());

        // Make a boundary with a random uuid
        String boundary = "===" + MoraStrings.randomAlphanumeric(60) + "===";

        // Open a HTTP connection and configure it
        HttpURLConnection httpConn = (HttpURLConnection) new URL(uploadAddress).openConnection();
        try {
            httpConn.setRequestMethod("POST");
            httpConn.setUseCaches(false);
            httpConn.setDoOutput(true); // indicates POST method
            httpConn.setDoInput(true);
            httpConn.setRequestProperty("Authorization", "Basic " + base64(trip.userId + ":" + trip.userSecret));
            httpConn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            httpConn.setRequestProperty("User-Agent", "Post2 Transfer Executor");
            httpConn.setRequestProperty("Accept", "*/*");

            // Add compression status and checksum
            httpConn.addRequestProperty("COMPRESSED", String.valueOf(uploadCompressed));
            httpConn.addRequestProperty("CHECKSUM", checksum.toString());

            // Open an output stream and a writer on this stream
            OutputStream outputStream = httpConn.getOutputStream();
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, charset),
                    true);


            // Write header of the file field
            writer.append("--");
            writer.append(boundary);
            writer.append("\r\nContent-Disposition: form-data; name=\"upfile\"; filename=\"");
            writer.append(s.name);
            writer.append("\"\r\nContent-Type: text/plain\r\n");
            writer.append("Content-Transfer-Encoding: binary\r\n\r\n");

            // Convert to either compression standard
            ByteSource desiredSource = uploadCompressed ?
                    MoraIO.compressUncompressed(s.byteSource) :
                    MoraIO.decompressCompressed(s.byteSource);

            // Copy the converted source to the stream
            desiredSource.asCharSource(charset).copyTo(writer);

            // Write footer for the file entry
            writer.append("\r\n\r\n--");
            writer.append(boundary);
            writer.append("--\r\n");
            writer.close();

            // Get the response code
            String responseText = CharStreams.toString(new InputStreamReader(httpConn.getInputStream()));
            int responseCode = httpConn.getResponseCode();

            // Analyze the response code
            if (responseCode != HttpStatus.SC_ACCEPTED) {
                log.error("Failed with status " + responseCode + ", response text: \r\n" + responseText);
                return false;
            } else {
                log.info("Successfully accepted uploaded, response text: \r\n" + responseText);
                return true;
            }
        } catch (IOException e) {
            String errorText = CharStreams.toString(new InputStreamReader(httpConn.getErrorStream()));
            log.error("Failed with with error: " + errorText);

            throw e;
        } finally {
            // Finally disconnect the HTTP connection
            httpConn.disconnect();
        }
    }
}
