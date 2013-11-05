package eu.liveandgov.wp1.backend;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.servlet.UnavailableException;

public class DivideAndConquerGtfs {
	
	static PostgresqlDatabase db;
	
	public static void main(String[] args) throws UnavailableException {
		ArrayList<RouteSnippet> a = getShapeOfTripFromDb("1065A_20130930_Ma_2_2000", "1065A_20120813_2");
		cleanUpRows(a);
		interpolateArrivalTimes(a);
		ensureSnippetSpatialDistance(a, 10); // max distance 10 meters
		ensureSnippetTemporalDistance(a, 3); // max distance 3 minutes
	}

    private static void ensureSnippetTemporalDistance(ArrayList<RouteSnippet> allRows, int distanceInMinutes) {
		// TODO Auto-generated method stub
		
	}

	private static void ensureSnippetSpatialDistance(ArrayList<RouteSnippet> allRows, int distanceInMeter) {
		// TODO Auto-generated method stub
		
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
    	
    	for(RouteSnippet r:allRows){
    		System.out.println(r.toCSV());
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
        double d = equatorialEarthRadius * c;

        return d;
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
		System.out.println(q);
		db = new PostgresqlDatabase("liveandgov", "liveandgov");
		
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
	String toCSV() {
		return 	trips_route_id + ","
	      + shapes_shape_id + ","
		  + trips_trip_id + ","
		  + shapes_shape_pt_lat + ","
		  + shapes_shape_pt_lon + "," 	
		  + shapes_shape_pt_sequence + "," 	
		  + stop_times_arrival_time + "," 	
		  + calendar_monday + "," 	
		  + calendar_tuesday + "," 	
		  + calendar_wednesday + "," 	
		  + calendar_thursday + "," 	
		  + calendar_friday + "," 	
		  + calendar_saturday + "," 	
		  + calendar_sunday + "\n";
	}
}