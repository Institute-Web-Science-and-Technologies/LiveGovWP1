package eu.liveandgov.wp1.backend;

import static org.junit.Assert.*;

import org.junit.Test;


public class UploadServletTest {
	@Test
	public void testPrintHelloWorld() {
		UploadServlet s = new UploadServlet();
		assertEquals(s.getHelloWorld(), "Hello World");
 
	}
	
}
