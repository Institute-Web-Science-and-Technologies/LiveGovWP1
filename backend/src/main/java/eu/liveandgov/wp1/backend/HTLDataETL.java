package eu.liveandgov.wp1.backend;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Locale;

import javax.servlet.UnavailableException;

import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;

public class HTLDataETL {
	PostgresqlDatabase db;
	
	public PostgresqlDatabase getDb() {
		return db;
	}

	private static final String LINEFILE = "/home/chrisschaefer/linja.dat";
	private static final String STOPFILE = "/home/chrisschaefer/pys__kki.dat";
	private static final String ROUTEFILE = "/home/chrisschaefer/reittimuoto.dat";
	private static final String ENCODING = "latin1";
	
	int[][] KKJ_ZONE_INFO = {
			{18, 500000},
			{21, 1500000},
			{24, 2500000},
			{27, 3500000},
			{30, 4500000},
			{33, 5500000}
	};
	

	public HTLDataETL() throws UnavailableException {
		db = new PostgresqlDatabase("myuser", "mypassword");
	}



	private void readLineInfos() throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(
				new FileInputStream(LINEFILE), ENCODING));

		File file = new File("/tmp/lines.csv");
		FileOutputStream fos = new FileOutputStream(file);
		Writer out = new OutputStreamWriter(fos, "UTF8");
		while (in.ready()) {
			out.write(extractLineInfo(in.readLine(),"\t") + "\n");
		}
		in.close();
		out.close();
	}

	private void readStopInfos() throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(
				new FileInputStream(STOPFILE), ENCODING));
		File file = new File("/tmp/stops.csv");
		FileOutputStream fos = new FileOutputStream(file);
		Writer out = new OutputStreamWriter(fos, "UTF8");
		while (in.ready()) {
			out.write(extractStopInfo(in.readLine(),"\t") + "\n");
		}
		in.close();
		out.close();
	}

	private void readRouteInfos() throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(
				new FileInputStream(ROUTEFILE), ENCODING));
		File file = new File("/tmp/routes.csv");
		FileOutputStream fos = new FileOutputStream(file);
		Writer out = new OutputStreamWriter(fos, "UTF8");
		while (in.ready()) {
			out.write(extractRouteInfo(in.readLine(),"\t") + "\n");
		}
		in.close();
		out.close();
	}

	public void extractTransferLoad() throws Exception {
		HTLDataETL etl = new HTLDataETL();
		etl.readLineInfos();
		etl.readStopInfos();
		etl.readRouteInfos();
		
		etl.createTables();
		
        CopyManager copyManager = new CopyManager((BaseConnection) etl.getDb().connection);

        copyManager.copyIn("COPY lineinfo FROM STDIN (NULL '');",  new FileReader("/tmp/lines.csv") );
        copyManager.copyIn("COPY stops FROM STDIN (NULL '')",  new FileReader("/tmp/stops.csv") );
        copyManager.copyIn("COPY routes FROM STDIN (NULL '')",  new FileReader("/tmp/routes.csv") );
        
	}
	
	
	
	public void createTables() throws SQLException {
		Statement s = db.createStatement();
		s.execute("CREATE TABLE IF NOT EXISTS lineinfo"
				+ "( id VARCHAR(7),"
				+ "date1 DATE,"
				+ " date2 DATE,"
				+ " language VARCHAR(1),"
				+ " lineName VARCHAR(60),"
				+ " terminal1Name VARCHAR(20),"
				+ " terminal2Name VARCHAR(20),"
				+ " stopCodeDir1 INTEGER,"
				+ " stopCodeDir2 INTEGER,"
				+ " lineLengthDir1 INTEGER,"
				+ " lineLengthDir2 INTEGER,"
				+ " transportMean INTEGER )");
		s.execute("CREATE TABLE IF NOT EXISTS stops"
				+ "( stopCode INTEGER,"
				//					+ " x_kkj2 INTEGER,"
				//					+ " y_kkj2 INTEGER,"
				//					+ " latitude FLOAT,"
				//					+ " longitude FLOAT,"
				+ " lonlat GEOGRAPHY(POINT),"
				+ " stopName VARCHAR(20),"
				+ " stopNameSwedish VARCHAR(20),"
				+ " address VARCHAR(20),"
				+ " addressSwedish VARCHAR(20),"
				+ " platformNumber VARCHAR(3),"
				//					+ " x_kkj3 INTEGER,"
				//					+ " y_kkj3 INTEGER,"
				+ " stopLocationAreaName VARCHAR(20),"
				+ " stopLocationAreaNameSwedish VARCHAR(20),"
				+ " shelter INTEGER,"
				+ " stopShortCode VARCHAR(6),"
				//					+ " x_wgs84_proj FLOAT,"
				//					+ " y_wgs84_proj FLOAT,"
				+ " coordMethod VARCHAR(1),"
				+ " accessibilityClass INTEGER,"
				+ " note VARCHAR(15) )");
		s.execute("CREATE TABLE IF NOT EXISTS routes"
				+ "( routeCode VARCHAR(6),"
				+ " routeDir VARCHAR(1),"
				+ " validFrom DATE,"
				+ " validTo DATE,"
				+ " stopCode INTEGER,"
				+ " type VARCHAR(1),"
				+ " stopOrder INTEGER,"
				//					+ " x INTEGER,"
				//					+ " y INTEGER,"
				+ " lonlat GEOGRAPHY(POINT) )");

		s.close();
	}

	public String extractLineInfo(String line, String delimiter) {
		String result = "";
		line = line.substring(1);
		result += line.substring(0, 6).trim(); // Id
		result += delimiter;
		result += line.substring(6, 14).trim(); // Date1
		result += delimiter;
		result += line.substring(14, 22).trim(); // Date2
		result += delimiter;
		result += line.substring(22, 23).trim(); // Language
		result += delimiter;
		result += line.substring(23, 83).trim(); // LineName
		result += delimiter;
		result += line.substring(83, 103).trim(); // Terminal1Name
		result += delimiter;
		result += line.substring(103, 123).trim(); // Terminal2Name
		result += delimiter;
		result += line.substring(123, 130).trim(); // StopCodeDir1
		result += delimiter;
		result += line.substring(130, 137).trim(); // StopCodeDir2
		result += delimiter;
		result += line.substring(137, 142).trim(); // LineLengthDir1
		result += delimiter;
		result += line.substring(142, 147).trim(); // LineLengthDir2
		result += delimiter;
		result += line.substring(147, 149).trim(); // TransportMean
		return result;
	}

	public String extractStopInfo(String line, String delimiter) {
		String result = "";
		line = line.substring(1);
		result += line.substring(0, 7).trim(); // StopCode

		// ignore
		line.substring(7, 14).trim(); // X_kkj2
		line.substring(14, 21).trim(); // Y_kkj2

		result += delimiter;

		String lat = line.substring(21, 29).trim(); // Latitude
		String lon = line.substring(29, 37).trim(); // Longitude

		result += "Point("+lon+" "+lat+")";

		result += delimiter;
		result += line.substring(37, 57).trim(); // StopName
		result += delimiter;
		result += line.substring(57, 77).trim(); // StopNameSwedish
		result += delimiter;
		result += line.substring(77, 97).trim(); // Address
		result += delimiter;
		result += line.substring(97, 117).trim(); // AddressSwedish
		result += delimiter;
		result += line.substring(117, 120).trim(); // PlatformNumber

		line.substring(120, 127).trim(); // X_kkj3
		line.substring(127, 134).trim(); // Y_kkj3
		result += delimiter;
		result += line.substring(134, 154).trim(); // StopLocationAreaName
		result += delimiter;
		result += line.substring(154, 174).trim(); // StopLocationAreaNameSwedish
		result += delimiter;
		result += line.substring(174, 176).trim(); // Shelter
		result += delimiter;
		result += line.substring(176, 182).trim(); // StopShortCode

		line.substring(182, 190).trim(); // X_wgs84_proj
		line.substring(190, 198).trim(); // Y_wgs84_proj

		result += delimiter;
		// WGS84-coordinates solx and soly for this stop calculated or measured
		// (L/M)
		result += line.substring(198, 199).trim(); // CoordMethod
		result += delimiter;
		result += line.substring(199, 200).trim(); // AccessibilityClass
		result += delimiter;
		result += line.substring(200, 215).trim(); // Note
		return result;
	}

	public String extractRouteInfo(String line, String delimiter) {
		String result = "";
		line = line.substring(1);
		result += line.substring(0, 6).trim(); // RouteCode
		result += delimiter;
		result += line.substring(6, 7).trim(); // RouteDir
		result += delimiter;
		result += line.substring(7, 15).trim(); // ValidFrom
		result += delimiter;
		result += line.substring(15, 23).trim(); // ValidTo
		result += delimiter;
		result += line.substring(23, 30).trim(); // StopCode
		result += delimiter;
		/*
		 * In case of geometry point relpysakki = M, else relpysakki= P = stop
		 * which is used by this route E = stop which is not used by this route
		 * X = crossing - = border
		 */
		result += line.substring(30, 31).trim(); // Type
		result += delimiter;
		result += line.substring(31, 35).trim(); // StopOrder
		result += delimiter;
		// kkj-2?
		String x = line.substring(35, 42).trim(); // X
		String y = line.substring(42, 49).trim(); // Y
		result += transferKkjToLonLat(x,y);
		return result;
	}

	public String transferKkjToLonLat(String kkjX, String kkjY) {
		double[] lalo = KKJxyToWGS85lalo(Double.valueOf(kkjX), Double.valueOf(kkjY));
		return String.format(Locale.ENGLISH, "POINT(%.6f %.6f)", lalo[1], lalo[0]);
	}	  


	private double[] KKJxyToWGS85lalo (double x,double y ) {
		double[] kLaLo = KKJxyToKKJlalo( x, y );
		double[] wLaLo = KKJlaloToWGS84lalo( kLaLo[0], kLaLo[1] );
		return wLaLo;
	}

	private double[] KKJxyToKKJlalo (double x,double y ) {
		if ( x < y ) {
			throw new IllegalArgumentException("Wrong coords!");
		}

		int zone = KKJZone( y );

		double[] LaLo = new double[2];

		double minLa = deg2Rad( 59 );
		double maxLa = deg2Rad( 70.5 );
		double minLo = deg2Rad( 18.5 );
		double maxLo = deg2Rad( 32 );

		for (int i = 0; i < 35; i++) {
			double deltaLa = maxLa - minLa;
			double deltaLo = maxLo - minLo;

			LaLo[0] = rad2Deg( minLa + 0.5 * deltaLa );
			LaLo[1] = rad2Deg( minLo + 0.5 * deltaLo );

			double[] KKJt = KKJlaloToKKJxy ( LaLo, zone );

			if( KKJt[0] < x ) {
				minLa = minLa + 0.45 * deltaLa;
			} else {
				maxLa = minLa + 0.55 * deltaLa;
			}

			if( KKJt[1] < y ) {
				minLo = minLo + 0.45 * deltaLo;
			} else {
				maxLo = minLo + 0.55 * deltaLo;
			}
		};

		double[] out = {
				LaLo[0],
				LaLo[1]
		};
		return out;
	}

	private double[] KKJlaloToKKJxy (double laLo[], int zone ) {
		double lo = deg2Rad( laLo[1] ) - deg2Rad( KKJ_ZONE_INFO[zone][0] );

		double a = 6378388;
		double f = 1 / 297;

		double b = ( 1.0 - f ) * a;
		double bb = b * b;
		double c = ( a / b ) * a;
		double ee = ( a * a - bb ) / bb;
		double n = ( a - b ) / ( a + b );
		double nn = n * n;

		double cosLa = Math.cos( deg2Rad( laLo[0] ) );
		double NN = ee * cosLa * cosLa;

		double LaF = Math.atan( Math.tan( deg2Rad( laLo[0] ) ) /
				Math.cos( lo * Math.sqrt( 1 + NN ) ) );

		double cosLaF = Math.cos(LaF);
		double t = ( Math.tan( lo ) * cosLaF ) / Math.sqrt( 1 + ee * cosLaF * cosLaF );

		double A = a / ( 1 + n );
		double A1 = A * ( 1 + nn / 4 + nn * nn / 64 );
		double A2 = A * 1.5 * n * ( 1 - nn / 8 );
		double A3 = A * 0.9375 * nn * ( 1 - nn / 4 );
		double A4 = A * 35 / 48 * nn * n;

		double out[] = // P and I
				{ A1 * LaF -
			A2 * Math.sin( 2 * LaF ) +
			A3 * Math.sin( 4 * LaF ) -
			A4 * Math.sin( 6 * LaF ),

			c * Math.log( t + Math.sqrt( 1 + t * t ) ) + 500000 + zone * 1000000};


		return out;
	}

	private double[] KKJlaloToWGS84lalo (double lat,double lon ) {
		double dLa = deg2Rad( 0.124867E+01 +
				-0.269982E+00 * lat +
				0.191330E+00 * lon +
				0.356119E-02 * lat * lat +
				-0.122312E-02 * lat * lon +
				-0.335514E-03 * lon * lon ) / 3600.0;

		double dLo = deg2Rad( -0.286111E+02 +
				0.114183E+01 * lat +
				-0.581428E+00 * lon +
				-0.152421E-01 * lat * lat +
				0.118177E-01 * lat * lon +
				0.826646E-03 * lon * lon ) / 3600.0;

		double[] WGS = {
				rad2Deg( deg2Rad( lat ) + dLa ), // lat
				rad2Deg( deg2Rad( lon ) + dLo ) // lon
		};

		return WGS;
	}

	private int KKJZone (double y ) {
		int zone = (int)Math.floor( y / 1000000 );
		if(zone < 0 || zone > 5)
			throw new IllegalArgumentException("Zone " + zone + " invalid.");
		return zone;
	}

	private double deg2Rad (double deg ) {
		return deg * (Math.PI / 180);
	}

	private double rad2Deg (double rad ) {
		return rad * ( 180 / Math.PI );
	}

}

