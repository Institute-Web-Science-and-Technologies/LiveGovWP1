/**
 * 
 */
package eu.liveandgov.wp1.server.db_helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.*;

import eu.liveandgov.wp1.server.sensor_helper.SensorValueFactory;
import eu.liveandgov.wp1.server.sensor_helper.SensorValueInterface;
import org.postgresql.Driver;

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
public class PostgresqlDatabase {

    public static final String DB_NAME = "liveandgov";
    public static final String DB_USER = "liveandgov";
    public static final String DB_PASS = "liveandgov";

    public Connection connection = null;
	
	public PostgresqlDatabase() {
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
            System.out.println("Where is your PostgreSQL JDBC Driver? Include in your library path!");
            e.printStackTrace();
        }

        System.out.println("Postgres driver version:" + Driver.getVersion());

		Statement stmtLink = null;
		try {
			connection = DriverManager.getConnection("jdbc:postgresql://127.0.0.1:5432/"+ DB_NAME +"?autoReconnect=true", DB_USER, DB_PASS);

			stmtLink = connection.createStatement();

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
			throw new IllegalArgumentException(e.getMessage());
		} finally {
			
			try {
				if (stmtLink != null)
					stmtLink.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
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
