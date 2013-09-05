/**
 * 
 */
package eu.liveandgov.wp1.backend;

import static org.junit.Assert.*;

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
		System.out.println("-------- PostgreSQL "
				+ "JDBC Connection Testing ------------");
 
		try {
 
			Class.forName("org.postgresql.Driver");
 
		} catch (ClassNotFoundException e) {
 
			System.out.println("Where is your PostgreSQL JDBC Driver? "
					+ "Include in your library path!");
			e.printStackTrace();
			fail();
 
		}
		try {
			db = new PostgresqlDatabase("myuser", "mypassword");
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
		System.out.println("-------- PostgreSQL "
				+ " distanceInMeter ------------");
			/*
			 *  distance between LAX and NRT (Tokyo/Narita)
			 *  example for LAX
			 *  <span class="geo"><span class="latitude">33.942536</span>°
			 *  <span class="longitude">-118.408075</span>°</span>
			 */
			assertEquals(db.distanceInMeter(-118.4079,33.9434,139.733,35.567), 8833954.76996256, 0.0000001);
	}

}
