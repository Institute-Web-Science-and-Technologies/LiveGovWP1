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
 *       sudo apt-get install postgresql-9.1-postgis;
 *       sudo -s -u postgres;
 *       psql;
 *       CREATE USER myuser WITH PASSWORD 'mypassword';
 *       CREATE DATABASE geodb;
 *       \q
 *       
 *       ---> on my locale pc worked this:
 *       psql -d geodb;
 *       CREATE EXTENSION postgis;
 *       \q
 *       exit;
 *       <---
 *       ---> on our server worked only this:
 *       exit;
 *       sudo -u postgres psql -d geodb -f /usr/share/postgresql/9.1/contrib/postgis-1.5/postgis.sql
 *       sudo -u postgres psql -d geodb -f /usr/share/postgresql/9.1/contrib/postgis-1.5/spatial_ref_sys.sql
 *       <---
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

		Statement stmtLink = null;
		try {
			connection = DriverManager.getConnection(
					"jdbc:postgresql://127.0.0.1:5432/geodb", user,
					password);

			stmtLink = connection.createStatement();

			String createDevInfoTable = "create table if not exists devinfo ( "
					+ "uuid integer not null, " + "textuuid text not null, "
					+ "device text, " + "fingerprint text, " + "id text, "
					+ "manufacturer text, " + "model text, " + "product text, "
					+ "androidVersion text, " + "PRIMARY KEY (uuid) )";

			stmtLink.execute(createDevInfoTable);

			String createSampleTable = "create table if not exists samples ( "
					+ "uuid integer not null, " + "sensorid text not null, "
					+ "ts bigint not null, " + "prio integer not null, "
					+ "synced integer default null, "
					+ "loc text, " // location can be null!
					+ "data text not null, " + "dataclass text not null, "
					+ "FOREIGN KEY (uuid) REFERENCES devinfo(uuid) )";

			stmtLink.execute(createSampleTable);

		} catch (SQLException e) {
			throw new UnavailableException(e.getMessage());
		} finally {
			
			try {
				if (stmtLink != null)
					stmtLink.close();
			} catch (SQLException e) {
				e.printStackTrace();
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
			}
	}
	
	@Override
	public Statement createStatement() {
		Statement stmtLink = null;
		try {
			return connection.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return stmtLink;
	}

}
