package eu.liveandgov.wp1.backend;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.NoSuchElementException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.liveandgov.wp1.backend.SensorValueObjects.RawSensorValue;
import eu.liveandgov.wp1.backend.format.SampleType;

public class WindowStreamTest {
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
	 * @brief Tests the SensorEventStream class using only a single line
	 */
	@Test
	public void processStreamOneElement() {
		String testSample = "ACC,1378130329455,ab85d157c5260ebe,-0.3064578175544739 5.746084213256836 7.4316020011901855 ";
		InputStream is = new ByteArrayInputStream(testSample.getBytes());
		SensorEventStream sensorEventS = SensorEventStream.processStream(is, null);	
	}
	
	/**
	 * @biref Tests the SensorEventStream class with a list of a few items
	 */
	@Test
	public void processStreamList() {
		String testSamples = "TAG,1378130328756,ab85d157c5260ebe,\"stairs\" \n"
							+ "GPS,1378130321132,ab85d157c5260ebe,50.3629052 7.5593997 0 \n"
							+ "ACC,1378130329455,ab85d157c5260ebe,-0.3064578175544739 5.746084213256836 7.4316020011901855 \n"
							+ "ACC,1378130329505,ab85d157c5260ebe,-0.15322890877723694 5.746084213256836 7.508216381072998 \n";
		RawSensorValue testData[] = new RawSensorValue[4];
		testData[0] = new RawSensorValue(SampleType.TAG, 1378130328756L, "ab85d157c5260ebe", "stairs ");
		testData[1] = new RawSensorValue(SampleType.GPS, 1378130321132L, "ab85d157c5260ebe", "50.3629052 7.5593997 0 ");
		testData[2] = new RawSensorValue(SampleType.ACC, 1378130329455L, "ab85d157c5260ebe", "-0.3064578175544739 5.746084213256836 7.4316020011901855 ");
		testData[3] = new RawSensorValue(SampleType.ACC, 1378130329505L, "ab85d157c5260ebe", "-0.15322890877723694 5.746084213256836 7.508216381072998 ");
		InputStream is = new ByteArrayInputStream(testSamples.getBytes());
		SensorEventStream sensorEventS = SensorEventStream.processStream(is, null);
		WindowStream ws = new WindowStream(sensorEventS, 3, 1);
		int windows = 0;
		for (SampleWindow _ : ws) {
			windows++;
		}
		assertEquals(2, windows);
	}
}
