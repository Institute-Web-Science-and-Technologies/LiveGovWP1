package eu.liveandgov.wp1.backend;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.JSONObject;


@WebServlet("/ServiceLineDetection")
public class ServiceLineDetection extends HttpServlet {
	private static final long serialVersionUID = 1L;
	PostgresqlDatabase db;
	PostgresqlDatabase logDb;
	final String logDBInsert = "INSERT INTO service_sld VALUES (?, ?, ?, ?, ?, ?)";
	
	// route_id -> transportation mean
	final HashMap<String, String> transportationMeans;
	
	static final int realtimeApiToleranceInMinutes = 1;
	static final int realtimeApiToleranceInMeter = 50;
	static final double realtimeApiPreferentialTreatmentScore = 2;
	
	static final int timeTableToleranceInMinutes = 2;
	static final int timeTableToleranceInMeter = 50;
	
	ExecutorService liveApiExecutor;
	Logger mLogger;
	/**
	 * @throws IOException 
	 * @see HttpServlet#HttpServlet()
	 */
	public ServiceLineDetection() throws UnavailableException, IOException {
		super();
		transportationMeans = initTransportationMeans();
		db = new PostgresqlDatabase("liveandgov", "liveandgov");
		logDb = new PostgresqlDatabase("liveandgov", "liveandgov", "liveandgov.uni-koblenz.de", "liveandgov_dev");
		liveApiExecutor = Executors.newCachedThreadPool();
		mLogger = Util.SLDLogger.log();
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
		
		long startTs = System.currentTimeMillis();
		JSONObject logJSON = new JSONObject();
		
		// check if user is valid
		if(!isUsernameValid(request)){
			PrintWriter out = response.getWriter();
			String error = "{\"error\":\"username required\"}";
			out.println(error);
			mLogger.warn(error);
			return;
		}
		ArrayList<LatLonTsDayTuple> coordinates = new ArrayList<LatLonTsDayTuple>();
		// parse data and get the most recent GPS coordinate
		LatLonTsDayTuple latestLatLonTsDayTuple = parseLonLatTsDayTuples(request,coordinates);
		
		logJSON.put("inputCoordinates", latLonArrayToJsonList(coordinates));
		
		// compute the time difference
		// between now and the latest 
		// Helsinki timestamp.
		long timeDiff = System.currentTimeMillis() - latestLatLonTsDayTuple.getTime();
		
		logJSON.put("timeDiffLatestCoordinate", timeDiff);
		
		// check if the most recent timestamp is actual enough to query the real time API
		Future<List<VehicleInfo>> liveApiResult = null;
		if(Math.abs(timeDiff) < realtimeApiToleranceInMinutes*60*1000){
			Callable<List<VehicleInfo>> c = new LiveApiVehiclesNearByCallable( latestLatLonTsDayTuple, realtimeApiToleranceInMeter );
			// this Api call is a HTTP request
			// don't wait for the response
			liveApiResult = liveApiExecutor.submit( c );
			// System.out.println("Using Live API " + timeDiff + " " + latestLatLonTsDayTuple.getUTC());
		}
		
		// build the sql query string to find all routes around the given GPS track
	    String routcodeSelect = createRouteSelectSql(coordinates);
	  
		try {
			Statement stm = db.connection.createStatement();
			
			// execute route query in postgres
			ResultSet rs = stm.executeQuery(routcodeSelect);

			// now, we need the LiveAPI response
			// wait until it arrives 
			List<VehicleInfo> liveVehicles = new ArrayList<VehicleInfo>();
			if(liveApiResult != null) {
				liveVehicles = liveApiResult.get();
			}
			List<JSONObject> allTrips = combineTimetableRoutesWithLiveRoutes(rs, liveVehicles);
			
			// create and send the result json
			JSONObject responseJSON = new JSONObject();
			responseJSON.put("routes", allTrips);			
			response.setContentType("application/json");
			PrintWriter out = response.getWriter();
			out.println(responseJSON.toString());
			
			String username = request.getHeader("username");
			logJSON.put("username",username);
			
			if(!username.equals("test_user")){
				logJSON.put("response",responseJSON);
			}
			logJSON.put("responseTime", System.currentTimeMillis()-startTs);
			mLogger.info(logJSON.toString());
			logResult(allTrips.get(0), username, latestLatLonTsDayTuple.getTime());
			
		} catch (Exception e) {
			e.printStackTrace();
			mLogger.error(e);
		}
	}

