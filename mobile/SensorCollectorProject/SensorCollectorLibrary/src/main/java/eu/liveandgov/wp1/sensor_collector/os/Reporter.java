package eu.liveandgov.wp1.sensor_collector.os;

import android.os.Bundle;

/**
 * <p>Reporter</p>
 * <p>TODO: Make a central report repository instead instead of adding them manually, see {@link eu.liveandgov.wp1.sensor_collector.config.Configurator} and {@link eu.liveandgov.wp1.sensor_collector.config.Configurator#initListener(eu.liveandgov.wp1.sensor_collector.config.ConfigListener, boolean)} usage</p>
 * Created by lukashaertel on 08.09.2014.
 */
public interface Reporter {
    /**
     * Assign this key if the originator of the report is available
     */
    public static final String SPECIAL_KEY_ORIGINATOR = "originator";

    /**
     * <p>Gets the report of the current status</p>
     */
    Bundle getReport();
}
