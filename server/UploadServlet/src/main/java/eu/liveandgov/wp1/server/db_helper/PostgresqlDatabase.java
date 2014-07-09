/**
 * 
 */
package eu.liveandgov.wp1.server.db_helper;

import java.sql.*;

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

    public static final String DB_NAME = "liveandgov_field";
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

            String createTripTable = "CREATE TABLE IF NOT EXISTS trip (trip_id SERIAL, user_id VARCHAR(36), start_ts BIGINT, stop_ts BIGINT, name VARCHAR(255));";
            stmtLink.execute(createTripTable);

            // TODO: Create Sensor Tables

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
