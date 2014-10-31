package eu.liveandgov.wp1.sensor_collector.strategies;

import com.google.common.io.CharSource;

import java.io.IOException;

/**
 * Created by Pazuzu on 07.10.2014.
 */
public class Transfer {
    public void transferAllStuffSomewhere(CharSource s) {
        try {
            s.copyTo(System.out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
