package eu.liveandgov.wp1.backend;

import org.junit.Test;

import junit.framework.Assert;
import junit.framework.TestCase;


public class UploadServletTest extends TestCase {
	@Test
	public void testPrintHelloWorld() {
		UploadServlet s = new UploadServlet();
		Assert.assertEquals(s.getHelloWorld(), "Hello World");
 
	}
}
