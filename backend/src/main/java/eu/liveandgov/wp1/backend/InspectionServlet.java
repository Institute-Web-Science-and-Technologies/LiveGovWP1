package eu.liveandgov.wp1.backend;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class InspectionServlet
 */
@WebServlet("/InspectionServlet")
public class InspectionServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	PostgresqlDatabase db;
    /**
     * @throws UnavailableException 
     * @see HttpServlet#HttpServlet()
     */
    public InspectionServlet() throws UnavailableException {
        super();
        db = new PostgresqlDatabase("myuser", "mypassword");
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String[] points = request.getParameterValues("points[]");
		System.out.println("---");
		// 'Point(24.95667 60.16946)'
		String[] pointsLonLat = new String[points.length];
		try {
			PreparedStatement knnQuery = db.connection.prepareStatement(""+
					"select routecode, distance from (" +
						"select routecode, " +
						"ST_Distance(ST_Line_Interpolate_Point( " +
						"lonlatline, " +
						"ST_Line_Locate_Point(lonlatline,ST_GeometryFromText(?,4326)) " +
						"), ST_GeometryFromText(?,4326)) as distance " +
						"from routesAsLonLatLines " +
						"order by " +
						"distance asc limit 5 " +
					") as top5;");
			Map<String,Integer> counts = new HashMap<String,Integer>();
			for (int i = 0; i < points.length; i++) {
				String lonLat = "POINT(" + points[i].substring(points[i].indexOf(',')+2, points[i].indexOf(')')) 
						+ " " +  points[i].substring(7, points[i].indexOf(',')) + ")";
				pointsLonLat[i] = lonLat;
				knnQuery.setString(1, lonLat);
				knnQuery.setString(2, lonLat);
				
				System.out.println(lonLat);
				// find nearest route:
				try {
					ResultSet rs = knnQuery.executeQuery();
					while (rs.next()) {
						if(counts.containsKey(rs.getString(1))) {
							int c = counts.get(rs.getString(1));
							c++;
							counts.put(rs.getString(1), c);
						}
						else {
							counts.put(rs.getString(1), 1);
						}
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			String routcode = "";
			int max = 0;
		    Iterator<Entry<String, Integer>> it = counts.entrySet().iterator();
		    while (it.hasNext()) {
		    	Entry<String, Integer> pairs = it.next();
		    	if(pairs.getValue() > max) {
		    		max = pairs.getValue();
		    		routcode = pairs.getKey();
		    	}
		        it.remove(); // avoids a ConcurrentModificationException
		    }
			System.out.println("route: " +routcode + " " +  max);
			knnQuery.close();
			

			
			try {
				Statement s = db.createStatement();
				ResultSet rs;
				String routeDir = "1";
				if(pointsLonLat.length > 1) {
					
					rs = s.executeQuery(""+
							"select ST_Line_Locate_Point(lonlatline,ST_GeometryFromText('"+pointsLonLat[0]+"',4326)), " +
								   "ST_Line_Locate_Point(lonlatline,ST_GeometryFromText('"+pointsLonLat[pointsLonLat.length-1]+"',4326)) " +
							"from routesAsLonLatLines " +
							"where routecode = '"+routcode+"' " +
							"AND routedir = '1' ");


					rs.next();
					if(rs.getFloat(1) < rs.getFloat(2)) {
						routeDir = "1";
						System.out.println("routedir = 1");
					}
					else {
						routeDir = "2";
						System.out.println("routedir = 2");
					}
				}
				
				String q = "" +
						"SELECT routecode, " +
							"routedir, " +
							"ST_AsGeoJSON(" +
							   "lonlatline" +
							") as routePoints " +
							"from routesAsLonLatLines " +
							"where routecode = '" +
							routcode + "' " +
									"AND " +
									"routedir = '" +
									routeDir +
									"' ";
				
				//System.out.println(q);
				rs = s.executeQuery(q);
				response.setContentType("application/json");
				PrintWriter out = response.getWriter();
				String json = "{\"routes\":[";

				while (rs.next()) {
					if(json.length() > 11) {
						json += ",\n";
					}
					json += "{";
					json += "\"routecode\":\""+rs.getString(1)+"\",";
					json += "\"routedir\":\""+rs.getString(2)+"\",";
					json += "\"geojson\":"+rs.getString(3);
					json += "}";
				}
				json += "]}";
				out.println(json);
				//System.out.println(json);
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}
;
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
