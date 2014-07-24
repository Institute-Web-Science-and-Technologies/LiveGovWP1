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
		 this(user, password, "localhost", "gtfsdb");
	}
	
	public PostgresqlDatabase(String user, String password, String host, String db) throws UnavailableException {
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

		String dbAddress = "jdbc:postgresql://"+host+"/"+db+"?autoReconnect=true";
		try {

			connection = DriverManager.getConnection(
					dbAddress, user,
					password);


		} catch (SQLException e) {
			Util.SLDLogger.log().error("dbAdress: " + dbAddress + " user: " + user + " password: " + password);
			Util.SLDLogger.log().error(e);
			throw new UnavailableException(e.getMessage());
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
