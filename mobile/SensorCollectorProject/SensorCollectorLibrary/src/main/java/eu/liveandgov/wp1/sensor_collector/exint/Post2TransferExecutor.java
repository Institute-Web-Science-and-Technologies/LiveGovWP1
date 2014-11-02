package eu.liveandgov.wp1.sensor_collector.exint;

import android.util.Base64;

import com.google.common.base.Charsets;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.io.CharStreams;
import com.google.common.io.Files;

import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.UUID;

import eu.liveandgov.wp1.sensor_collector.logging.LogPrincipal;

/**
 * <p>Alternative transfer that eliminates the need for TPL</p>
 * Created by lukashaertel on 02.11.2014.
 */
public class Post2TransferExecutor implements TransferExecutor {
    /**
     * Logger for the transfer executor
     */
    private final Logger log = LogPrincipal.get();

    /**
     * Charset for the transfer
     */
    private static final Charset CHARSET = Charsets.UTF_8;

    /**
     * Encodes a string in default base 64
     *
     * @param s The string to encode
     * @return Returns an encoded string
     */
    private static String base64(String s) {
        byte[] bytes = s.getBytes(CHARSET);
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    public static Post2TransferExecutor INSTANCE = new Post2TransferExecutor();

    @Override
    public boolean transfer(String target, String id, String secret, boolean compressed, File file) throws IOException {
        // Get checksum of file
        HashCode checksum = Files.hash(file, Hashing.sha256());

        // Make a boundary with a random uuid
        String boundary = "===" + UUID.randomUUID() + "===";

        // Open a HTTP connection and configure it
        HttpURLConnection httpConn = (HttpURLConnection) new URL(target).openConnection();
        try {

            httpConn.setRequestMethod("POST");
            httpConn.setUseCaches(false);
            httpConn.setDoOutput(true); // indicates POST method
            httpConn.setDoInput(true);
            httpConn.setRequestProperty("Authorization", "Basic " + base64(id + ":" + secret));
            httpConn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            httpConn.setRequestProperty("User-Agent", "Post2 Transfer Executor");

            // Add compression status and checksum
            httpConn.addRequestProperty("COMPRESSED", String.valueOf(compressed));
            httpConn.addRequestProperty("CHECKSUM", checksum.toString());

            // Open an output stream and a writer on this stream
            OutputStream outputStream = httpConn.getOutputStream();
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, CHARSET),
                    true);

            // Write header of the file field
            String fileName = file.getName();
            writer.append("--");
            writer.append(boundary);
            writer.append("\r\nContent-Disposition: form-data; name=\"upfile\"; filename=\"");
            writer.append(fileName);
            writer.append("\"\r\nContent-Type: ");
            writer.append(URLConnection.guessContentTypeFromName(fileName));
            writer.append("\r\nContent-Transfer-Encoding: binary\r\n\r\n");

            // Copy the file to the stream
            Files.copy(file, outputStream);

            // Write footer for the file entry
            writer.append("\r\n\r\n--");
            writer.append(boundary);
            writer.append("--\r\n");
            writer.close();

            // Get the response code
            int responseCode = httpConn.getResponseCode();
            String responseText = CharStreams.toString(new InputStreamReader(httpConn.getInputStream()));

            // Analyze the response code
            if (responseCode != HttpStatus.SC_ACCEPTED) {
                log.error("Failed with status " + responseCode + ", response text: \r\n" + responseText);
                return false;
            } else {
                log.info("Failed with status " + responseCode + ", response text: \r\n" + responseText);
                return false;
            }
        } finally {
            // Finally disconnect the HTTP connection
            httpConn.disconnect();
        }
    }
}