	private void logResult (JSONObject data, String username, long ts) {
		PreparedStatement stmt = null;
			try {
				stmt = logDb.connection.prepareStatement(logDBInsert);
				stmt.setString(1, username);
				stmt.setLong(2, ts);
				stmt.setString(3, data.getString("route_id"));
				stmt.setString(4, data.getString("shape_id"));
				stmt.setString(5, data.getString("transportation_mean"));
				stmt.setInt(6, data.getInt("score"));
				stmt.execute();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Util.SLDLogger.log().error(e);
			}
	}
	
	private List<JSONObject> latLonArrayToJsonList(ArrayList<LatLonTsDayTuple> coordinates) {
		List<JSONObject> allTrips = new ArrayList<JSONObject>();
		for(LatLonTsDayTuple t:coordinates){
			allTrips.add(t.getJson());
		}
		return allTrips;
	}

	/**
	 * Combines the database and the LiveAPI results in one ranked result set 
	 * @param rs database response (all trips/routes in the static time table matching the GPS track)
	 * @param liveVehicles LiveAPI response (all trips/routes given by the LiveAPI matching the GPS track)
	 * @return combined and ranked result set
	 * @throws SQLException
	 */
	public List<JSONObject> combineTimetableRoutesWithLiveRoutes(ResultSet rs, List<VehicleInfo> liveVehicles) throws SQLException {
		
		List<JSONObject> allTrips = new ArrayList<JSONObject>();
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
				// 	increase the score of routes also found via live API
				if(v.getRoute().equals(routeId) && v.getDirection() == dir) {
					score *= realtimeApiPreferentialTreatmentScore;
					// remove the route to make sure that the route will not be increased twice
					i.remove();
				}
			}
			trip.put("score", score);
			allTrips.add(trip);
			
			// sort the result set descending by score
			Collections.sort( allTrips, new ScoreComparator());
		}
		
		// append routes not found in the static timetables but via the live API 
		for(VehicleInfo v: liveVehicles){
			JSONObject trip = new JSONObject();
			trip.put("route_id", v.getRoute());
			trip.put("shape_id", v.getRoute() + "_liveApiShape_" + v.getDirection());
			trip.put("trip_id", v.getRoute() + "_liveApi_Tp_" + v.getDirection() + "_" + v.getDeparture());
			trip.put("transportation_mean", transportationMeans.get(v.getRoute()));
			trip.put("score", 1*realtimeApiPreferentialTreatmentScore);
			allTrips.add(trip);
		}
		return allTrips;
	}

	/**
	 * Create the SQL query string to find all trips which match
	 * the given GPS track
	 * @param GPS track
	 * @return SQL select query string
	 */
	public String createRouteSelectSql( ArrayList<LatLonTsDayTuple> coordinates) {
		String routcodeSelect = ""
			+ "SELECT suball.route_id, \n"
			+ "       suball.shape_id, \n"
			+ "       suball.trip_id, \n"
			+ "       Sum(cnt) AS score \n"
			+ "FROM   (";
		  
		  for (int i = 0; i < coordinates.size(); i++) {
			  
			  String betweenTimeClause = coordinates.get(i).getBetweenTimeClause("arrival_time",timeTableToleranceInMinutes);
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
		return routcodeSelect;
	}

	/**
	 * Parses the given GPS track and stores it in the member variable {@code coordinates}
	 * 
	 * @param request
	 * @param result of parsed request
	 * @return the latest {@code LatLonTsDayTuple} (the coordinate with the most recent timestamp)
	 */
	public LatLonTsDayTuple parseLonLatTsDayTuples(HttpServletRequest request, ArrayList<LatLonTsDayTuple> coordinates) {  
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
		return latestLatLonTsDayTuple;
	}

	public boolean isUsernameValid(HttpServletRequest request) throws IOException {
		String username = request.getHeader("username");
		if(username == null){
			return false;
		}
		return true;
	}
	
	/**
	 * Create a look up map for the transportation means of each route
	 * @return routeID -> transportation_mean map
	 */
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
	

	/**
	 * A comparator for
	 * sorting all JSONObjects descending by "score"
	 */
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
