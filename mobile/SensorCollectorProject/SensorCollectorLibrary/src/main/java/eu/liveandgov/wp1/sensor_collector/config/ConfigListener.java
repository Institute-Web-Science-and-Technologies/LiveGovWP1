package eu.liveandgov.wp1.sensor_collector.config;

import eu.liveandgov.wp1.sensor_collector.api.MoraConfig;

/**
 * Created by lukashaertel on 17.11.2014.
 */
public interface ConfigListener {
    void updated(MoraConfig config);
}
