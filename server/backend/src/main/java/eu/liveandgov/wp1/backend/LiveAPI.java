package eu.liveandgov.wp1.backend;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import java.util.concurrent.Callable;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

@WebServlet("/LiveAPI")
public class LiveAPI extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static final String REQUEST = "http://83.145.232.209:10001/?type=vehicles&lng1=20&lat1=60&lng2=30&lat2=70&online=1";
	
	// TODO refactor: in this class we only need the route_ids without transportation means
	// route_id -> transportation mean
	static HashMap<String, String> serviceLineWhiteList;
	
	public LiveAPI() {
		super();
		// The live API reply contains vehicles which are not
		// in our database. Therefore, our LiveAPI wrapper  
		// forwards only vehicles found on the white list.
		serviceLineWhiteList = ServiceLineDetection.initTransportationMeans();
	}
	
	/**
	 * 
	 * @return A List of vehicles currently traveling in Helsinki
	 * @throws IOException
	 */
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
		
		// current time in Helsinki
		Date d = new Date();
		String ts = getHelsinkiDateAsSimpleDateString(d);
		
		// current day in Helsinki
		String day = getHelsinkiDayString(d);
		
		List<JSONObject> allTrips = new ArrayList<JSONObject>();
		for (VehicleInfo o : getVehicles()) {
			JSONObject trip = new JSONObject(); 
			trip.put("route_id",o.getRoute());
			trip.put("trip_id",o.getId());
			trip.put("lat",o.getLat());
			trip.put("lon",o.getLon());
			trip.put("ts",ts);
			trip.put("day",day);
			trip.put("dir",o.getDirection());
			trip.put("departure",o.getDeparture());
			allTrips.add(trip);
		}
		JSONObject responseJSON = new JSONObject();
		responseJSON.put("vehicles", allTrips);
		
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();	
		out.println(responseJSON.toString());
	}

	public static String getHelsinkiDayString(Date d) {
		SimpleDateFormat ft = new SimpleDateFormat ("EEE", Locale.US);
		ft.setTimeZone(TimeZone.getTimeZone( "Europe/Helsinki" ));
		return ft.format(d);
	}

	/**
	 * @return current time in Helsinki as String yyyy-MM-dd HH:mm:ss
	 */
	public static String getHelsinkiDateAsSimpleDateString(Date d) {
		SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss", Locale.US);
		ft.setTimeZone(TimeZone.getTimeZone( "Europe/Helsinki" ));
		return ft.format(d);
	}

    /**
     * 
     * @param latLonTsDayTuple origin where you are 
     * @param toleranceInMeter radius in which you search
     * @return all LiveAPI vehicles with a distance less than {@code toleranceInMeter} from {@code latLonTsDayTuple}
     */
	public static List<VehicleInfo> getVehiclesNearBy(LatLonTsDayTuple latLonTsDayTuple, int toleranceInMeter) {
		List<VehicleInfo> returnList = new LinkedList<VehicleInfo>();
		try {
			for (VehicleInfo o : getVehicles()) {
				double distanceInMeter = DivideAndConquerGtfs.haversineInMeter(o.getLat(), o.getLon(), 
						latLonTsDayTuple.getLat(), latLonTsDayTuple.getLon());
				if(distanceInMeter < toleranceInMeter) {
					returnList.add(o);	
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			Util.SLDLogger.log().error(e);
		}
		return returnList;
	}
}

/**
 *  A wrapper for asynchronous calls of {@code getVehiclesNearBy}
 */
class LiveApiVehiclesNearByCallable implements Callable<List<VehicleInfo>>
{
  private final LatLonTsDayTuple latLonTsDayTuple;
  private final int toleranceInMeter;
  LiveApiVehiclesNearByCallable( LatLonTsDayTuple latLonTsDayTuple, int toleranceInMeter ){
    this.latLonTsDayTuple = latLonTsDayTuple;
    this.toleranceInMeter = toleranceInMeter;
  }
  @Override public List<VehicleInfo> call()
  {
    return LiveAPI.getVehiclesNearBy(latLonTsDayTuple, toleranceInMeter);
  }
}
