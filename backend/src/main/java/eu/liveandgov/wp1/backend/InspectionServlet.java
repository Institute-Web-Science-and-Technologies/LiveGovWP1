package eu.liveandgov.wp1.backend;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
		
		for (int i = 0; i < points.length; i++) {
			String lonLat = "POINT(" + points[i].substring(points[i].indexOf(',')+2, points[i].indexOf(')')) 
					+ " " +  points[i].substring(7, points[i].indexOf(',')) + ")";
			pointsLonLat[i] = lonLat;
			System.out.println(lonLat);
		}


		String routcode = "";
		String q = "Select routecode, routedir, count(*) from (";

		for (int i = 0; i < points.length; i++) {
			q += i>0?" UNION ALL ":"";
			q += "(SELECT routecode, routedir, " +
					"ST_Distance(lonlatline," +
						"ST_GeometryFromText('POINT(" + points[i].substring(points[i].indexOf(',')+2, points[i].indexOf(')')) 
						+ " " +  points[i].substring(7, points[i].indexOf(',')) + ")',4326)" +
					") as d FROM routesAsLonLatLines " +
					"order by d limit 5)";
		}
		q += ") as subsel where d < 0.001 group by routecode, routedir order by count(*) desc"; // 0.001° ~ 100m
		try {
		Statement knn3Query = db.connection.createStatement();
		ResultSet rs = knn3Query.executeQuery(q);
		System.out.println(q);
		if(rs.next()) {
			routcode = rs.getString(1);
			System.out.println("\"routecode\":\""+rs.getString(1)+"\",\"routedir\":\""+rs.getString(2)+"\"");		
		}
		while (rs.next()) {
			System.out.println("\"routecode\":\""+rs.getString(1)+"\",\"routedir\":\""+rs.getString(2)+"\"");
		}
		//System.out.println(json);
		
		} catch (SQLException e) {
				e.printStackTrace();
		}
		try {
			Statement s = db.createStatement();
			ResultSet rs;
			String routeDir = "select '1'::VARCHAR(1)";
			if(pointsLonLat.length > 1) {
				routeDir = ""+
					"select " +
						"case " +
						   "when ST_Line_Locate_Point(lonlatline,ST_GeometryFromText('"+pointsLonLat[0]+"',4326)) " +
						     " < ST_Line_Locate_Point(lonlatline,ST_GeometryFromText('"+pointsLonLat[pointsLonLat.length-1]+"',4326)) " +
						   "then '1' " +
						   "else '2' " +
						"end " +
					"from routesAsLonLatLines " +
					"where routecode = '"+routcode+"' " +
					"AND routedir = '1' ";
				rs = s.executeQuery(routeDir);
			}

			System.out.println(routeDir);
			
			q = "SELECT routecode, " +
						"routedir, " +
						"ST_AsGeoJSON(" +
						   "lonlatline" +
						") as routePoints " +
						"from routesAsLonLatLines " +
						"where routecode = '" +
						routcode + "' " +
								"AND " +
								"routedir = (" +
								routeDir +");";
			
			System.out.println(q);
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
	}
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
