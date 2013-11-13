package eu.liveandgov.wp1.backend;

public class VehicleInfo {

	private String id;
	private String route;
	private double lat;
	private double lon;
	private int bearing;
	private int direction;
	private int previousStop;
	private int currentStop;
	private int departure;
	private double distance;

	public String getId() {
		return id;
	}

	public String getRoute() {
		return route;
	}

	public double getLat() {
		return lat;
	}

	public double getLon() {
		return lon;
	}

	public int getBearing() {
		return bearing;
	}

	public int getDirection() {
		return direction;
	}

	public int getPreviousStop() {
		return previousStop;
	}

	public int getCurrentStop() {
		return currentStop;
	}

	public int getDeparture() {
		return departure;
	}
	
	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}
	
	public VehicleInfo(String[] a) {
		this.id = a[0];
		this.route = a[1];
		this.lon = Double.parseDouble(a[2]);
		this.lat = Double.parseDouble(a[3]);
		this.bearing = Integer.parseInt(a[4]);
		this.direction = Integer.parseInt(a[5]);
		this.previousStop = Integer.parseInt(a[6]);
		this.currentStop = Integer.parseInt(a[7]);
		if (a.length == 9)
			this.departure = Integer.parseInt(a[8]);
		else
			this.departure = 0;
	}

}
