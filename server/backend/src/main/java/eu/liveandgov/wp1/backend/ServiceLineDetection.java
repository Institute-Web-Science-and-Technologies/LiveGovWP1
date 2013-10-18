package eu.liveandgov.wp1.backend;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

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
public class ServiceLineDetection extends HttpServlet {
	private static final long serialVersionUID = 1L;
	PostgresqlDatabase db;
	ArrayList<LatLonTsDayTuple> coordinates;

	/**
	 * @throws UnavailableException
	 * @see HttpServlet#HttpServlet()
	 */
	public ServiceLineDetection() throws UnavailableException {
		super();
		db = new PostgresqlDatabase("liveandgov", "liveandgov");
		// TODO Auto-generated constructor stub
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
	+ "SELECT suball.route_id, \n"
	+ "       suball.shape_id, \n"
	+ "       suball.trip_id, \n"
	+ "       Sum(cnt) AS score \n"
	+ "FROM   (";
  
  for (int i = 0; i < coordinates.size(); i++) {
	  
	  String p = coordinates.get(i).getLonLatPoint();
	  String betweenTimeClause = coordinates.get(i).getBetweenTimeClause(10);
	  String d = coordinates.get(i).getISO8601Date();
	  String day = coordinates.get(i).getWeekdayName();


	  routcodeSelect += i>0?"        UNION ALL \n        ":"";
	  routcodeSelect += ""
					+ "(SELECT trips.route_id, \n"
					+ "                trips.shape_id, \n"
					+ "                trips.trip_id, \n"
					+ "                Count(*) AS cnt \n"
					+ "         FROM   trips, \n"
					+ "                stop_times, \n"
					+ "                stops, \n"
					+ "                calendar \n"
					+ "         WHERE  trips.route_id IN (SELECT route_id \n"
					+ "                                   FROM   routes \n"
					+ "                                   WHERE  St_distance(geom, St_geometryfromtext('"+p+"', 4326)) \n"
					+ "                                          = ( \n"
					+ "                                          SELECT Min(St_distance(geom, St_geometryfromtext('"+p+"',4326))) \n"
					+ "                                          FROM   routes)) \n"
					+ "                AND trips.trip_id = stop_times.trip_id \n"
					+ "                AND stop_times.arrival_time BETWEEN "+betweenTimeClause+" \n"
					+ "                AND stops.stop_id = stop_times.stop_id \n"
					+ "                AND St_distance(stops.geom, St_geometryfromtext('"+p+"', 4326)) < 0.01 \n"
					+ "                AND calendar.service_id = trips.service_id \n"
					+ "                AND calendar."+day+" \n"
					+ "             -- AND DATE '"+d+"' BETWEEN calendar.start_date AND calendar.end_date \n"
					+ "         GROUP  BY trips.route_id, \n"
					+ "                   trips.trip_id, \n"
					+ "                   trips.shape_id) \n";
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
	String json = "{\"routes\":[";

	while (rs.next()) {
		if(json.length() > 11) {
			json += ",\n";
		}
		json += "{";
		json += "\"route_id\":\""+rs.getString(1)+"\",";
		json += "\"shape_id\":\""+rs.getString(2)+"\",";
		json += "\"trip_id\":\""+rs.getString(3)+"\",";
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
}
