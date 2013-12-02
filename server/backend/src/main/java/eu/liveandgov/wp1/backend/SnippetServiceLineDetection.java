package eu.liveandgov.wp1.backend;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

/**
 * Servlet implementation class InspectionServlet
 */
@WebServlet("/ServiceLineDetection")
public class SnippetServiceLineDetection extends HttpServlet {
	private static final long serialVersionUID = 1L;
	PostgresqlDatabase db;
	ArrayList<LatLonTsDayTuple> coordinates;
	
	// route_id -> transportation mean
	HashMap<String, String> transportationMeans;
	
	static final int realtimeApiToleranceInMinutes = 1;
	static final int realtimeApiToleranceInMeter = 50;
	static final double realtimeApiPreferentialTreatmentScore = 2;
	
	static final int timeTableToleranceInMinutes = 2;
	static final int timeTableToleranceInMeter = 50;
	
	ExecutorService liveApiExecutor;
	Future<List<VehicleInfo>> liveApiResult;

	/**

	 * @see HttpServlet#HttpServlet()
	 */
	public SnippetServiceLineDetection() throws UnavailableException {
		super();
		transportationMeans = initTransportationMeans();
		db = new PostgresqlDatabase("liveandgov", "liveandgov");
		liveApiExecutor = Executors.newCachedThreadPool();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String username = request.getHeader("username");
		if(username == null){
			PrintWriter out = response.getWriter();
			out.println("{\"error\":\"username required\"}");
			return;
		}
		coordinates = new ArrayList<LatLonTsDayTuple>();  
		String line = null;
		LatLonTsDayTuple latestLatLonTsDayTuple = new LatLonTsDayTuple();
		try {
		    BufferedReader reader = request.getReader();

			while ((line = reader.readLine()) != null) {
				LatLonTsDayTuple t = new LatLonTsDayTuple(line);
				if(t.getTime() > latestLatLonTsDayTuple.getTime()){
					// store the most recent timestamp
					latestLatLonTsDayTuple = t;
				}
				coordinates.add(t);
			}
		  } catch (Exception e) { 
			  e.getMessage();
        }
		
		// The reason for this weird line is that 
		// "new Date()" uses the local timezone of the 
		// machine the code is running on. But we need
		// a "new Date()" from Helsinki to compute the
		// time difference between now and the latest 
		// Helsinki timestamp.
		long timeDiff = LiveAPI.getSimpleDateStringAsDate(LiveAPI.getHelsinkiDateAsSimpleDateString()).getTime() - latestLatLonTsDayTuple.getTime();
		// check if the most recent timestamp is actual enough to query the real time API
		if(Math.abs(timeDiff) < realtimeApiToleranceInMinutes*60*1000){
			Callable<List<VehicleInfo>> c = new LiveApiVehiclesNearByCallable( latestLatLonTsDayTuple, realtimeApiToleranceInMeter );
			// this Api call is a HTTP request
			// don't wait for the response
			liveApiResult = liveApiExecutor.submit( c );
			// System.out.println("Using Live API " + timeDiff + " " + latestLatLonTsDayTuple.getUTC());
		}
		 
	  String routcodeSelect = ""
		+ "SELECT suball.route_id, \n"
		+ "       suball.shape_id, \n"
		+ "       suball.trip_id, \n"
		+ "       Sum(cnt) AS score \n"
		+ "FROM   (";
	  
	  for (int i = 0; i < coordinates.size(); i++) {
		  
		  String betweenTimeClause = coordinates.get(i).getBetweenTimeClause2("arrival_time",timeTableToleranceInMinutes);
		  String d = coordinates.get(i).getISO8601Date();
		  String day = coordinates.get(i).getWeekdayName();
		  String bb = coordinates.get(i).getBoundingBox(timeTableToleranceInMeter);
	
		  routcodeSelect += i>0?"        UNION ALL \n        ":"";
		  routcodeSelect += ""
						+ "(SELECT route_id, \n"
						+ "                shape_id, \n"
						+ "                trip_id, \n"
						+ "                Count(*) AS cnt \n"
						+ "         FROM   snippets_"+day+" \n"
						+ "         WHERE  geom && "+bb+" \n"
						+ "                AND "+betweenTimeClause+" \n"
	
						+ "             -- AND DATE '"+d+"' BETWEEN calendar.start_date AND calendar.end_date \n"
						+ "         GROUP  BY route_id, \n"
						+ "                   trip_id, \n"
						+ "                   shape_id) \n";
	  }
	  routcodeSelect += ") AS suball \n"
		  + "GROUP  BY suball.route_id, \n"
		  + "          suball.trip_id, \n"
		  + "          suball.shape_id \n"
		  + "ORDER  BY score DESC "
		  + "LIMIT 10;";
	  
		try {
			Statement stm = db.connection.createStatement();
			ResultSet rs = stm.executeQuery(routcodeSelect);

		List<JSONObject> allTrips = new ArrayList<JSONObject>();
		// now, we need the LiveAPI response
		// wait until it arrives 
		List<VehicleInfo> liveVehicles = new ArrayList<VehicleInfo>();
		if(liveApiResult != null) {
			liveVehicles = liveApiResult.get();
		}
		while (rs.next()) {
			JSONObject trip = new JSONObject();
			String routeId = rs.getString(1);
			trip.put("route_id", routeId);
			String shapeId = rs.getString(2);

			int dir = Integer.parseInt(shapeId.substring(shapeId.length() - 1)); 
			trip.put("shape_id", shapeId);

			trip.put("trip_id", rs.getString(3));
			trip.put("transportation_mean", transportationMeans.get(rs.getString(1)));
			
			Iterator<VehicleInfo> i = liveVehicles.iterator();
			int score = rs.getInt(4);
			while (i.hasNext()) {
				VehicleInfo v = i.next();
				if(v.getRoute().equals(routeId) && v.getDirection() == dir) {
					score *= realtimeApiPreferentialTreatmentScore;
					i.remove();
				}
			}
			trip.put("score", score);
			allTrips.add(trip);
		}
		for(VehicleInfo v: liveVehicles){
			JSONObject trip = new JSONObject();
			trip.put("route_id", v.getRoute());
			trip.put("shape_id", v.getRoute() + "_liveApiShape_" + v.getDirection());
			trip.put("trip_id", v.getRoute() + "_liveApi_Tp_" + v.getDirection() + "_" + v.getDeparture());
			trip.put("transportation_mean", transportationMeans.get(v.getRoute()));
			trip.put("score", 1*realtimeApiPreferentialTreatmentScore);
			allTrips.add(trip);
		}
		
		Collections.sort( allTrips, new ScoreComparator());
		JSONObject responseJSON = new JSONObject();
		responseJSON.put("routes", allTrips);
		
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();		

			out.println(responseJSON.toString());
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}
	
	// load the routeID -> transportation_mean map into RAM
	public static HashMap<String, String> initTransportationMeans(){
		String selectMeans = ""
				+ "SELECT routes.route_id, "
				+ "       route_type.route_type_name "
				+ "FROM   routes, "
				+ "       route_type "
				+ "WHERE  routes.route_type = route_type.route_type";
		try {
			PostgresqlDatabase db;
			db = new PostgresqlDatabase("liveandgov", "liveandgov");
			Statement stm = db.connection.createStatement();
			ResultSet rs = stm.executeQuery(selectMeans);
			HashMap<String, String> transportationMeansMap = new HashMap<String, String>();
		while (rs.next()) {
			transportationMeansMap.put(rs.getString(1),rs.getString(2));
		}
		return transportationMeansMap;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		catch (UnavailableException e) {
			e.printStackTrace();
		}
		return new HashMap<String, String>();
	}
	
	// sort all JSONObjects descending by "score"
	class ScoreComparator implements Comparator<JSONObject>	{
	    public int compare(JSONObject a, JSONObject b)
	    {
	        int scoreA = a.getInt("score");
	        int scoreB = b.getInt("score");

	        if(scoreA > scoreB)
	            return -1;
	        if(scoreA < scoreB)
	            return 1;
	        return 0;    
	    }
	}
}
