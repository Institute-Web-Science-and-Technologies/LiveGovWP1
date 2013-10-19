package eu.liveandgov.wp1.backend;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import eu.liveandgov.wp1.backend.SensorValueObjects.AccSensorValue;
import eu.liveandgov.wp1.backend.SensorValueObjects.GPSSensorValue;
import eu.liveandgov.wp1.backend.SensorValueObjects.GoogleActivitySensorValue;
import eu.liveandgov.wp1.backend.SensorValueObjects.GraSensorValue;
import eu.liveandgov.wp1.backend.SensorValueObjects.LacSensorValue;
import eu.liveandgov.wp1.backend.SensorValueObjects.RawSensorValue;
import eu.liveandgov.wp1.backend.SensorValueObjects.TagSensorValue;


/**
 * Servlet implementation class UploadServlet2
 *
 *
 * Test with: echo "ACC,1378128012707152,id1241242,0.018311 0.117111 0.32142" |
 * curl localhost:8080/backend/upload -F "upfile=@-"
 *
 * or using the provided test data cat test-upload-data.txt | curl
 * localhost:8080/backend/upload -F "upfile=@-"
 *
 */
@WebServlet("/upload")
@MultipartConfig
public class UploadServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public UploadServlet() {
		super();
	}

	/**
	 * Return simple HTML response.
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
        response.getWriter().write("<h1>Live+Gov UploadServlet.</h1> <p>Please upload file via POST.</p>");
        response.setStatus(HttpServletResponse.SC_OK);
	}

	/**
	 * Receive sensor data in .ssf format:
     * - write data to disk
     * - check if data is written to disk? -> Send response.
     */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        try {
            // get input stream from MultiPart Form part
	    	InputStream uploadedFileInputStream = getInputStreamFromRequest(request, "upfile");

    		// copy file to memory in order to make it consumable twice
    		ByteArrayOutputStream baos = new ByteArrayOutputStream();
    		copyStream(uploadedFileInputStream, baos);

            // write data to disk
            long unixTime = System.currentTimeMillis() / 1000L;
            String fileName = request.getHeader("ID") + "_" + unixTime;
            File savedFile = saveToDisk(new ByteArrayInputStream(baos.toByteArray()), fileName );

            // Validation
            checkFile(request, savedFile);

            // everything went fine
            response.setStatus(HttpServletResponse.SC_OK);

        } catch (IOException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } catch (IllegalArgumentException e) {
            // raised if validation Failed
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private void checkFile(HttpServletRequest request, File savedFile) throws IllegalArgumentException, IOException {
        long checksumProvided;

        try {
            checksumProvided = Long.parseLong(request.getHeader("CHECKSUM"));
        } catch (NumberFormatException e){
            System.err.println("CHECKSUM not  provided.");
            return; // pass check
        }

        long checksumComputed = getChecksum(savedFile);

        System.out.println("ChecksumProvided: " + checksumProvided);
        System.out.println("ChecksumComputed: " + checksumComputed);

        if (checksumProvided != checksumComputed )  {
            throw new IllegalArgumentException("Checksum error. Provided " + checksumProvided +
                " but computed " + checksumComputed );
        } else {
            System.out.println("Checksum test passed");
        }
    }

    private long getChecksum(File savedFile) {
        return savedFile.length();
    }

    private InputStream getInputStreamFromRequest(HttpServletRequest request,
                                                  String partName) throws IOException {
        try {
            Part uploadedFile = null;
            InputStream out = null;
            uploadedFile = request.getPart(partName);
            return uploadedFile.getInputStream();
        } catch (ServletException e){
            throw new IOException(e);
        }
    }

    private File saveToDisk(InputStream input, String fileName) throws IOException {
		File outfile = new File(Configuration.OUT_DIR, fileName);
		OutputStream outstream = new FileOutputStream(outfile);
        try {
    		copyStream(input, outstream);
        } finally {
            input.close();
            outstream.flush();
            outstream.close();
        }
        return outfile;
	}

	private void copyStream(InputStream input, OutputStream output)	throws IOException {
        // http://stackoverflow.com/questions/43157/easy-way-to-write-contents-of-a-java-inputstream-to-an-outputstream/43168#43168
        final int BUF_SIZE = 1024*100;
		byte[] buffer = new byte[BUF_SIZE];
		int bytesRead;

		while ((bytesRead = input.read(buffer)) != -1) {
			output.write(buffer, 0, bytesRead);
		}
	}

}
