package eu.liveandgov.wp1.backend;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Comparator;

import javax.servlet.UnavailableException;

import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;

public class DivideAndConquerGtfs {
	
	static PostgresqlDatabase db;
	
	public static void main(String[] args) throws UnavailableException, SQLException, IOException {
		//ArrayList<RouteSnippet> a = getShapeOfTripFromDb("1065A_20130930_Ma_2_2000", "1065A_20120813_2");
		//ArrayList<RouteSnippet> a = new ArrayList<RouteSnippet>(a1.subList(0, 12));
		db = new PostgresqlDatabase("liveandgov", "liveandgov");
		String filename = "/tmp/snippets.csv";
		File file = new File(filename);
		FileOutputStream fos = new FileOutputStream(file);
		Writer out = new OutputStreamWriter(fos, "UTF8");

		Statement stm = db.connection.createStatement();
		ResultSet rs = stm.executeQuery("SELECT trip_id, shape_id FROM trips");
		int processedRows = 0;
		while (rs.next()) {
			writeTripToFile(rs.getString(1), rs.getString(2),  10, 2, out);
			processedRows++;
			if(processedRows%1000 == 0){
				System.out.println("processed rows: " + processedRows + " (" + processedRows*100/206153.0 + "%)");
			}
		}
		out.flush();
		out.close();
		createSnippetTable();
		loadSnippetTable(filename);
//    	for(RouteSnippet r:a){
////    		System.out.println("L.marker(["+r.shapes_shape_pt_lat+", "+r.shapes_shape_pt_lon+"]).addTo(map).bindPopup('"+r.toCSV()+"');");
////  System.out.println( r.toCSV());
//    	}
	}

	public static void writeTripToFile(String tripId, String shapeId,  int distanceInMeter, int distanceInMinutes, Writer out) throws UnavailableException, IOException{
		ArrayList<RouteSnippet> a = getShapeOfTripFromDb(tripId, shapeId);
		cleanUpRows(a);
		interpolateArrivalTimes(a);
		ensureSnippetDistance(a, distanceInMeter, distanceInMinutes);
		//Collections.sort(a, new RouteSnippetComparator());
    	for(RouteSnippet r:a){
    		out.write(r.toCSV()+"\n");
    	}
		
	}
	
	private static void ensureSnippetDistance(ArrayList<RouteSnippet> allRows, int distanceInMeter, int distanceInMinutes) {

    	int size = allRows.size();
    	int distanceInSec = distanceInMinutes * 60;

		for(int j = 0; j < size-1; j++){
			RouteSnippet r0 = allRows.get(j);
			RouteSnippet r1 = allRows.get(j+1);
			
			double sDelta = r1.meterTraveled - r0.meterTraveled;
			int spatialRatio = (int) Math.ceil(sDelta / distanceInMeter);
			double tDelta = r1.stop_times_arrival_time - r0.stop_times_arrival_time;
			int temporalRatio = (int) Math.ceil(tDelta / distanceInSec);
			int ratio = Math.max(spatialRatio,temporalRatio);
			
			if(ratio > 1) {
				// this part is longer than distanceInMeter or distanceInMinutes, split it in smaller pieces
				double partLength = sDelta / ratio;
				double partTime = tDelta / ratio;
				for(int i = 1; i < ratio; i++){
					double[] latLon =  latLonBetweenAtDistance(r0.shapes_shape_pt_lat, 
							r0.shapes_shape_pt_lon, 
							r1.shapes_shape_pt_lat, 
							r1.shapes_shape_pt_lon, 
							partLength*i);
					int arrivalTimeInSec = (int)(r0.stop_times_arrival_time + partTime*i);
					RouteSnippet rIntermediate = new RouteSnippet(r0);
					rIntermediate.shapes_shape_pt_lat = (float)latLon[0];
					rIntermediate.shapes_shape_pt_lon = (float)latLon[1];
					rIntermediate.stop_times_arrival_time = arrivalTimeInSec;
					rIntermediate.meterTraveled = r0.meterTraveled + partLength*i;
					allRows.add(rIntermediate);
				}
			}
		}
		
	}

