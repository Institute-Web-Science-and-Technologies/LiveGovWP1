package eu.liveandgov.wp1.backend;
import static org.junit.Assert.*;

import java.text.ParseException;
import org.junit.Test;
public class LatLonTsDayTupleTest {
	@Test
	public void constructorTest() throws ParseException {
		LatLonTsDayTuple t = new LatLonTsDayTuple("60.1652805,24.95296666,2013-09-23 20:06:36,Mon");
		assertEquals("POINT(24.95297 60.16528)", t.getLonLatPoint());
		assertEquals("Monday", t.getWeekdayName());
		assertEquals("2013-09-23", t.getISO8601Date());
		
	}

	@Test
	public void timeBetweenClauseTest() throws ParseException {
		LatLonTsDayTuple t = new LatLonTsDayTuple("60.1652805,24.95296666,2013-09-23 22:59:59,Mon");
		assertEquals("arrival_time>=" + ((22*3600 + 59*60 + 59)-(3*60)) + " AND arrival_time<" + ((22*3600 + 59*60 + 59)+(3*60)), t.getBetweenTimeClause("arrival_time",3));
		t = new LatLonTsDayTuple("60.1652805,24.95296666,2013-09-23 12:36:14,Mon");
		assertEquals("arrival_time>=" + (12*3600 + 36*60 + 14) + " AND arrival_time<" + (12*3600 + 36*60 + 14), t.getBetweenTimeClause("arrival_time",0));
	}
	
	@Test
	public void getBoundingBoxTest() throws ParseException {
		LatLonTsDayTuple t = new LatLonTsDayTuple("60.1652805,24.95296666,2013-09-23 22:59:59,Mon");
		String bb = t.getBoundingBox(0);
		assertEquals("ST_MakeBox2D(St_geometryfromtext('POINT(24.95297 60.16528)',4326),St_geometryfromtext('POINT(24.95297 60.16528)',4326))", bb);
		bb = t.getBoundingBox(10);
		assertEquals("ST_MakeBox2D(St_geometryfromtext('POINT(24.95315 60.16537)',4326),St_geometryfromtext('POINT(24.95279 60.16519)',4326))", bb);
	}
	
}
