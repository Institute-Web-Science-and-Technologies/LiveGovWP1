package eu.liveandgov.wp1.backend;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/LiveAPI")
public class LiveAPI extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// http://dl.dropboxusercontent.com/u/20567085/Mattersoft%20Live!%20interface%20description%20v1_6.pdf
	private static final String REQUEST = "http://83.145.232.209:10001/?type=vehicles&lng1=20&lat1=60&lng2=30&lat2=70&online=1";

	public static List<VehicleInfo> getVehicles() throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(new URL(
				REQUEST).openStream()));

		String line;
		LinkedList<VehicleInfo> list = new LinkedList<VehicleInfo>();
		while ((line = in.readLine()) != null) {
			String[] fields = line.split(";");
			list.add(new VehicleInfo(fields));
		}

		return list;
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		
		SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
		
		Calendar gmt = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		long gmtTime = gmt.getTime().getTime();

		long timezoneAlteredTime = gmtTime + TimeZone.getTimeZone("Europe/Helsinki").getRawOffset();
		Calendar helsinkiCalendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Helsinki"));
		helsinkiCalendar.setTimeInMillis(timezoneAlteredTime);
		
		String ts = ft.format(helsinkiCalendar.getTime());
		String day = String.format(Locale.US,"%tA", helsinkiCalendar.getTime() );
		String json = "{\"vehicles\":[";
		for (VehicleInfo o : getVehicles()) {
			if(json.length() > 13) {
				json += ",\n";
			}
			json += "{";
			json += "\"route_id\":\""+o.getId()+"\",";
			json += "\"lat\":"+o.getLat()+",";
			json += "\"lon\":"+o.getLon()+",";
			json += "\"ts\":\""+ts+"\",";
			json += "\"score\":\""+day;
			json += "\"}";
		}
		json += "]}";
		out.println(json);
	}
}
