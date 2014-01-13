package eu.liveandgov.wp1.backend;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.json.JSONObject;

public class SSCClient {

	private static final String REQUEST_BASE = "https://testservicecenter.yucat.com/servicecenter/api/";
	// https://mobility.yucat.com/servicecenter/api

	public SSCClient(final String user, final String pass) {
		Authenticator.setDefault(new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(user, pass.toCharArray());
			}
		});
	}

	public enum Status {
		OK, WARNING, CRITICAL, UNKNOWN
	}

	private enum Method {
		GET, POST, PUT, DELETE
	}

	private HttpURLConnection prepareHttpURLConnection(String url, Method method, String contentType)
			throws IOException {
		HttpURLConnection conn = (HttpURLConnection) new URL(REQUEST_BASE + url)
				.openConnection();
		if (method.equals(Method.POST) || method.equals(Method.PUT)) {
			conn.setDoOutput(true);
		}
		conn.setRequestMethod(method.toString());
		conn.setRequestProperty("Content-Type", contentType);
		conn.connect();
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
		HttpURLConnection conn = prepareHttpURLConnection(request, Method.PUT, "application/json");
	
		OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
		out.write(json.toString());
		out.close();
		return readResponse(conn);
	}
	
	private String readResponse(HttpURLConnection conn) throws IOException {
		if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String decodedString;
			String returnVal = "";
			while ((decodedString = in.readLine()) != null) {
				returnVal += decodedString;
			}
			in.close();
			return returnVal;
		}
		else {
			return "code: " + conn.getResponseCode();
		}
	}

	public String postLogFile(String id, String filename) throws IOException {

		String crlf = "\r\n";
		String twoHyphens = "--";
		String boundary =  "---------------------------LIVEANDGOV---";
		
		String request = "diagnostics/log/" + id;
		
		JSONObject json = new JSONObject();
		json.put("filename", filename).put("customerid",1).put("registereduserid",1);
		
		HttpURLConnection conn = prepareHttpURLConnection(request, Method.POST, "multipart/form-data;boundary=" + boundary);

		DataOutputStream out = new DataOutputStream(conn.getOutputStream());
		out.writeBytes(twoHyphens + boundary + crlf);
		out.writeBytes("Content-Type: application/json" + crlf + crlf);
		out.writeBytes(json.toString());		
		out.writeBytes(crlf);
		
		out.writeBytes(twoHyphens + boundary + crlf);
		out.writeBytes("Content-Type: application/text" + crlf + crlf);
		BufferedReader f = new BufferedReader(new FileReader(filename));
		
		  String line;
	      while((line = f.readLine()) != null){
	          out.writeBytes(line);
	      }
		out.writeBytes(twoHyphens + boundary + twoHyphens + crlf);
		out.flush();
		out.close();
		
		out.close();
		if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String decodedString;
			String returnVal = "";
			while ((decodedString = in.readLine()) != null) {
				returnVal += decodedString;
			}
			in.close();
			return returnVal;
		}
		else {
			return "code: " + conn.getResponseCode();
		}

	}
	
	public static void main(String [] args)
	{
		SSCClient c = new SSCClient(args[0], args[1]);
		try {
			System.out.println(c.setHealthCheck("10", Status.OK));
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
