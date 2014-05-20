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
import java.io.PrintWriter;
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
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;

import eu.liveandgov.wp1.HARPipeline;
import eu.liveandgov.wp1.server.har_service.PostgresqlDB;
import eu.liveandgov.wp1.server.har_service.SSFReader;
import eu.liveandgov.wp1.server.har_service.StringProducer;
import static java.lang.System.currentTimeMillis;

/**
 * Servlet implementation class HarService
 */
@WebServlet("/api")
public class HarService extends HttpServlet {
	private static final long serialVersionUID = 1L;
    static final String OUT_DIR = "/srv/liveandgov/HARRawFiles/";
    private static final String FIELD_NAME_UPFILE = "upfile";
    private static Logger Log = Logger.getLogger(HarService.class);
    
    static {
        try {
            SimpleLayout layout = new SimpleLayout();
            FileAppender appender = null;
            appender = new FileAppender(layout,"/var/log/HARService.log",true);
            Logger.getRootLogger().addAppender(appender);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
   
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
		PrintWriter writer = response.getWriter();
        writer.write(
                "<html>" +
                "<h1>HAR Service</h1>" +
                "<form action=\"\" enctype=\"multipart/form-data\" method=\"post\">" +
                "Classify: <input type=\"file\" name=\"upfile\" size=\"40\"/><br/>" +
                "<input type=\"submit\" value=\"Submit\">" +
                "</form>" +
                "</html>"
                );
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Log.info("Incoming POST request from " + request.getRemoteAddr());

		PostgresqlDB db = new PostgresqlDB();
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

        BufferedReader reader;
        if (! isCompressed(request)) {
        		reader = new BufferedReader(new FileReader(outFile));
        } else {
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
        
        // Insert into database
        db.logResult(pr.getID(), stringProducer.getActivity());
        Log.info("Recognized activity " + stringProducer.getActivity() + " for device " + pr.getID());
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
                    continue;
                }
                return item.openStream();
            }
        } catch (FileUploadException e) {
        } catch (IOException e) {
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
