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
import java.io.Reader;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.List;

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
import eu.liveandgov.wp1.backend.SensorValueObjects.RawSensorValue;
import eu.liveandgov.wp1.backend.SensorValueObjects.TagSensorValue;

/**
 * Servlet implementation class UploadServlet2
 * 
 * 
 * Test with:
 * echo "ACC,1378128012707152,id1241242,0.018311 0.117111 0.32142" | curl localhost:8080/backend/upload -F "upfile=@-"
 * 
 * or using the provided test data
 * cat test-upload-data.txt | curl localhost:8080/backend/upload -F "upfile=@-"
 * 
 */
@WebServlet("/upload")
@MultipartConfig
public class UploadServlet extends HttpServlet {
	public static final String OUT_DIR = "/tmp/liveandgov/uploads/";
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public UploadServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setStatus(HttpServletResponse.SC_NOT_IMPLEMENTED);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		Part uploadedFile = null;
		try {
			uploadedFile = request.getPart("upfile");
		} catch (ServletException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		if (uploadedFile == null) {
			// if 'upfile' field not set - return 'Bad Request'
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		
		InputStream uploadedFileInputStream = uploadedFile.getInputStream();

		// copy file to memory in order to make it consumable twice
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		copyStream(uploadedFileInputStream, baos);
		saveToDisk(new ByteArrayInputStream(baos.toByteArray()), request.getHeader("id"));
		
		try {
			saveToDatabase(new ByteArrayInputStream(baos.toByteArray()));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void saveToDisk(InputStream input, String id) throws IOException {
		long unixTime = System.currentTimeMillis() / 1000L;
		String absoluteFilename = OUT_DIR + id + "_" + unixTime;
		File outfile = new File(absoluteFilename);
		OutputStream outstream = new FileOutputStream(outfile);
		copyStream(input, outstream);
		outstream.flush();
		outstream.close();
	}
	
	private void saveToDatabase(InputStream input) throws IOException, UnavailableException, SQLException {
		PostgresqlDatabase db = new PostgresqlDatabase("user", "pass");
		PreparedStatement psAcc, psGPS, psTag;
		Timestamp ts;
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		
		psAcc = db.connection.prepareStatement("INSERT INTO accelerometer VALUES (?, ?, ?, ?, ?)");
		psGPS = db.connection.prepareStatement("INSERT INTO gps VALUES (?, ?, ?, ?)");
		psTag = db.connection.prepareStatement("INSERT INTO tags VALUES (?, ?, ?)");
		
		while (reader.ready()) {
			RawSensorValue rsv = RawSensorValue.fromString(reader.readLine());
			switch (rsv.type) {
			case ACC:
				AccSensorValue asv = AccSensorValue.fromRSV(rsv);
				psAcc.setString(1, asv.id);
				ts = new Timestamp(asv.timestamp * 1000L);
				psAcc.setTimestamp(2, ts);
				psAcc.setFloat(3, asv.x);
				psAcc.setFloat(4, asv.y);
				psAcc.setFloat(5, asv.z);
				psAcc.addBatch();
				break;
			case GPS:
				GPSSensorValue gsv = GPSSensorValue.fromRSV(rsv);
				psGPS.setString(1, gsv.id);
				ts = new Timestamp(gsv.timestamp * 1000L);
				psGPS.setTimestamp(2, ts);
				// TODO
				psGPS.setDouble(3, gsv.latitude);
				psGPS.setDouble(4, gsv.longitude);
				psGPS.addBatch();
				break;
			case TAG:
				TagSensorValue tsv = TagSensorValue.fromRSV(rsv);
				psTag.setString(1, tsv.id);
				ts = new Timestamp(tsv.timestamp * 1000L);
				psTag.setTimestamp(2, ts);
				psTag.setString(3, tsv.tag);
				psTag.addBatch();
				break;
			default:
				break;
			}
		}
		psAcc.executeBatch();
		psGPS.executeBatch();
		psTag.executeBatch();
		psAcc.close();
		psGPS.close();
		psTag.close();
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