	private static void interpolateArrivalTimes(ArrayList<RouteSnippet> allRows) {
		// we suppose that the first row has always a arrival time
    	int t0 = allRows.get(0).stop_times_arrival_time;    	
    	int t0Index = 0;
    	double t0MeterTraveled = 0;
    	int t1 = 0;
    	int t1Index = 0;
    	double t1MeterTraveled = 0;
    	int size = allRows.size();
    	while(true){
    		// search t1
			for(int j = t0Index+1; j < size; j++){
				int t = allRows.get(j).stop_times_arrival_time;
				if(t != 0) {
					t1 = t;
					t1Index = j;
					t1MeterTraveled = allRows.get(j).meterTraveled;
					break;
				}
			}
			int totalTime = t1 - t0;
			double totalMeterTraveled = t1MeterTraveled - t0MeterTraveled;
			
			for(int j = t0Index; j<=t1Index; j++){
				if(totalTime != 0){
					double meterTraveledSinceT0 = allRows.get(j).meterTraveled - t0MeterTraveled;
					// interpolate the travel time
					int timeDelta = (int)(meterTraveledSinceT0*totalTime/totalMeterTraveled);
					// store time in seconds since midnight
					allRows.get(j).stop_times_arrival_time = (t0 + timeDelta);
				}
				else {
					allRows.get(j).stop_times_arrival_time = t0;
				}
			}
			if(t1Index == size-1){
				// end of array reached
				break;
			}
			t0 = t1;
			t0Index = t1Index;
			t0MeterTraveled = t1MeterTraveled;
		}		
	}

	private static int secSinceMidnight(int t0) {
		// t0 has the shape 195940 for 19:59:40
    	int hours = t0/10000;
    	int minutes = (t0 - hours*10000)/100;
    	int seconds = t0 - hours*10000 - minutes*100;

    	// seconds since midnight
    	return 3600*hours + 60 * minutes + seconds;
	}

	private static void cleanUpRows(ArrayList<RouteSnippet> allRows) {
    	String trips_route_id = null;
    	String trips_trip_id = null;
    	boolean calendar_monday = false;
    	boolean calendar_tuesday = false;
    	boolean calendar_wednesday = false;
    	boolean calendar_thursday = false;
    	boolean calendar_friday = false;
    	boolean calendar_saturday = false;
    	boolean calendar_sunday = false;
    	boolean isFirst = true;
    	RouteSnippet lastRow = null;
    	for(RouteSnippet r:allRows) {
			if(isFirst) {
				// later on some columns are NULL, therefore store them here
				trips_route_id = r.trips_route_id;
				trips_trip_id = r.trips_trip_id;
				calendar_monday = r.calendar_monday;
				calendar_tuesday = r.calendar_tuesday;
				calendar_wednesday = r.calendar_wednesday;
				calendar_thursday = r.calendar_thursday;
				calendar_friday = r.calendar_friday;
				calendar_saturday = r.calendar_saturday;
				calendar_sunday = r.calendar_sunday;
				r.meterTraveled = 0;
				isFirst = false;
			}
			else {
				// in some rows the route_id and trip_id are NULL 
				r.trips_route_id = trips_route_id;
				r.trips_trip_id = trips_trip_id;
				r.calendar_monday = calendar_monday;
				r.calendar_tuesday = calendar_tuesday;
				r.calendar_wednesday = calendar_wednesday;
				r.calendar_thursday = calendar_thursday;
				r.calendar_friday = calendar_friday;
				r.calendar_saturday = calendar_saturday;
				r.calendar_sunday = calendar_sunday;
				// sum up all the travel distance
				r.meterTraveled = lastRow.meterTraveled +
						haversineInMeter(r.shapes_shape_pt_lat, r.shapes_shape_pt_lon, 
								lastRow.shapes_shape_pt_lat, lastRow.shapes_shape_pt_lon);
			}
			r.stop_times_arrival_time = secSinceMidnight(r.stop_times_arrival_time);
			lastRow = r;
		}
		
	}

	static final double equatorialEarthRadius = 6371009D; 
    static final double d2r = (Math.PI / 180D);

    public static double haversineInMeter(double lat1, double long1, double lat2, double long2) {
        double dlong = (long2 - long1) * d2r;
        double dlat = (lat2 - lat1) * d2r;
        double a = Math.pow(Math.sin(dlat / 2D), 2D) + Math.cos(lat1 * d2r) * Math.cos(lat2 * d2r)
                * Math.pow(Math.sin(dlong / 2D), 2D);
        double c = 2D * Math.atan2(Math.sqrt(a), Math.sqrt(1D - a));
        return equatorialEarthRadius * c;
    }
    
    public static double[] latLonBetweenAtDistance(double lat1, double long1, double lat2, double long2, double distanceInMeter) {
    	double bearing = bearingInRad(lat1, long1, lat2, long2);
    	return greatCircleEndPositionLatLon(lat1, long1, bearing, distanceInMeter);    
    }
    
