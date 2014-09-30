/**
 * 
 */
package eu.liveandgov.wp1.server.db_helper;

import eu.liveandgov.wp1.server.CONFIG;
import eu.liveandgov.wp1.server.SQL;
import org.apache.log4j.Logger;
import org.postgresql.Driver;

import java.sql.*;

import static junit.framework.Assert.assertNotNull;

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
    private static final Logger Log = Logger.getLogger(PostgresqlDatabase.class);

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
            Log.error("Where is your PostgreSQL JDBC Driver? Include in your library path!");
        }

        Log.debug("Postgres driver version:" + Driver.getVersion());

		Statement stmtLink = null;
		try {
            Log.debug("Connecting to DB");
			connection = DriverManager.getConnection(CONFIG.JDBC_CONNECTION, CONFIG.DB_USER, CONFIG.DB_PASS);

            stmtLink = connection.createStatement();
            stmtLink.execute(SQL.CREATE_TRIP_TABLE);
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		} finally {
			try {
				if (stmtLink != null)
					stmtLink.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

    public void setSecret(String user, String secret){
        assertNotNull(connection);
        if (secret == null) { return; }

        try {
            connection.createStatement().execute(SQL.CREATE_AUTH_TABLE);

            PreparedStatement q = connection.prepareStatement("DELETE FROM auth WHERE user_id = ?;");
            q.setString(1,user);
            q.execute();

            PreparedStatement p = connection.prepareStatement("INSERT INTO auth VALUES (? , ?);");
            p.setString(1, user);
            p.setString(2, secret);
            p.execute();
        } catch (SQLException e) {
            e.printStackTrace();
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
