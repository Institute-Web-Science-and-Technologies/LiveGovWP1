package eu.liveandgov.wp1.backend.sensorLoop;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;

import org.apache.commons.lang3.StringUtils;
import org.supercsv.io.CsvListReader;

import eu.liveandgov.wp1.backend.SensorValueObjects.AccFeatureValue;
import eu.liveandgov.wp1.backend.SensorValueObjects.AccSampleWindow;
import eu.liveandgov.wp1.backend.SensorValueObjects.AccSensorValue;
import eu.liveandgov.wp1.backend.SensorValueObjects.RawSensorValue;
import eu.liveandgov.wp1.backend.SensorValueObjects.TaggedAccFeatureValue;
import eu.liveandgov.wp1.backend.format.SampleType;
import eu.liveandgov.wp1.backend.machineLearning.ActivityRecognition;

public class SensorLoop {
	private BufferedReader reader;
	
	private static final int WINDOW_SIZE = 90;
	private static final int STEP_SIZE = 15;
	
	public SensorLoop(InputStream is) {
		reader = new BufferedReader(new InputStreamReader(is));
	}
	
	public void doLoop() throws Exception {
		String line = "";
		AccSampleWindow sw = new AccSampleWindow(WINDOW_SIZE);
		
		int stepCouter = 0;
		
		String currentTag = "none";
		//CSVFileOutput csvOut = new CSVFileOutput("sensor.csv");
		PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter("/srv/liveandgov/classification/classify.log", true)));
		while( (line = reader.readLine()) != null ) {
			// System.out.println("<- " + line);
			
			RawSensorValue rsv = RawSensorValue.fromString(line); 
			// System.out.println(rsv.toString());

			// Get tags - also we drop the window here so we don't get overlapping windows.
			if(rsv.type == SampleType.TAG) {
				currentTag = StringUtils.strip(rsv.value, " \"");
				sw.drop();
			}
			
			// Filter accelerometer values			
			if (rsv.type != SampleType.ACC) {
//				System.out.println("-> Not of type ACC");
				continue;
			}
			
			AccSensorValue asv = AccSensorValue.fromRSV(rsv);
			// System.out.println(asv.toString());			
			
			// Fill sample window
			sw.add(asv);
			if (! sw.isFull()) { continue; }
			if (! (stepCouter++ % STEP_SIZE == 0)) { continue; }
			//System.out.println(sw.toString());
			
			// sample window is full here
			TaggedAccFeatureValue af = TaggedAccFeatureValue.fromWindow(sw, currentTag);
			writer.println(ActivityRecognition.myClassify(af.toWekaObjArr()));
			//System.out.println(af.toCSV());
		}
		writer.flush();
		writer.close();
	}

	
}
