package eu.liveandgov.wp1.server.har_service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import org.postgresql.Driver;


public class PostgresqlDB {
	private static final String DB_NAME = "liveandgov";
	private static final String DB_USER = "liveandgov";
	private static final String DB_PASS = "liveandgov";
	
	private static final String INSERT_PREPARED = "INSERT INTO har_service VALUES (?,?,?);";
	
    private Connection connection = null;

	
	public PostgresqlDB() {
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			System.out.println("Postgres driver not found");
			e.printStackTrace();
		}
		
		Statement stmt = null;
		try {
			connection = DriverManager.getConnection("jdbc:postgresql://127.0.0.1:5432/"+ DB_NAME +"?autoReconnect=true", DB_USER, DB_PASS);
			
			stmt = connection.createStatement();
			
			String createHARTable = "CREATE TABLE IF NOT EXISTS har_service (id VARCHAR(36), ts BIGINT, result VARCHAR(36));";
			stmt.execute(createHARTable);
			
		} catch (SQLException e) {
			System.out.println("Error creating table");
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					stmt.close();
			} catch(SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void logResult(String userId, String result) {
		long curTime = System.currentTimeMillis();
		PreparedStatement insert = null;
		try {
			insert = connection.prepareStatement(INSERT_PREPARED);
			insert.setString(1, userId);
			insert.setLong(2, curTime);
			insert.setString(3, result);
			insert.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (insert != null)
					insert.close();
			} catch(SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
