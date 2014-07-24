package eu.liveandgov.wp1.backend;
import static org.junit.Assert.*;
import java.util.Date;


import org.junit.Test;
public class LiveAPITest {
	@Test
	public void getHelsinkiDateAsSimpleDateStringTest() {
		Date d = new Date(1386067336000L); // 2013-12-03 @ 10:42:16am in UTC
	    String s = LiveAPI.getHelsinkiDateAsSimpleDateString(d);
	    assertEquals("2013-12-03 12:42:16", s); // two hours offset in Helsinki
	}
	@Test
	public void getHelsinkiDayStringTest() {
		Date d = new Date(1386067336000L); // 2013-12-03 @ 10:42:16am in UTC
	    String s = LiveAPI.getHelsinkiDayString(d);
	    assertEquals("Tue", s); // two hours offset in Helsinki
	}
}
