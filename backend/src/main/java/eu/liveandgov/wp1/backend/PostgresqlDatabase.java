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
	public double distanceInMeter(double lat0, double lon0, double lat1, double lon1) {
		try {
			System.out.println("foo");
			Statement stmtLink = connection.createStatement();
			System.out.println("bar");
			ResultSet rs = stmtLink.executeQuery("SELECT ST_Distance(ST_GeographyFromText('Point("
					+lat0+ " " + lon0
					+")'),ST_GeographyFromText('Point("
					+ lat1 + " " + lon1
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
				System.out.println("-------- PostgreSQL "
						+ " connection.close(); ------------");
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

}
