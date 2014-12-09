package eu.liveandgov.wp1.sensor_collector.config;

import java.io.IOException;

import eu.liveandgov.wp1.sensor_collector.api.MoraConfig;

/**
 * <p>Configuration manager and notifier</p>
 * Created by lukashaertel on 17.11.2014.
 */
public interface Configurator {
    /**
     * <p>Adds a listener to the configuration notification chain</p>
     *
     * @param listener   The listener to add
     * @param initialize True if a stub notification should be pushed
     */
    void initListener(ConfigListener listener, boolean initialize);

    /**
     * <p>Gets the current config</p>
     */
    MoraConfig getConfig();

    /**
     * <p>Sets the current config</p>
     *
     * @param config The new config value
     */
    void setConfig(MoraConfig config);

    /**
     * <p>Resets the config to the default</p>
     */
    void resetConfig();

    /**
     * <p>Loads the config</p>
     *
     * @throws IOException Exception thrown by underlying operations
     */
    void loadConfig() throws IOException;

    /**
     * <p>Stores the config</p>
     *
     * @throws IOException Exception thrown by underlying operations
     */
    void storeConfig() throws IOException;
}