    // http://www.movable-type.co.uk/scripts/latlong.html
    public static double bearingInRad(double lat1, double long1, double lat2, double long2){
    	double dlong = (long2 - long1) * d2r;
    	double y = Math.sin(dlong) * Math.cos(lat2 * d2r);
    	double x = Math.cos(lat1 * d2r)*Math.sin(lat2 * d2r) -
    	        Math.sin(lat1 * d2r)*Math.cos(lat2 * d2r)*Math.cos(dlong);
    	return Math.atan2(y, x);
    }
    
    // http://www.movable-type.co.uk/scripts/latlong.html
    public static double[] greatCircleEndPositionLatLon(double lat, double lon, double bearingInRad, double distanceInMeter){
    	double lat2 = Math.asin( Math.sin(lat * d2r)*Math.cos(distanceInMeter/equatorialEarthRadius) + 
            Math.cos(lat * d2r)*Math.sin(distanceInMeter/equatorialEarthRadius)*Math.cos(bearingInRad));
    	double lon2 = lon * d2r + Math.atan2(Math.sin(bearingInRad)*Math.sin(distanceInMeter/equatorialEarthRadius)*Math.cos(lat * d2r), 
                   Math.cos(distanceInMeter/equatorialEarthRadius)-Math.sin(lat * d2r)*Math.sin(lat2));
    	return new double[] {lat2 / d2r, lon2 / d2r};
    }
    
