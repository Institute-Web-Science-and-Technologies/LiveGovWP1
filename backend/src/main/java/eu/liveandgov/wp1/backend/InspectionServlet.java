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
						"s1.kkj2 <-> ST_Transform(ST_GeometryFromText(?,4326),2392) as distance " +
						"from routes s1 " +
						"order by " +
						"distance asc limit 10" +
					") as top10 " +
					"where distance < 1000");
			Map<String,Integer> counts = new HashMap<String,Integer>();
			for (int i = 0; i < points.length; i++) {
				String lonLat = "POINT(" + points[i].substring(points[i].indexOf(',')+2, points[i].indexOf(')')) 
						+ " " +  points[i].substring(7, points[i].indexOf(',')) + ")";
				pointsLonLat[i] = lonLat;
				knnQuery.setString(1, lonLat);
				//knnQuery.setString(2, lonLat);
				
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
			
			Statement s = db.createStatement();
			try {
				ResultSet rs = s.executeQuery("" +
						"select * " +
						"from ( " +
							"SELECT r.routecode, " +
							"r.routedir, " +
							"r.validto, " +
							"ST_AsGeoJSON(" +
							      "ST_MakeLine(" +
							          "ST_Transform(r.kkj2,4326) ORDER BY r.stoporder" +
							      ")" +
							") as routePoints " +
							"from routes as r " +
							"group by routecode, " +
							"routedir, " +
							"validto " +
							"having routecode = '" +
							routcode + "' " +
					    ") as route " +
					    "order by route.validto desc " +
					    "limit 2 ");
				response.setContentType("application/json");
				PrintWriter out = response.getWriter();
				String json = "{\"routes\":[";
				String validDate = "";
				while (rs.next()) {
					if(json.length() > 11) {
						json += ",\n";
					}
					json += "{";
					json += "\"routecode\":\""+rs.getString(1)+"\",";
					validDate = rs.getString(3);
					json += "\"routedir\":\""+rs.getString(2)+"\",";
					json += "\"geojson\":"+rs.getString(4);
					json += "}";
				}
				json += "]}";
				out.println(json);
				//System.out.println(json);

				if(pointsLonLat.length > 1) {
					rs = s.executeQuery(""+
							"select *  from ( " +
							"select stoporder, " +
							"kkj2 <-> ST_Transform(ST_GeometryFromText('"+pointsLonLat[0]+"',4326),2392) as distance " +
							"from routes " +
							"where routecode = '"+routcode+"' " +
							"AND routedir = '1' " +
							"AND validTo = '"+validDate+"' " +
							"order by " +
							"distance asc limit 2" +
							") as subsel " +
							"order by subsel.stoporder");
	
					rs.next();
					int stop1 = rs.getInt(1);// +  " " + rs.getString(2) + " " + rs.getString(3));
					float distance1 =  rs.getFloat(2);
					System.out.println("s1: " + stop1 + " d1: " + distance1);
					rs.next();
					int stop2 =  rs.getInt(1);
					float distance2 =  rs.getFloat(2);
					System.out.println("s2: " + stop2 + " d2: " + distance2);
					
					rs = s.executeQuery(""+
							"select stoporder, " +
							"kkj2 <-> ST_Transform(ST_GeometryFromText('"+pointsLonLat[1]+"',4326),2392) as distance " +
							"from routes " +
							"where routecode = '"+routcode+"' " +
							"AND routedir = '1' " +
							"AND validTo = '"+validDate+"' " +
							"AND stoporder in (" + stop1 + ", " + stop2 + ") " +
							"order by stoporder ");
					
					rs.next();
					float distance3 =  rs.getFloat(2);
					System.out.println("s3: " + rs.getString(1) + " d3: " + distance3);
					rs.next();
					float distance4 =  rs.getFloat(2);
					System.out.println("s4: " + rs.getString(1) + " d4: " + distance4);
					
					if((distance1 > distance3 && distance2 > distance4) 
					|| (distance1 > distance3 && distance2 < distance3)
					|| (distance1 < distance3 && distance2 < distance3)) {
						System.out.println("dir1");
					}
					else {
						System.out.println("dir2");
					}
				}
				
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
