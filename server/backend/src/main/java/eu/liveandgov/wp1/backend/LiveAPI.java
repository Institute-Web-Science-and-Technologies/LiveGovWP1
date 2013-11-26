package eu.liveandgov.wp1.backend;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
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
import javax.servlet.UnavailableException;
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
	
	// TODO refactor: in this class we only need the route_ids without transportation means
	// route_id -> transportation mean
	static HashMap<String, String> serviceLineWhiteList;
	
	public LiveAPI() {
		super();
		serviceLineWhiteList = SnippetServiceLineDetection.initTransportationMeans();
	}
	
	
	private static List<VehicleInfo> getVehicles() throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(new URL(
				REQUEST).openStream()));

		String line;
		LinkedList<VehicleInfo> list = new LinkedList<VehicleInfo>();
		while ((line = in.readLine()) != null) {
			String[] fields = line.split(";");
			VehicleInfo vi = new VehicleInfo(fields);
			if(serviceLineWhiteList.containsKey(vi.getRoute())){
							list.add(new VehicleInfo(fields));
			}
		}

		return list;
	}
	
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();		
		SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
		ft.setTimeZone(TimeZone.getTimeZone( "Europe/Helsinki" ));
		Date d = new Date();
		String ts = ft.format(d);
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


	public static List<VehicleInfo> getVehiclesNearBy(LatLonTsDayTuple latLonTsDayTuple, int toleranceInMeter) {
		List<VehicleInfo> returnList = new LinkedList<VehicleInfo>();
		try {
			for (VehicleInfo o : getVehicles()) {
				if(DivideAndConquerGtfs.haversineInMeter(o.getLat(), o.getLon(), 
						latLonTsDayTuple.getLat(), latLonTsDayTuple.getLon()) < toleranceInMeter) {
					returnList.add(o);	
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return returnList;
	}
}
