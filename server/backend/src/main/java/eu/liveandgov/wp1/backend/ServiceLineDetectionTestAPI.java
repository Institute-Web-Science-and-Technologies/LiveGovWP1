package eu.liveandgov.wp1.backend;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
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
@WebServlet("/ServiceLineDetectionTestAPI")
public class ServiceLineDetectionTestAPI extends HttpServlet {
	private static final long serialVersionUID = 1L;
	PostgresqlDatabase db;
	ArrayList<LatLonTsDayTuple> coordinates;
	HashMap<String, String> transportationMeans;

	/**
	 * @throws UnavailableException
	 * @see HttpServlet#HttpServlet()
	 */
	public ServiceLineDetectionTestAPI() throws UnavailableException {
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
		
		String username = request.getHeader("username");
		if(username == null){
			PrintWriter out = response.getWriter();
			out.println("{\"error\":\"username required\"}");
			Util.SLDLogger.log().warn("Test API request without username");
			return;
		}
		
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
		  String logmsg = "Test API call username: " + username;
		  if(coordinates.size() > 0) {
			  logmsg += " " + coordinates.get(0).getLonLatPoint();
		  }
		  Util.SLDLogger.log().info(logmsg);
		try {
			int lastDigitOfTimestamp = Integer.parseInt(coordinates.get(0)
					.getDaytimeDigitsOnly().substring(5, 6));
			if (lastDigitOfTimestamp < 9) {
				// 80% return a tram line result
				coordinates.add(new LatLonTsDayTuple("60.169204,24.939605999999998,2013-11-18 15:17:05,Mon"));
				coordinates.add(new LatLonTsDayTuple("60.170615,24.93748099999999,2013-11-18 15:18:15,Mon"));
				if (lastDigitOfTimestamp == 8) {
					// 10% return a result other than a tram(bus/ferry etc.)
					coordinates.add(new LatLonTsDayTuple("60.178487,24.929092999999995,2013-11-18 15:22:35,Mon"));
				}
			}
			else {
				// 10% return an empty result : {"routes":[]}
			}

		} catch (ParseException e) {
			e.printStackTrace();
			Util.SLDLogger.log().error(e);
		}
 
  String routcodeSelect = ""
	+ "SELECT suball.route_id, \n"
	+ "       suball.shape_id, \n"
	+ "       suball.trip_id, \n"
	+ "       Sum(cnt) AS score \n"
	+ "FROM   (";
  
  for (int i = 0; i < coordinates.size(); i++) {
	  
	  String betweenTimeClause = coordinates.get(i).getBetweenTimeClause("arrival_time",2);
	  String d = coordinates.get(i).getISO8601Date();
	  String day = coordinates.get(i).getWeekdayName();
	  String bb = coordinates.get(i).getBoundingBox(5);

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
		Util.SLDLogger.log().error(e);
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
			Util.SLDLogger.log().error(e);
		}

	}
}
