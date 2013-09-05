package eu.liveandgov.wp1.backend;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class HTLDataETLTest {
	HTLDataETL htlDataEtl;
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		htlDataEtl = new HTLDataETL();
	}
	@Test
	public void transferKkjToLonLatTest() {
		
		
//		assertEquals(o.transferKkjToLonLat(kkjX, kkjY), 8833954.76996256, 0.0000001);
	}
	@Test
	public void lineExtractionTest() {
		String input = " 1001  2012042720120603rSalutorget - Kottby                                         Salutorget          Kottby              10304231250431068750701902";
		String expected = "1001|20120427|20120603|r|Salutorget - Kottby|Salutorget|Kottby|1030423|1250431|06875|07019|02";
		String output = htlDataEtl.extractLineInfo(input, "|");
		assertEquals(output,expected);
	}
	@Test
	public void routeExtractionTest() {
		String input = " 1001  120110815202012311030423P000166732322553016";
		String expected = "1001|1|20110815|20201231|1030423|P|0001|POINT(24.947662 59.941050)";
		String output = htlDataEtl.extractRouteInfo(input, "|");
		assertEquals(output,expected);
	}
	@Test
	public void stopExtractionTest() {
		String input = " 10101026673494255328260.1694624.95667Ritarihuone         Riddarhuset         Mariankatu          Mariegatan             66748563386744                                        01H 200760.1696124.95679M3               40";
		String expected = "1010102|Point(24.95667 60.16946)|Ritarihuone|Riddarhuset|Mariankatu|Mariegatan||||01|H 2007|M|3|";
		String output = htlDataEtl.extractStopInfo(input, "|");
		assertEquals(output,expected);
	}
	
	@Test
	public void extractTransferLoadTest() {
		// set importData to true if you want to import the HTL dataset
		boolean importData = false;
		try {
			if(importData) {
				htlDataEtl.extractTransferLoad();
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
}
