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
	public static final String OUT_DIR = "/srv/liveandgov/UploadServletRawFiles";

	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public UploadServlet() {
		super();
		// TODO Auto-generated constructor stub
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
     * - write data to db
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
            String fileName = request.getHeader("id") + "_" + unixTime;
            File savedFile = saveToDisk(new ByteArrayInputStream(baos.toByteArray()), fileName );

            // Validation
            checkFile(request, savedFile);

            // save data to db
			saveToDatabase(new ByteArrayInputStream(baos.toByteArray()));

        } catch (IOException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

        } catch (IllegalArgumentException e) {
            // raised if validation Failed
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
        return;
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
		String absoluteFilename = OUT_DIR + fileName;
		File outfile = new File(absoluteFilename);
		OutputStream outstream = new FileOutputStream(outfile);
		copyStream(input, outstream);
		outstream.flush();
		outstream.close();
        return outfile;
	}

	private void saveToDatabase(InputStream input) throws IOException {
        try {
		PostgresqlDatabase db = new PostgresqlDatabase("liveandgov", "liveandgov");
		PreparedStatement psAcc, psGPS, psTag, psAct, psLac, psGra;
		Timestamp ts;
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));

		psAcc = db.connection
				.prepareStatement("INSERT INTO accelerometer VALUES (?, ?, ?, ?, ?)");
		psGPS = db.connection
				.prepareStatement("INSERT INTO gps VALUES (?, ?, ST_GeomFromText(?,4326))");
		psTag = db.connection
				.prepareStatement("INSERT INTO tags VALUES (?, ?, ?)");
		psAct = db.connection
				.prepareStatement("INSERT INTO google_activity VALUES (?, ?, ?)");
		psLac = db.connection
				.prepareStatement("INSERT INTO linear_acceleration VALUES (?, ?, ?, ?, ?)");
		psGra = db.connection
				.prepareStatement("INSERT INTO gravity VALUES (?, ?, ?, ?, ?)");

        String line;
		while (reader.ready()) {
            line = reader.readLine();
            RawSensorValue rsv;
            try {
			    rsv = RawSensorValue.fromString(line);
            }
            catch (Exception e) {
                System.err.println("Error parsing line: " + line);
                continue;
            }

			switch (rsv.type) {
			case ACC:
				AccSensorValue asv = AccSensorValue.fromRSV(rsv);
				psAcc.setString(1, asv.id);
				ts = new Timestamp(asv.timestamp);
				psAcc.setTimestamp(2, ts);
				psAcc.setFloat(3, asv.x);
				psAcc.setFloat(4, asv.y);
				psAcc.setFloat(5, asv.z);
				psAcc.addBatch();
				break;
			case GPS:
				GPSSensorValue gsv = GPSSensorValue.fromRSV(rsv);
				psGPS.setString(1, gsv.id);
				ts = new Timestamp(gsv.timestamp);
				psGPS.setTimestamp(2, ts);
				psGPS.setString(3, "POINT(" + Double.toString(gsv.longitude)
						+ ' ' + Double.toString(gsv.latitude) + ")");

				psGPS.addBatch();
				break;
			case TAG:
				TagSensorValue tsv = TagSensorValue.fromRSV(rsv);
				psTag.setString(1, tsv.id);
				ts = new Timestamp(tsv.timestamp);
				psTag.setTimestamp(2, ts);
				psTag.setString(3, tsv.tag);
				psTag.addBatch();
				break;
			case ACT:
				GoogleActivitySensorValue gasv = GoogleActivitySensorValue.fromRSV(rsv);
				psAct.setString(1, gasv.id);
				ts = new Timestamp(gasv.timestamp);
				psAct.setTimestamp(2, ts);
				psAct.setString(3, gasv.activity);
				psAct.addBatch();
				break;
			case LAC:
				LacSensorValue lasv = LacSensorValue.fromRSV(rsv);
				psLac.setString(1, lasv.id);
				ts = new Timestamp(lasv.timestamp);
				psLac.setTimestamp(2, ts);
				psLac.setFloat(3, lasv.x);
				psLac.setFloat(4, lasv.y);
				psLac.setFloat(5, lasv.z);
				psLac.addBatch();
				break;
			case GRA:
				GraSensorValue grasv = GraSensorValue.fromRSV(rsv);
				psGra.setString(1, grasv.id);
				ts = new Timestamp(grasv.timestamp);
				psGra.setTimestamp(2, ts);
				psGra.setFloat(3, grasv.x);
				psGra.setFloat(4, grasv.y);
				psGra.setFloat(5, grasv.z);
				psGra.addBatch();
				break;
			default:
				break;
			}
		}

		psAcc.executeBatch();
		psGPS.executeBatch();
		psTag.executeBatch();
		psAct.executeBatch();
		psLac.executeBatch();
		psGra.executeBatch();
		psAcc.close();
		psGPS.close();
		psTag.close();
		psAct.close();
		psLac.close();
		psGra.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new IOException(e);
        } catch (UnavailableException e) {
            e.printStackTrace();
            throw new IOException(e);
        }
	}

	private void copyStream(InputStream input, OutputStream output)
			throws IOException {
		byte[] buffer = new byte[1024];
		int bytesRead;
		while ((bytesRead = input.read(buffer)) != -1) {
			output.write(buffer, 0, bytesRead);
		}
	}

}
