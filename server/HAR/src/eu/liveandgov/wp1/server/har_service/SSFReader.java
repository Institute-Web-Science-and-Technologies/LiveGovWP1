package eu.liveandgov.wp1.server.har_service;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.InputMismatchException;

import eu.liveandgov.wp1.data.impl.Acceleration;
import eu.liveandgov.wp1.data.impl.Motion;
import eu.liveandgov.wp1.pipeline.Producer;
import eu.liveandgov.wp1.serialization.impl.MotionSerialization;

public class SSFReader extends Producer<Acceleration> {
	private String device_id;
	
	public String getID() { return this.device_id; }
	
	public void classify(BufferedReader reader) throws IOException {
        String line;
        while((line = reader.readLine())!= null){
        		try {
	        		Motion motion = MotionSerialization.MOTION_SERIALIZATION.deSerialize(line);
	            Acceleration acc = new Acceleration(motion.getTimestamp(), motion.getDevice(), motion.values);
	            this.device_id = acc.getDevice();
	            produce(acc); 
            } catch (InputMismatchException e) {
            		continue;
            }
        		
        }
    }
}
