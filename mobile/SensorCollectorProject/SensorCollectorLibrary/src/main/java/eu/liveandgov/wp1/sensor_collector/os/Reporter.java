package eu.liveandgov.wp1.sensor_collector.os;

import android.os.Bundle;

/**
 * <p>Reporter</p>
 * Created by lukashaertel on 08.09.2014.
 */
public interface Reporter {
    /**
     * <p>Gets the report of the current status</p>
     */
    Bundle getReport();
}
