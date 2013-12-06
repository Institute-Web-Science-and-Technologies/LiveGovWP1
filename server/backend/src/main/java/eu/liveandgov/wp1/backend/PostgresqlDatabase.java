/**
 * 
 */
package eu.liveandgov.wp1.backend;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.UnavailableException;

/**
 * @author chrisschaefer
 * 
 * How to install a postgres with postgis extension:
 * 
 * 		 sudo apt-add-repository ppa:ubuntugis/ppa
 * 		 sudo apt-get update
 *       sudo apt-get install postgresql-9.1-postgis;
 *       sudo -s -u postgres;
 *       psql;
 *       CREATE USER myuser WITH PASSWORD 'mypassword';
 *       CREATE DATABASE geodb;
 *       \q
 *       psql -d geodb;
 *       CREATE EXTENSION postgis;
 *       \q
 *       exit;
 *       
 *       sudo -s -u postgres;
 *       psql -d geodb;
 *       GRANT ALL PRIVILEGES ON DATABASE geodb TO myuser;
 *       GRANT SELECT ON spatial_ref_sys TO myuser;
 *		 \q
 *       exit;
 */
public class PostgresqlDatabase extends Database {
	Connection connection = null;
	
	public PostgresqlDatabase(String user, String password) throws UnavailableException {
		try {
			// problem:
			// exception when run it in eclipse+m2+tomcat7: path!java.lang.ClassNotFoundException: org.postgresql.Driver...
			// solution:
			// right click on Project 
			//  -> Properties 
			//  -> Deployment Assembly 
			//  -> Add 
			//  -> Java Build Path Entries 
			//  -> Maven Dependencies
			Class.forName("org.postgresql.Driver");
 
		} catch (ClassNotFoundException e) { 
			System.out.println("Where is your PostgreSQL JDBC Driver? "
					+ "Include in your library path!");
			e.printStackTrace();
			Util.SLDLogger.log().error(e);
		}
		Statement stmtLink = null;
		String dbAddress = "jdbc:postgresql://localhost/gtfsdb?autoReconnect=true";
		try {

			connection = DriverManager.getConnection(
					dbAddress, user,
					password);

			stmtLink = connection.createStatement();

//			String createDevInfoTable = "create table if not exists devinfo ( "
//					+ "uuid integer not null, " + "textuuid text not null, "
//					+ "device text, " + "fingerprint text, " + "id text, "
//					+ "manufacturer text, " + "model text, " + "product text, "
//					+ "androidVersion text, " + "PRIMARY KEY (uuid) )";
//
//			stmtLink.execute(createDevInfoTable);
//
//			String createSampleTable = "create table if not exists samples ( "
//					+ "uuid integer not null, " + "sensorid text not null, "
//					+ "ts bigint not null, " + "prio integer not null, "
//					+ "synced integer default null, "
//					+ "loc text, " // location can be null!
//					+ "data text not null, " + "dataclass text not null, "
//					+ "FOREIGN KEY (uuid) REFERENCES devinfo(uuid) )";
//
//			stmtLink.execute(createSampleTable);

			String createAccelerometerTable = "CREATE TABLE IF NOT EXISTS accelerometer (id VARCHAR(36), ts TIMESTAMP, x FLOAT, y FLOAT, z FLOAT);";
			String createGPSTable = "CREATE TABLE IF NOT EXISTS gps (id VARCHAR(36), ts TIMESTAMP, lonlat GEOGRAPHY(Point));";
			String createTagsTable = "CREATE TABLE IF NOT EXISTS tags (id VARCHAR(36), ts TIMESTAMP, tag TEXT);";
			String createGActivityTable = "CREATE TABLE IF NOT EXISTS google_activity (id VARCHAR(36), ts TIMESTAMP, activity TEXT);";
			String createLACTable = "CREATE TABLE IF NOT EXISTS linear_acceleration (id VARCHAR(36), ts TIMESTAMP, x FLOAT, y FLOAT, z FLOAT);";
			String createGravityTable = "CREATE TABLE IF NOT EXISTS gravity (id VARCHAR(36), ts TIMESTAMP, x FLOAT, y FLOAT, z FLOAT);";

			stmtLink.execute(createAccelerometerTable);
			stmtLink.execute(createGPSTable);
			stmtLink.execute(createTagsTable);
			stmtLink.execute(createGActivityTable);
			stmtLink.execute(createLACTable);
			stmtLink.execute(createGravityTable);

		} catch (SQLException e) {
			Util.SLDLogger.log().error("dbAdress: " + dbAddress + " user: " + user + " password: " + password);
			Util.SLDLogger.log().error(e);
			throw new UnavailableException(e.getMessage());
		} finally {
			
			try {
				if (stmtLink != null)
					stmtLink.close();
			} catch (SQLException e) {
				e.printStackTrace();
				Util.SLDLogger.log().error(e);
			}
		}
	}
	/* (non-Javadoc)
	 * @see eu.liveandgov.wp1.backe29,nd.AbstractDatabase#distanceInMeter(double, double, double, double)
	 */
	@Override
	public double distanceInMeter(double lon0, double lat0, double lon1, double lat1) {
		try {
			Statement stmtLink = connection.createStatement();
 			ResultSet rs = stmtLink.executeQuery("SELECT ST_Distance(ST_GeographyFromText('Point("
					+lon0+ " " + lat0
					+")'),ST_GeographyFromText('Point("
					+ lon1 + " " + lat1
					+ ")'))");
			while (rs.next()) {
				return rs.getDouble(1);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Util.SLDLogger.log().error(e);
		}
		return 0;
	}
	
	protected void finalize() {
		if (connection != null)
			try {
				connection.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Util.SLDLogger.log().error(e);
			}
	}
	
	@Override
	public Statement createStatement() {
		Statement stmtLink = null;
		try {
			return connection.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
			Util.SLDLogger.log().error(e);
		}
		return stmtLink;
	}

}
