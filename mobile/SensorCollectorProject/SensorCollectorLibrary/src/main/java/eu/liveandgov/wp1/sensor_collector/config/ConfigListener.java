package eu.liveandgov.wp1.sensor_collector.config;

import eu.liveandgov.wp1.sensor_collector.api.MoraConfig;

/**
 * <p>Listener hook for configuration changes</p>
 * Created by lukashaertel on 17.11.2014.
 */
public interface ConfigListener {
    /**
     * Called when the configuration has been updates
     *
     * @param was    The old configuration or null if initial
     * @param config The new configuration item
     */
    void updated(MoraConfig was, MoraConfig config);
}
