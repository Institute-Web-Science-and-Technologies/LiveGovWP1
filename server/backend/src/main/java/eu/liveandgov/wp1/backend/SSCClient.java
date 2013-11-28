package eu.liveandgov.wp1.backend;

import java.io.IOException;
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

	private HttpURLConnection prepareHttpURLConnection(String url, Method method)
			throws IOException {
		HttpURLConnection conn = (HttpURLConnection) new URL(REQUEST_BASE + url)
				.openConnection();
		if (method.equals(Method.POST) || method.equals(Method.PUT))
			conn.setDoOutput(true);
		conn.setRequestMethod(method.toString());
		conn.connect();
		return conn;
	}

	private String getISO8601UTCDate(Date date) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
		df.setTimeZone(TimeZone.getTimeZone("UTC"));
		return df.format(date);
	}

	public void setHealthCheck(String id, Status status) throws IOException {
		String request = "diagnostics/healthcheck/" + id;
		JSONObject json = new JSONObject();
		json.put("status", status.ordinal()).put("datelastupdate",
				getISO8601UTCDate(new Date()));
		HttpURLConnection conn = prepareHttpURLConnection(request, Method.PUT);
		new OutputStreamWriter(conn.getOutputStream()).write(json.toString());
	}

}
