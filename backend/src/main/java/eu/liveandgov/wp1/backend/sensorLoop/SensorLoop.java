package eu.liveandgov.wp1.backend.sensorLoop;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.supercsv.io.CsvListReader;

import eu.liveandgov.wp1.backend.SensorValueObjects.AccFeatureValue;
import eu.liveandgov.wp1.backend.SensorValueObjects.AccSensorValue;
import eu.liveandgov.wp1.backend.SensorValueObjects.RawSensorValue;
import eu.liveandgov.wp1.backend.SensorValueObjects.SampleWindow;
import eu.liveandgov.wp1.backend.format.SampleType;

public class SensorLoop {
	
	private BufferedReader reader;
	
	public SensorLoop(InputStream is) {
		reader = new BufferedReader(new InputStreamReader(is));
	}
	
	public void doLoop() throws IOException {
		String line = "";
		SampleWindow<AccSensorValue> sw = new SampleWindow<AccSensorValue>(10);
		
		while( (line = reader.readLine()) != null ){
			System.out.println(line);
			
			RawSensorValue rsv = RawSensorValue.fromString(line); 
			System.out.println(rsv);
			
			// Filter accelerometer values
			if (rsv.type != SampleType.ACC) continue;
			System.out.println(rsv);
			
			AccSensorValue asv = AccSensorValue.fromRSV(rsv);
			System.out.println(asv);			

			// Fill sample window
			sw.add(asv);
			if (! sw.isFull()) continue;
			System.out.println(sw);
		
			// sample window is full here
			AccFeatureValue af = AccFeatureValue.fromWindow(sw);			
			System.out.println(af);
		}
	}

	
}
