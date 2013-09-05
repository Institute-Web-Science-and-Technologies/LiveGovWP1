package eu.liveandgov.wp1.backend;

import static org.junit.Assert.*;

import org.junit.Test;


public class UploadServletTest {
	@Test
	public void testPrintHelloWorld() {
		OldUploadServlet s = new OldUploadServlet();
		assertEquals(s.getHelloWorld(), "Hello World");
 
	}
	
}
