package eu.liveandgov.wp1.server;

import eu.liveandgov.wp1.server.db_helper.BatchInserter;
import eu.liveandgov.wp1.server.db_helper.PostgresqlDatabase;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.sql.SQLException;
import java.util.zip.GZIPInputStream;

import static java.lang.System.currentTimeMillis;

/**
 * Implementation of a sensor data UploadServlet based on {@link org.apache.commons.fileupload}
 * see <a href="http://commons.apache.org/proper/commons-fileupload/">website.</a>
 * <p/>
 * Test with
 * <pre>{@code echo "FILE CONTENTS" | curl localhost:8080/server/upload -F "upfile=@-"}</pre>
 * or
 * <pre>{@code cat test-upload-data.txt | curl localhost:8080/server/upload -F "upfile=@-"}</pre>
 * also a python test script is provided in the /scripts/ folder.
 * <p/>
 * User: hartmann
 * Date: 10/19/13
 */
public class UploadServlet extends HttpServlet {
    private static final String FIELD_NAME_UPFILE = "upfile";
    private static final Logger Log = Logger.getLogger(UploadServlet.class);

    /**
     * Handle GET REQUEST
     * Show a simple HTML Upload form to test the servlet.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Log.info("Incoming GET request from " + req.getRemoteAddr());

        PrintWriter writer = resp.getWriter();
        writer.write(
                "<html>" +
                        "<h1>Upload Servlet</h1>" +
                        "<form action=\"\" enctype=\"multipart/form-data\" method=\"post\">" +
                        "Upload File: <input type=\"file\" name=\"upfile\" size=\"40\"/><br/>" +
                        "<input type=\"submit\" value=\"Submit\">" +
                        "</form>" +
                        "</html>"
        );
    }

    /**
     * Handle POST REQUEST
     * Take form/multi-part attachment with name 'upfile' and write it to the file system.
     * Retuns Status 202 if everything went ok.
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Log.info("Incoming POST request from " + req.getRemoteAddr());

        // Retrieve Upfile
        InputStream fileStream = getStreamFromField(req, FIELD_NAME_UPFILE);

        if (fileStream == null) {
            Log.error("Field not found: " + FIELD_NAME_UPFILE);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // Write to file system
        String fileName = generateFileName(req);
        int bytesWritten = 0;

        File outFile = new File(CONFIG.OUT_DIR, fileName);
        try {
            bytesWritten = writeStreamToFile(fileStream, outFile);
        } catch (IOException e) {
            Log.error("Error writing output file:", e);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        Log.info(String.format("Wrote %d bytes to file %s.", bytesWritten, fileName));

        // Success
        Log.info("Received file " + fileName + " of length " + bytesWritten);
        resp.getWriter().write(
                // Output will be parsed by test script.
                "Status:Success.\n" +
                        "Destination:" + outFile.getAbsolutePath() + "\n" +
                        "Bytes written:" + bytesWritten
        );
        resp.setStatus(HttpServletResponse.SC_ACCEPTED);

        // Insert directly to database
        BufferedReader reader;
        if (!isCompressed(req)) {
            Log.info("Opening Text Reader");
            reader = new BufferedReader(new FileReader(outFile));
        } else {
            Log.info("Opening GZIP Reader");
            reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(outFile)), "UTF8"));
        }

        Log.info("Writing file into database.");
        try {
            PostgresqlDatabase db = new PostgresqlDatabase();
            BatchInserter.batchInsertFile(db, reader);

            Log.debug("Setting Secret: " + getSecret(req));
            db.setSecret(getId(req), getSecret(req));
        } catch (SQLException e) {
            Log.info("Error writing db.", e);
        } catch (IOException e) {
            Log.info("Error reading file" + outFile.getAbsolutePath());
        } finally {
            reader.close();
        }
        Log.info("Wrote data into DB.");
    }

    /**
     * Writes contents of inputStream to a file.
     * Uses Apache IOUtils for copy.
     *
     * @param inputStream stream to read
     * @param file        file to write
     * @return the number of bytes copied, or -1 if > Integer.MAX_VALUE
     * @throws IOException if writing of file fails
     * @see <a href="http://stackoverflow.com/questions/43157/easy-way-to-write-contents-of-a-java-inputstream-to-an-outputstream/43168#43168">StackOverflow</a>
     * for a discussion.
     */
    private int writeStreamToFile(InputStream inputStream, File file) throws IOException {
        assert (inputStream != null) : "inputStream must be non-null";

        OutputStream outputStream = new FileOutputStream(file);
        return IOUtils.copy(inputStream, outputStream);
    }

    /**
     * Generate a "unique" file name for the given request.
     *
     * @param req HttpRequest
     * @return fileName
     */
    private String generateFileName(HttpServletRequest req) {
        String ending = isCompressed(req) ? ".csv.gz" : ".csv";
        return req.getHeader("ID") + "_" + currentTimeMillis() + ending;
    }

    private boolean isCompressed(HttpServletRequest req) {
        return Boolean.parseBoolean(req.getHeader("COMPRESSED"));
    }

    private String getSecret(HttpServletRequest req) {
        return req.getHeader("SECRET");
    }

    private String getId(HttpServletRequest req){
        return req.getHeader("ID");
    }

    /**
     * Get contents of fieldName as InputStream from POST request
     *
     * @param req       HttpRequest Containing a Form Request
     * @param fieldName name of field to isolate
     * @return formFieldStream
     * @see <a href="http://commons.apache.org/proper/commons-fileupload/streaming.html">the docs.</a>
     */
    private InputStream getStreamFromField(HttpServletRequest req, String fieldName) {
        try {
            // Check that we have a file upload request
            boolean isMultipart = ServletFileUpload.isMultipartContent(req);
            if (!isMultipart) {
                Log.error("Received non-multipart POST request");
                return null;
            }

            // Create a new file upload handler
            ServletFileUpload upload = new ServletFileUpload();

            // Parse the request
            FileItemIterator iter = upload.getItemIterator(req);

            while (iter.hasNext()) {
                FileItemStream item = iter.next();
                String name = item.getFieldName();

                if (!name.equals(FIELD_NAME_UPFILE)) {
                    Log.info("Found unknown field " + name);
                    continue;
                }
                return item.openStream();
            }
        } catch (FileUploadException e) {
            Log.error("Error parsing the upfile", e);
        } catch (IOException e) {
            Log.error("Network error while parsing file.", e);
        }
        return null;
    }
}
