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
        
        insertValuesInRoutesAsLonLatLinesTable();
        
	}
	
	
	
	public void createTables() throws SQLException {
		Statement s = db.createStatement();
		s.execute("DROP TABLE IF EXISTS lineinfo");
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
		
		s.execute("DROP TABLE IF EXISTS stops");
		s.execute("CREATE TABLE IF NOT EXISTS stops"
				+ "( stopCode INTEGER,"
				//					+ " x_kkj2 INTEGER,"
				//					+ " y_kkj2 INTEGER,"
				//					+ " latitude FLOAT,"
				//					+ " longitude FLOAT,"
				+ " kkj2 GEOMETRY(POINT,2392)," // 2392 == KKJ2 reference system
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
		s.execute("create index stops_idx on stops using gist (kkj2);");
		
		s.execute("DROP TABLE IF EXISTS routes");
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
				+ " kkj2 GEOMETRY(POINT,2392) )"); // 2392 == KKJ2 reference system
		s.execute("create index routes_idx on routes using gist (kkj2);");
		
		s.execute("DROP TABLE IF EXISTS routesAsLonLatLines;");
		s.execute("CREATE TABLE IF NOT EXISTS routesAsLonLatLines"
				+ "( routeCode VARCHAR(6),"
				+ " routeDir VARCHAR(1),"
				+ " validTo DATE,"
				+ " lonlatline GEOMETRY(Linestring,4326) );");
		s.close();
	}

	public void insertValuesInRoutesAsLonLatLinesTable() throws SQLException {
		Statement s = db.createStatement();
		s.execute("INSERT INTO routesAsLonLatLines" +
				"(" +
				"routecode," +
				"routedir," +
				"validto," +
				"lonlatline" +
				")" +
				"select * from " +
				"(" +
					"SELECT r.routecode, r.routedir, r.validto, " +
					"ST_MakeLine(ST_Transform(r.kkj2,4326) ORDER BY r.stoporder) as routePoints " +
					"from routes as r " +
					"group by routecode, routedir, validto" +
				") as foo " +
				"natural join " +
				"(" +
					"select routecode, routedir, max(validto) as validto " +
					"from routes group by routecode, routedir" +
				") as bar");
		s.execute("create index routesAsLonLatLines_idx on routesAsLonLatLines using gist (lonlatline);");
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
		result += delimiter;
		
		// attention please, in postgres X and Y are swapped
		result += "SRID=2392;POINT("
		+ line.substring(14, 21).trim() /* Y_kkj2 */ + " "
		+ line.substring(7, 14).trim() /* X_kkj2 */ + ")";

		// ignore
		// line.substring(21, 29).trim(); // Latitude
		// line.substring(29, 37).trim(); // Longitude

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

		// ignore 
		// line.substring(120, 127).trim(); // X_kkj3
		// line.substring(127, 134).trim(); // Y_kkj3
		
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
	
		// attention please, in postgres X and Y are swapped
		result += "SRID=2392;POINT("
		+ line.substring(42, 49).trim() /* Y_kkj2 */ + " "
		+ line.substring(35, 42).trim() /* X_kkj2 */ + ")";

		return result;
	}
}

