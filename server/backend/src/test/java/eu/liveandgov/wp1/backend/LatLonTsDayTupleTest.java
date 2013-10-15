package eu.liveandgov.wp1.backend;
import static org.junit.Assert.*;

import java.text.ParseException;
import org.junit.Test;
public class LatLonTsDayTupleTest {
	@Test
	public void constructorTest() throws ParseException {
		LatLonTsDayTuple t = new LatLonTsDayTuple("60.1652805,24.95296666,2013-09-23 20:06:36,Mon");
		assertEquals("POINT(24.95297 60.16528)", t.getLonLatPoint());
		assertEquals("20130923", t.getDateDigitsOnly());
		assertEquals("200636", t.getDaytimeDigitsOnly());
		assertEquals("Monday", t.getWeekdayName());
		assertEquals("2013-09-23 20:06:36", t.getUTC());
		assertEquals("2013-09-23", t.getISO8601Date());
		
	}

	@Test
	public void timeBetweenClauseTest() throws ParseException {
		LatLonTsDayTuple t = new LatLonTsDayTuple("60.1652805,24.95296666,2013-09-23 20:06:36,Mon");
		assertEquals("195936 AND 201336", t.getBetweenTimeClause(7));
		t = new LatLonTsDayTuple("60.1652805,24.95296666,2013-09-23 12:36:14,Mon");
		assertEquals("123114 AND 124114", t.getBetweenTimeClause(5));
	}
	
}
