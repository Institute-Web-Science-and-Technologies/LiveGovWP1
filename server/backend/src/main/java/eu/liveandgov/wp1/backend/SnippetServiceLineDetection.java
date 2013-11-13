package eu.liveandgov.wp1.backend;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class InspectionServlet
 */
@WebServlet("/ServiceLineDetection")
public class SnippetServiceLineDetection extends HttpServlet {
	private static final long serialVersionUID = 1L;
	PostgresqlDatabase db;
	ArrayList<LatLonTsDayTuple> coordinates;
	HashMap<String, String> transportationMeans;

	/**
	 * @throws UnavailableException
	 * @see HttpServlet#HttpServlet()
	 */
	public SnippetServiceLineDetection() throws UnavailableException {
		super();
		db = new PostgresqlDatabase("liveandgov", "liveandgov");
		initTransportationMeans();
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
		coordinates = new ArrayList<LatLonTsDayTuple>();  
		String line = null;
		  try {
		    BufferedReader reader = request.getReader();

			while ((line = reader.readLine()) != null) {
				coordinates.add(new LatLonTsDayTuple(line));
			}
		  } catch (Exception e) { 
			  e.getMessage();
        }
  String routcodeSelect = ""
	+ "SELECT suball.trips_route_id, \n"
	+ "       suball.shapes_shape_id, \n"
	+ "       suball.trips_trip_id, \n"
	+ "       Sum(cnt) AS score \n"
	+ "FROM   (";
  
  for (int i = 0; i < coordinates.size(); i++) {
	  
	  String p = coordinates.get(i).getLonLatPoint();
	  String betweenTimeClause = coordinates.get(i).getBetweenTimeClause2(2);
	  String d = coordinates.get(i).getISO8601Date();
	  String day = coordinates.get(i).getWeekdayName();
	  String bb = coordinates.get(i).getBoundingBox(5);

	  routcodeSelect += i>0?"        UNION ALL \n        ":"";
	  routcodeSelect += ""
					+ "(SELECT trips_route_id, \n"
					+ "                shapes_shape_id, \n"
					+ "                trips_trip_id, \n"
					+ "                Count(*) AS cnt \n"
					+ "         FROM   snippets \n"
					+ "         WHERE  geom && "+bb+" \n"
					+ "                AND stop_times_arrival_time BETWEEN "+betweenTimeClause+" \n"
					+ "                AND calendar_"+day+" \n"
					+ "             -- AND DATE '"+d+"' BETWEEN calendar.start_date AND calendar.end_date \n"
					+ "         GROUP  BY trips_route_id, \n"
					+ "                   trips_trip_id, \n"
					+ "                   shapes_shape_id) \n";
  }
  routcodeSelect += ") AS suball \n"
	  + "GROUP  BY suball.trips_route_id, \n"
	  + "          suball.trips_trip_id, \n"
	  + "          suball.shapes_shape_id \n"
	  + "ORDER  BY score DESC "
	  + "LIMIT 10;";
  System.out.println(routcodeSelect);
  
	try {
		Statement stm = db.connection.createStatement();
		ResultSet rs = stm.executeQuery(routcodeSelect);
		
		response.setContentType("application/json");
	PrintWriter out = response.getWriter();
	String json = "{\"routes\":[";

	while (rs.next()) {
		if(json.length() > 11) {
			json += ",\n";
		}
		json += "{";
		json += "\"route_id\":\""+rs.getString(1)+"\",";
		json += "\"shape_id\":\""+rs.getString(2)+"\",";
		json += "\"trip_id\":\""+rs.getString(3)+"\",";
		json += "\"transportation_mean\":\""+transportationMeans.get(rs.getString(1))+"\",";
		json += "\"score\":"+rs.getInt(4);
		json += "}";
	}
	json += "]}";
		out.println(json);
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	}
	private void initTransportationMeans(){
		String selectMeans = ""
				+ "SELECT routes.route_id, "
				+ "       route_type.route_type_name "
				+ "FROM   routes, "
				+ "       route_type "
				+ "WHERE  routes.route_type = route_type.route_type";
		try {
			Statement stm = db.connection.createStatement();
			ResultSet rs = stm.executeQuery(selectMeans);
			transportationMeans = new HashMap<String, String>();
		while (rs.next()) {
			transportationMeans.put(rs.getString(1),rs.getString(2));
		}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
