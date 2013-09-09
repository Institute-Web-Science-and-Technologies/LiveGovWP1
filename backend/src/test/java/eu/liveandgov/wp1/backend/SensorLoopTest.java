package eu.liveandgov.wp1.backend;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

import eu.liveandgov.wp1.backend.sensorLoop.SensorLoop;

public class SensorLoopTest {

	public static final String FILENAME = "test-upload-data.txt";
	
	@Test
	public void test() throws Exception {
		
		InputStream is = new FileInputStream(FILENAME);
		
		SensorLoop l = new SensorLoop(is, "test");
		
		l.doLoop();
	}

}
