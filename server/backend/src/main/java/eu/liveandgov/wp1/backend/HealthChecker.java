package eu.liveandgov.wp1.backend;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import org.apache.commons.codec.binary.Base64;

import org.json.JSONObject;

public class HealthChecker {

	private static final String SERVICE_CENTER_URL = "https://testservicecenter.yucat.com/servicecenter/api/";
	private static final String SLD_API_URL = "http://mobile-sensing.west.uni-koblenz.de:8080/backend/ServiceLineDetection";
//	private static final String SERVICE_CENTER_URL = "http://localhost:8080/";
	// https://mobility.yucat.com/servicecenter/api
	
	private static final String EXPECTED_SLD_RESPONSE = "{\"routes\":[{\"trip_id\":\"1074_20130930_Ti_1_1812\",\"route_id\":\"1074\",\"score\":16,\"transportation_mean\":\"Bus\",\"shape_id\":\"1074_20130812_1\"},{\"trip_id\":\"1072_20130930_Ti_2_1735\",\"route_id\":\"1072\",\"score\":14,\"transportation_mean\":\"Bus\",\"shape_id\":\"1072_20130812_2\"},{\"trip_id\":\"4732_20130930_Ti_2_1735\",\"route_id\":\"4732\",\"score\":14,\"transportation_mean\":\"Bus\",\"shape_id\":\"4732_20130812_2\"},{\"trip_id\":\"1064_20130930_Ti_1_1808\",\"route_id\":\"1064\",\"score\":14,\"transportation_mean\":\"Bus\",\"shape_id\":\"1064_20110815_1\"},{\"trip_id\":\"1071_20130930_Ti_1_1805\",\"route_id\":\"1071\",\"score\":14,\"transportation_mean\":\"Bus\",\"shape_id\":\"1071_20121203_1\"},{\"trip_id\":\"1077_20130930_Ti_1_1808\",\"route_id\":\"1077\",\"score\":14,\"transportation_mean\":\"Bus\",\"shape_id\":\"1077_20130812_1\"},{\"trip_id\":\"1075_20130930_Ti_2_1738\",\"route_id\":\"1075\",\"score\":14,\"transportation_mean\":\"Bus\",\"shape_id\":\"1075_20130919_2\"},{\"trip_id\":\"9633_20130930_Ti_2_1717\",\"route_id\":\"9633\",\"score\":14,\"transportation_mean\":\"Bus\",\"shape_id\":\"9633_20130812_2\"},{\"trip_id\":\"1067_20130930_Ti_2_1739\",\"route_id\":\"1067\",\"score\":14,\"transportation_mean\":\"Bus\",\"shape_id\":\"1067_20130828_2\"},{\"trip_id\":\"1067_20130930_Ti_1_1806\",\"route_id\":\"1067\",\"score\":14,\"transportation_mean\":\"Bus\",\"shape_id\":\"1067_20130828_1\"}]}";
	private static final String LOG_DIR = "/var/log/";
	private static final int HEALTH_CHECK_INTERVAL_IN_SEC = 180;
	private static final int POST_LOG_FILES_INTERVAL_IN_SEC = 24*60*60;
	static final String crlf = "\r\n";
	static final String twoHyphens = "--";
	static final String boundary =  "---------------------------LIVEANDGOV---";
	
	private String authStringBase64;
	private int lastLineCommited = 0;
	private String lastCommitTimestamp = "2013-12-06T00:00Z";
	private String lastHealthCheckTimestamp = "2013-12-06T00:00Z";

	public HealthChecker(final String user, final String pass) {
		String authString = user + ":" + pass;
		byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
		authStringBase64 = new String(authEncBytes);
	}

	public enum Status {
		OK, WARNING, CRITICAL, UNKNOWN
	}

	private enum Method {
		GET, POST, PUT, DELETE
	}

	private HttpURLConnection prepareHttpURLConnection(String url, Method method, String contentType)
			throws IOException {
		HttpURLConnection conn = (HttpURLConnection) new URL(url)
				.openConnection();
		if (method.equals(Method.POST) || method.equals(Method.PUT)) {
			conn.setDoOutput(true);
		}
		conn.setRequestMethod(method.toString());
		conn.setRequestProperty("Authorization", "Basic " + authStringBase64);
		conn.setRequestProperty("Content-Type", contentType);
		conn.setUseCaches(false);
		return conn;
	}

