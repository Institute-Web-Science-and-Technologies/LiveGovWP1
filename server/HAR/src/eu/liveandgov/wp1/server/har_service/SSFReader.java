package eu.liveandgov.wp1.server.har_service;

import java.io.BufferedReader;
import java.io.IOException;

import eu.liveandgov.wp1.data.impl.Acceleration;
import eu.liveandgov.wp1.data.impl.Motion;
import eu.liveandgov.wp1.pipeline.Producer;
import eu.liveandgov.wp1.serialization.impl.MotionSerialization;

public class SSFReader extends Producer<Acceleration> {
	public void classify(BufferedReader reader) throws IOException {
        String line;
        while((line = reader.readLine())!= null){
        	Motion motion = MotionSerialization.MOTION_SERIALIZATION.deSerialize(line);
            Acceleration acc = new Acceleration(motion.getTimestamp(), motion.getDevice(), motion.values);
            produce(acc);
        }
    }
}
