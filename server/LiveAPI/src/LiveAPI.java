import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class LiveAPI {

	// http://dl.dropboxusercontent.com/u/20567085/Mattersoft%20Live!%20interface%20description%20v1_6.pdf
	private static final String REQUEST = "http://83.145.232.209:10001/?type=vehicles&lng1=20&lat1=60&lng2=30&lat2=70&online=1";

	public static List<VehicleInfo> getVehicleDistances(final double lat,
			final double lon) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(new URL(
				REQUEST).openStream()));

		String line;
		LinkedList<VehicleInfo> list = new LinkedList<VehicleInfo>();
		while ((line = in.readLine()) != null) {
			String[] fields = line.split(";");
			list.add(new VehicleInfo(fields));
		}

		class VehicleDistanceComparator implements Comparator<VehicleInfo> {

			    static final double _equatorialEarthRadius = 6371009D; 
			    static final double _d2r = (Math.PI / 180D);

			    public double HaversineInKM(double lat1, double long1, double lat2, double long2) {
			        double dlong = (long2 - long1) * _d2r;
			        double dlat = (lat2 - lat1) * _d2r;
			        double a = Math.pow(Math.sin(dlat / 2D), 2D) + Math.cos(lat1 * _d2r) * Math.cos(lat2 * _d2r)
			                * Math.pow(Math.sin(dlong / 2D), 2D);
			        double c = 2D * Math.atan2(Math.sqrt(a), Math.sqrt(1D - a));
			        double d = _equatorialEarthRadius * c;

			        return d;
			    }


			
			private double calcDistance(VehicleInfo o) {
				return HaversineInKM(lat, lon, o.getLat(), o.getLon());
			}

			@Override
			public int compare(VehicleInfo o1, VehicleInfo o2) {
				double d1 = calcDistance(o1);
				o1.setDistance(d1);
				double d2 = calcDistance(o2);
				o2.setDistance(d2);
				if (d1 < d2)
					return -1;
				else if (d1 > d2)
					return 1;
				else
					return 0;
			}

		}

		Collections.sort(list, new VehicleDistanceComparator());
		return list;
	}

	public static void main(String[] args) throws IOException {
		double lat = 24.95296666;
		double lon = 60.1652805;
		List<VehicleInfo> list = getVehicleDistances(lat, lon);
		for (VehicleInfo o : list) {
			System.out.println(o.getDistance() + " " + o.getId() + " " + o.getLat() + " " + o.getLon());
		}

	}

}
