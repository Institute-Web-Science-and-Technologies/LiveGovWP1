package eu.liveandgov.wp1.backend;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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

import org.json.JSONArray;
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
	static final int realtimeApiToleranceInMeter = 10;
	static final double realtimeApiPreferentialTreatmentScore = 2;
	
	static final int timeTableToleranceInMinutes = 2;
	static final int timeTableToleranceInMeter = 10;
	
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
		System.out.println("username: " + username);
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
		// check if the most recent timestamp is actual enough to query the real time API
		if(new Date().getTime() - latestLatLonTsDayTuple.getTime() < realtimeApiToleranceInMinutes*60*1000){
			Callable<List<VehicleInfo>> c = new LiveApiVehiclesNearByCallable( latestLatLonTsDayTuple, realtimeApiToleranceInMeter );
			// this Api call is a HTTP request
			// don't wait for the response
			liveApiResult = liveApiExecutor.submit( c );		
			System.out.println("Using Live API");
		}
		 
	  String routcodeSelect = ""
		+ "SELECT suball.route_id, \n"
		+ "       suball.shape_id, \n"
		+ "       suball.trip_id, \n"
		+ "       Sum(cnt) AS score \n"
		+ "FROM   (";
	  
	  for (int i = 0; i < coordinates.size(); i++) {
		  
		  String p = coordinates.get(i).getLonLatPoint();
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
	  System.out.println(routcodeSelect);
	  
		try {
			Statement stm = db.connection.createStatement();
			ResultSet rs = stm.executeQuery(routcodeSelect);
			
			response.setContentType("application/json");
			
			

		PrintWriter out = response.getWriter();
		
		JSONObject responseJSON = new JSONObject();
		
		JSONArray routes = new JSONArray();
		
		String json = "{\"routes\":[";
	
		// now, we need the LiveAPI response
		// wait until it arrives 
		List<VehicleInfo> liveVehicles = liveApiResult.get();
		
		if(liveVehicles.size() != 0){
			
		}
		else {		
			while (rs.next()) {
				JSONObject route = new JSONObject();
				String routeId = rs.getString(1);
				route.put("route_id", routeId);
				route.put("shape_id", rs.getString(2));
				String tripId = rs.getString(3);
				int dir = Integer.getInteger(tripId.substring(tripId.length() - 1)); 
				route.put("trip_id", tripId);
				route.put("transportation_mean", transportationMeans.get(rs.getString(1)));
				
				Iterator<VehicleInfo> i = liveVehicles.iterator();
				while (i.hasNext()) {
					VehicleInfo v = i.next();
					if(v.getRoute().equals(routeId) && v.getDirection() == dir) {
						route.put("score", rs.getInt(4) + realtimeApiPreferentialTreatmentScore);
						i.remove();
					}
				}
				
				route.put("score", rs.getInt(4));
				
				json += "{";
				json += "\"route_id\":\""+rs.getString(1)+"\",";
				json += "\"shape_id\":\""+rs.getString(2)+"\",";
				json += "\"trip_id\":\""+rs.getString(3)+"\",";
				json += "\"transportation_mean\":\""+transportationMeans.get(rs.getString(1))+"\",";
				json += "\"score\":"+rs.getInt(4);
				json += "}";
			}
		}
		json += "]}";
			out.println(json);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (UnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new HashMap<String, String>();
	}
}
