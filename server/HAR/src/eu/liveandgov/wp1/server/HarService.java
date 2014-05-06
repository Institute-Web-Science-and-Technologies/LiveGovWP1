package eu.liveandgov.wp1.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import eu.liveandgov.wp1.HARPipeline;
import eu.liveandgov.wp1.server.har_service.SSFReader;
import eu.liveandgov.wp1.server.har_service.StringProducer;
import static java.lang.System.currentTimeMillis;

/**
 * Servlet implementation class HarService
 */
@WebServlet("/api")
public class HarService extends HttpServlet {
	private static final long serialVersionUID = 1L;
    static final String OUT_DIR = ".";
    private static final String FIELD_NAME_UPFILE = "upfile";
    private static final Logger Log = Logger.getLogger(HarService.class);

       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public HarService() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // Retrieve Upfile
        InputStream fileStream = getStreamFromField(request, FIELD_NAME_UPFILE);

        if (fileStream == null) {
            Log.error("Field not found: " + FIELD_NAME_UPFILE);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // Write to file system
        String fileName = generateFileName(request);
        int bytesWritten = 0;

        File outFile = new File(OUT_DIR, fileName);
        try {
            bytesWritten = writeStreamToFile(fileStream, outFile);
        } catch (IOException e) {
            Log.error("Error writing output file:",e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // Success
        Log.info("Received file " + fileName + " of length " + bytesWritten);

        // Insert directly to database

        BufferedReader reader;
        if (! isCompressed(request)) {
            Log.info("Opening Text Reader");
            reader = new BufferedReader(new FileReader(outFile));
        } else {
            Log.info("Opening GZIP Reader");
            reader = new BufferedReader(new InputStreamReader(new GZIPInputStream( new FileInputStream(outFile)), "UTF8"));
        }
        
        SSFReader pr = new SSFReader();

        HARPipeline harPipeline = new HARPipeline();
        StringProducer stringProducer = new StringProducer();

        pr.setConsumer(harPipeline);
        harPipeline.setConsumer(stringProducer);
        
        pr.classify(reader);
        
        response.getWriter().write(
                stringProducer.getActivity()
        );
        response.setStatus(HttpServletResponse.SC_ACCEPTED);
	}
	
	
	/**
     * Get contents of fieldName as InputStream from POST request
     *
     * @see <a href="http://commons.apache.org/proper/commons-fileupload/streaming.html">the docs.</a>
     *
     * @param req               HttpRequest Containing a Form Request
     * @param fieldName         name of field to isolate
     * @return formFieldStream
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
    
    private boolean isCompressed(HttpServletRequest req) {
        return Boolean.parseBoolean(req.getHeader("COMPRESSED"));
    }

    
    private String generateFileName(HttpServletRequest req) {
        String ending = isCompressed(req) ? ".csv.gz" : ".csv";
        return req.getHeader("ID") + "_" + currentTimeMillis() + ending;
    }
    
    /**
     *  Writes contents of inputStream to a file.
     *  Uses Apache IOUtils for copy.
     *  @see <a href="http://stackoverflow.com/questions/43157/easy-way-to-write-contents-of-a-java-inputstream-to-an-outputstream/43168#43168">StackOverflow</a>
     *  for a discussion.
     *
     * @param inputStream   stream to read
     * @param file          file to write
     * @throws IOException  if writing of file fails
     * @return the number of bytes copied, or -1 if > Integer.MAX_VALUE
     */
    private int writeStreamToFile(InputStream inputStream, File file) throws IOException {
        assert(inputStream != null): "inputStream must be non-null";

        OutputStream outputStream = new FileOutputStream(file);
        return IOUtils.copy(inputStream, outputStream);
    }
}