	private static ArrayList<RouteSnippet> getShapeOfTripFromDb(String tripId, String shapeId) throws UnavailableException {
		String q = ""
				+ "SELECT * "
				+ "FROM   (SELECT trips.route_id, "
				+ "               shapes.shape_id, "
				+ "               trips.trip_id, "
				+ "               shapes.shape_pt_lat, "
				+ "               shapes.shape_pt_lon, "
				+ "               shapes.shape_pt_sequence, "
				+ "               stop_times.arrival_time, "
				+ "               calendar.monday, "
				+ "               calendar.tuesday, "
				+ "               calendar.wednesday, "
				+ "               calendar.thursday, "
				+ "               calendar.friday, "
				+ "               calendar.saturday, "
				+ "               calendar.sunday "
				+ "        FROM   shapes, "
				+ "               trips, "
				+ "               stops, "
				+ "               stop_times, "
				+ "               calendar "
				+ "        WHERE  shapes.shape_id = trips.shape_id "
				+ "               AND trips.trip_id = '"+tripId+"' "
				+ "               AND trips.trip_id = stop_times.trip_id "
				+ "               AND stops.stop_id = stop_times.stop_id "
				+ "               AND shapes.geom = stops.geom "
				+ "               AND calendar.service_id = trips.service_id "
				+ "        UNION "
				+ "        SELECT NULL, "
				+ "               shapes.shape_id, "
				+ "               NULL, "
				+ "               shapes.shape_pt_lat, "
				+ "               shapes.shape_pt_lon, "
				+ "               shapes.shape_pt_sequence, "
				+ "               NULL, "
				+ "               NULL, "
				+ "               NULL, "
				+ "               NULL, "
				+ "               NULL, "
				+ "               NULL, "
				+ "               NULL, "
				+ "               NULL "
				+ "        FROM   shapes "
				+ "        WHERE  shapes.shape_id = '"+shapeId+"') AS u "
				+ "ORDER  BY u.shape_pt_sequence";
		// System.out.println(q);

		
		ArrayList<RouteSnippet> allRows = new ArrayList<RouteSnippet>();
		RouteSnippet lastRow = new RouteSnippet();

		try {
			Statement stm = db.connection.createStatement();
			ResultSet rs = stm.executeQuery(q);
			while (rs.next()) {
				RouteSnippet row = new RouteSnippet(rs);

				if(allRows.size() > 0 &&
						lastRow.shapes_shape_pt_sequence == row.shapes_shape_pt_sequence
						&& row.stop_times_arrival_time > 0){
					// this shape point exists twice (one time in the stops table and one time in the shapes table)
					// replace it 
					allRows.set(allRows.size()-1,row);
				}
				else if (lastRow.shapes_shape_pt_sequence != row.shapes_shape_pt_sequence){
					allRows.add(row);
				}
				lastRow = row;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	return allRows;
}
	static void createSnippetTable() throws SQLException {
		Statement s = db.connection.createStatement();

		s.execute("DROP TABLE IF EXISTS snippets");
		s.execute("CREATE TABLE IF NOT EXISTS snippets"
				+ "( trips_route_id  VARCHAR(20),"
			      + " shapes_shape_id VARCHAR(100),"
				  + " trips_trip_id VARCHAR(100),"
				  + " calendar_monday boolean," 	
				  + " calendar_tuesday boolean," 	
				  + " calendar_wednesday boolean," 	
				  + " calendar_thursday boolean," 	
				  + " calendar_friday boolean," 	
				  + " calendar_saturday boolean," 	
				  + " calendar_sunday boolean,"
				  + " geom  GEOMETRY(POINT,4326)," 	
				  + " shapes_shape_pt_sequence INTEGER," 	
				  + " stop_times_arrival_time INTEGER," 	
				  + " meterTraveled REAL )");			
	}
	static void loadSnippetTable(String filename) throws SQLException, FileNotFoundException, IOException {

		CopyManager copyManager = new CopyManager((BaseConnection) db.connection);
        copyManager.copyIn("COPY snippets FROM STDIN (NULL '', DELIMITER ',');",  new FileReader(filename) );
		Statement s = db.connection.createStatement();
        s.execute("create index snippets_idx on snippets using gist (geom);");
	}
}
	
class RouteSnippet {
	String trips_route_id;
	String shapes_shape_id;
	String trips_trip_id; 
	float shapes_shape_pt_lat;	
	float shapes_shape_pt_lon;	
	int shapes_shape_pt_sequence;	
	int	stop_times_arrival_time;
	boolean calendar_monday;
	boolean calendar_tuesday;
	boolean calendar_wednesday;
	boolean calendar_thursday;
	boolean calendar_friday;
	boolean calendar_saturday;
	boolean calendar_sunday;
	
	double meterTraveled;
	
	RouteSnippet(){};
	RouteSnippet(ResultSet rs) throws SQLException{
		trips_route_id = rs.getString(1);
		shapes_shape_id = rs.getString(2);
		trips_trip_id = rs.getString(3); 
		shapes_shape_pt_lat = rs.getFloat(4);	
		shapes_shape_pt_lon = rs.getFloat(5);	
		shapes_shape_pt_sequence = rs.getInt(6);	
		stop_times_arrival_time = rs.getInt(7);
		calendar_monday = rs.getBoolean(8);
		calendar_tuesday = rs.getBoolean(9);
		calendar_wednesday = rs.getBoolean(10);
		calendar_thursday = rs.getBoolean(11);
		calendar_friday = rs.getBoolean(12);
		calendar_saturday = rs.getBoolean(13);
		calendar_sunday = rs.getBoolean(14);
	}

	public RouteSnippet(RouteSnippet r0) {
		trips_route_id = r0.trips_route_id;
		shapes_shape_id = r0.shapes_shape_id;
		trips_trip_id = r0.trips_trip_id;
		shapes_shape_pt_lat = r0.shapes_shape_pt_lat;
		shapes_shape_pt_lon = r0.shapes_shape_pt_lon;
		shapes_shape_pt_sequence = r0.shapes_shape_pt_sequence;
		stop_times_arrival_time = r0.stop_times_arrival_time;
		calendar_monday = r0.calendar_monday;
		calendar_tuesday = r0.calendar_tuesday;
		calendar_wednesday = r0.calendar_wednesday;
		calendar_thursday = r0.calendar_thursday;
		calendar_friday = r0.calendar_friday;
		calendar_saturday = r0.calendar_saturday;
		calendar_sunday = r0.calendar_sunday;

		meterTraveled = r0.meterTraveled;
	}
	String toCSV() {
		return 	trips_route_id + ","
	      + shapes_shape_id + ","
		  + trips_trip_id + ","
		  + (calendar_monday?"t,":"f,") 	
		  + (calendar_tuesday?"t,":"f,") 	
		  + (calendar_wednesday?"t,":"f,") 	
		  + (calendar_thursday?"t,":"f,") 	
		  + (calendar_friday?"t,":"f,") 	
		  + (calendar_saturday?"t,":"f,") 	
		  + (calendar_sunday?"t,":"f,")
		  + "SRID=4326;POINT(" 
		  + shapes_shape_pt_lon + " "
		  + shapes_shape_pt_lat + ")," 	
		  + shapes_shape_pt_sequence + "," 	
		  + stop_times_arrival_time + "," 	
		  
		  + meterTraveled;
	}
}

class RouteSnippetComparator implements Comparator<RouteSnippet> {
	@Override
	public int compare(RouteSnippet arg0, RouteSnippet arg1) {
		return new Double(arg0.meterTraveled).compareTo(arg1.meterTraveled);
	}
}