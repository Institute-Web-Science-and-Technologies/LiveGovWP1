package eu.liveandgov.wp1.sensor_collector.config;

import java.io.IOException;

import eu.liveandgov.wp1.sensor_collector.api.MoraConfig;

/**
 * <p>Configuration manager and notifier</p>
 * Created by lukashaertel on 17.11.2014.
 */
public interface Configurator {
    /**
     * Adds a listener to the configuration notification chain
     *
     * @param listener   The listener to add
     * @param initialize True if a stub notification should be pushed
     */
    void initListener(ConfigListener listener, boolean initialize);

    /**
     * Gets the current config
     */
    MoraConfig getConfig();

    /**
     * Sets the current config
     *
     * @param config The new config value
     */
    void setConfig(MoraConfig config);

    /**
     * Resets the config to the default
     */
    void resetConfig();

    /**
     * Loads the config
     *
     * @throws IOException Exception thrown by underlying operations
     */
    void loadConfig() throws IOException;

    /**
     * Stores the config
     *
     * @throws IOException Exception thrown by underlying operations
     */
    void storeConfig() throws IOException;
}