	private String getISO8601UTCDate(Date date) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
		df.setTimeZone(TimeZone.getTimeZone("UTC"));
		return df.format(date);
	}

	public String setHealthCheck(String id, Status status) throws IOException {
		String request = "diagnostics/healthcheck/" + id;
		JSONObject json = new JSONObject();
		json.put("status", status.ordinal()).put("datelastupdate",
				getISO8601UTCDate(new Date()));
		HttpURLConnection conn = prepareHttpURLConnection(SERVICE_CENTER_URL + request, Method.PUT, "application/json");
		conn.connect();
		
		OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
		out.write(json.toString());
		out.close();
		return readResponse(conn);
	}
	
	private String readResponse(HttpURLConnection conn) throws IOException {
		if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			String returnVal = "";
			while ((line = in.readLine()) != null) {
				returnVal += line;
			}
			in.close();
			return returnVal;
		}
		else {
			return "code: " + conn.getResponseCode();
		}
	}

	public String postLogFile(String id, String filename) throws IOException {
		
		String request = "diagnostics/log/" + id;
		String now = getISO8601UTCDate(new Date());
		JSONObject json = new JSONObject();
		json.put("filename", filename)
		    .put("customerid",4)
		    .put("registereduserid",6)
		    .put("startdate", lastCommitTimestamp)
		    .put("enddate", now);
		
		lastCommitTimestamp = now;
		
		HttpURLConnection conn = prepareHttpURLConnection(SERVICE_CENTER_URL + request, Method.POST, "multipart/form-data; boundary=" + boundary);
		conn.connect();
		
		DataOutputStream out = new DataOutputStream(conn.getOutputStream());
		out.writeBytes(twoHyphens + boundary + crlf);
		out.writeBytes("Content-Type: application/json" + crlf + crlf);
		out.writeBytes(json.toString());		

		out.writeBytes(crlf + twoHyphens + boundary + crlf);
		out.writeBytes("Content-Type: application/text" + crlf + crlf);
		BufferedReader f = new BufferedReader(new FileReader(LOG_DIR + filename));
		
		  String line;
		  int pos = 0;
	      while((line = f.readLine()) != null){
	    	  if(pos < lastLineCommited) {
	    		  pos++;
	    		  continue;
	    	  }
	          out.writeBytes(line + "\n");
	          pos++;
	      }
	      f.close();
		out.writeBytes( crlf + twoHyphens + boundary + twoHyphens + crlf);
		out.flush();
		out.close();
		
		String returnVal;
		if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String decodedString;
			returnVal = "Posted " + (pos - lastLineCommited) + " lines. Cursor at position " + pos + ". Service center response: ";
			while ((decodedString = in.readLine()) != null) {
				returnVal += decodedString;
			}
			in.close();
		}
		else {
			returnVal = "Code: " + conn.getResponseCode() + ". Failed to commit " + (pos - lastLineCommited) + " lines. Cursor at position " + pos + ".";
		}
		lastLineCommited = pos; // Even if the commit failed, don't send this lines again. 
		return returnVal;
	}
	public Status checkSLD() throws IOException {
		HttpURLConnection conn = prepareHttpURLConnection(SLD_API_URL, Method.POST, "application/x-www-form-urlencoded");
		conn.addRequestProperty("username", "test_user");
		conn.connect();
		OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
		out.write("60.180693,24.952174000000014,2014-01-14 18:12:14,Tue\n");
		out.close();
		String response = readResponse(conn);
		if(response.equals(EXPECTED_SLD_RESPONSE)) {
			return Status.OK;
		}
		else {
			System.out.println("SLD NOK!!!");
			return Status.CRITICAL;
		}
		
	}
	

	public static void main(String [] args)
	{
		// simple thread wrapper for health checks
		class SetHealthCheckThread extends Thread {
			HealthChecker c;
			public SetHealthCheckThread(HealthChecker c) {
		        this.c = c;
		    }
		    public void run() {
		        for (;;) {
		            try {
		            	c.lastHealthCheckTimestamp = c.getISO8601UTCDate(new Date());
		            	System.out.println(c.lastHealthCheckTimestamp + " Health check. Service center response: " + c.setHealthCheck("10", c.checkSLD()));
		                sleep(HEALTH_CHECK_INTERVAL_IN_SEC * 1000);
		            } catch (InterruptedException e) {} catch (IOException e) {
						e.printStackTrace();
					}
		        }
		    }
		}
		
		// simple thread wrapper for log files
		class PostLogFileThread extends Thread {
			HealthChecker c;
			public PostLogFileThread(HealthChecker c) {
		        this.c = c;
		    }
		    public void run() {
		        for (;;) {
		            try {
		            	System.out.println(c.lastCommitTimestamp + " Log file post. " + c.postLogFile("13", "ServiceLineDetection.log"));
		                sleep(POST_LOG_FILES_INTERVAL_IN_SEC * 1000);
		            } catch (InterruptedException e) {} catch (IOException e) {
						e.printStackTrace();
					}
		        }
		    }
		}
		
		HealthChecker c = new HealthChecker(args[0], args[1]);
		new SetHealthCheckThread(c).start();
		new PostLogFileThread(c).start();
	}
}
