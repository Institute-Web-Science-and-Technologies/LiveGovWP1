package eu.liveandgov.wp1.backend;

public abstract class Database {
   /** computes the great circle distance between to coordinates given in latitude and longitude
    * 
    * @param lat0 latitude of point 0
    * @param lon0 longitude of point 0
    * @param lat1 latitude of point 1
    * @param lon1 longitude of point 1
    * @return great circle distance in meter
    */
	public abstract double distanceInMeter(double lat0, double lon0, double lat1, double lon1);
}
