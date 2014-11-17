package eu.liveandgov.wp1.sensor_collector.strategies;

import com.google.common.io.CharSource;

import java.io.IOException;

import eu.liveandgov.wp1.sensor_collector.api.Trip;

/**
 * Created by Pazuzu on 07.10.2014.
 */
public class Transfer {
    public void transferAllStuffSomewhere(Trip trip, CharSource s) {
        try {
            System.out.println("Transfering" + trip);
            s.copyTo(System.out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
