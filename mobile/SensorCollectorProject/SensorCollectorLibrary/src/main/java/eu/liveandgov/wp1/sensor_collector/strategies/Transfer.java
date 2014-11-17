package eu.liveandgov.wp1.sensor_collector.strategies;

import com.google.common.io.CharSource;
import com.google.common.io.LineProcessor;

import java.io.IOException;

import eu.liveandgov.wp1.sensor_collector.api.Trip;

/**
 * Created by Pazuzu on 07.10.2014.
 */
public class Transfer {
    public void transferAllStuffSomewhere(Trip trip, CharSource s) {
        try {
            System.out.println("Transfering" + trip);

            s.readLines(new LineProcessor<Object>() {
                @Override
                public boolean processLine(String line) throws IOException {
                    System.out.println(line);
                    return true;
                }

                @Override
                public Object getResult() {
                    return null;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
