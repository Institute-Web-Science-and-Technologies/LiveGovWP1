package eu.liveandgov.wp1.backend;

import java.text.SimpleDateFormat;
import java.util.Date;
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
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;

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
	
	public static Date getLocalHelsinkiTime() {
		try {
			SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss Z"); 
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse("http://www.earthtools.org/timezone-1.1/60/25");
			return ft.parse(doc.getElementsByTagName("isotime").item(0).getTextContent());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;		
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();		
		SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
		ft.setTimeZone(TimeZone.getTimeZone( "Europe/Helsinki" ));
		Date d = getLocalHelsinkiTime();
		String ts = ft.format(d.getTime());
		String day = String.format(Locale.US,"%tA", d.getTime()).substring(0,3);
		String json = "{\"vehicles\":[";
		for (VehicleInfo o : getVehicles()) {
			if(json.length() > 13) {
				json += ",\n";
			}
			json += "{";
			json += "\"route_id\":\""+o.getRoute()+"\",";
			json += "\"trip_id\":\""+o.getId()+"\",";
			json += "\"lat\":"+o.getLat()+",";
			json += "\"lon\":"+o.getLon()+",";
			json += "\"ts\":\""+ts+"\",";
			json += "\"day\":\""+day;
			json += "\"}";
		}
		json += "]}";
		out.println(json);
	}
}
