/**
 * 
 */
package eu.liveandgov.wp1.backend;

import static org.junit.Assert.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.UnavailableException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author chrisschaefer
 *
 */
public class PostgresqlDatabaseTest {
	Database db;
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		try {
 
			Class.forName("org.postgresql.Driver");
 
		} catch (ClassNotFoundException e) {
 
			System.out.println("Where is your PostgreSQL JDBC Driver? "
					+ "Include in your library path!");
			e.printStackTrace();
			fail();
 
		}
		try {
			db = new PostgresqlDatabase("liveandgov", "liveandgov");
		} catch (UnavailableException e) {
			e.printStackTrace();
			fail();
		}
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void distanceInMeter() {
			/*
			 *  distance between LAX and NRT (Tokyo/Narita)
			 *  example for LAX
			 *  <span class="geo"><span class="latitude">33.942536</span>°
			 *  <span class="longitude">-118.408075</span>°</span>
			 */
			assertEquals(db.distanceInMeter(-118.4079,33.9434,139.733,35.567), 8833954.76996256, 0.0000001);
	}
	
	@Test
	public void playWithGeoTable() {
		Statement s = db.createStatement();
		try {
			assertEquals(s.execute("DROP TABLE IF EXISTS airports;"), false);
			assertEquals(s.execute("CREATE TABLE airports (code VARCHAR(3),geog GEOGRAPHY(Point));"), false);
			assertEquals(s.execute("INSERT INTO airports VALUES ('LAX', 'POINT(-118.4079 33.9434)');"), false);
			assertEquals(s.execute("INSERT INTO airports VALUES ('NRT', 'POINT(139.733 35.567)');"), false);
			
			ResultSet rs = s.executeQuery("SELECT ST_Distance(a1.geog,a2.geog) from airports a1, airports a2 where a1.code <> a2.code;");
			while (rs.next()) {
				assertEquals(rs.getDouble(1), 8833954.76996256, 0.0000001);
			}

			assertEquals(s.execute("DROP TABLE airports;"), false);
			s.close();
		} catch (SQLException e) {
			e.printStackTrace();
			fail();
		}

	}
	@Test
	public void kkjToLonLatTest() {
		Statement s = db.createStatement();
		try {
			ResultSet rs = s.executeQuery("SELECT ST_AsText(ST_Transform(ST_GeomFromText('POINT(2553282 6673494)',2392),4326));");
			while (rs.next()) {
				assertEquals(rs.getString(1), "POINT(24.9565985407928 60.1696077230336)");
			}
			s.close();
		} catch (SQLException e) {
			e.printStackTrace();
			fail();
		}

	}
	
}
