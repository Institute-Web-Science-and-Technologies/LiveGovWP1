package eu.liveandgov.wp1.sensor_collector.components;

import android.os.Bundle;

import com.google.common.base.Objects;

import eu.liveandgov.wp1.sensor_collector.api.MoraConfig;
import eu.liveandgov.wp1.sensor_collector.config.ConfigListener;
import eu.liveandgov.wp1.sensor_collector.config.Configurator;
import eu.liveandgov.wp1.sensor_collector.os.Reporter;
import eu.liveandgov.wp1.sensor_collector.os.SampleSource;

/**
 * <p>
 * Regular case of a sample source, this includes maintaining activation state, configuration
 * management, activation and deactivation policies, as well as report generation
 * </p>
 * <p>
 * Created on 05.12.2014.
 * </p>
 *
 * @author lukashaertel
 */
public abstract class RegularSampleSource implements SampleSource, Reporter {
    /**
     * <p>Store for the configurator</p>
     */
    private final Configurator configurator;

    /**
     * <p>Gets the configurator used to configure this source</p>
     *
     * @return Returns the configurator
     */
    protected Configurator getConfigurator() {
        return configurator;
    }

    /**
     * <p>Activation flag</p>
     */
    private boolean active;

    /**
     * <p>Constructs the regular sample source on a configurator</p>
     *
     * @param configurator The configurator to use
     */
    protected RegularSampleSource(Configurator configurator) {
        this.configurator = configurator;

        // Add the configuration change listener
        configurator.initListener(new ConfigListener() {
            @Override
            public void updated(MoraConfig was, MoraConfig config) {
                // Default update strategy is reactivation
                if (!isActive())
                    return;

                if (Objects.equal(getDelay(was), getDelay(config))) {
                    deactivate();
                    activate();
                }
            }
        }, false);

    }

    /**
     * <p>Returns true if the system sensor is available</p>
     *
     * @return Returns availability
     */
    protected boolean isAvailable() {
        return true;
    }

    @Override
    public void activate() {
        if (active) return;

        if (!isAvailable()) return;

        if (getDelay(configurator.getConfig()) == null)
            return;

        try {
            handleActivation();
        } finally {
            active = true;
        }
    }

    /**
     * <p>Handles the actual activation after checking of the policies</p>
     */
    protected abstract void handleActivation();

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void deactivate() {
        if (!active) return;

        if (!isAvailable()) return;

        if (getDelay(configurator.getConfig()) == null)
            return;

        try {
            handleDeactivation();
        } finally {
            active = false;
        }
    }

    /**
     * <p>Handles the actual deactivation after checking of the policies</p>
     */
    protected abstract void handleDeactivation();

    /**
     * <p>Extracts the delay specification for this sample source</p>
     *
     * @param config The configuration object
     * @return Returns a number if enabled, <code>null</code> if disabled
     */
    protected abstract Integer getDelay(MoraConfig config);

    /**
     * <p>Checks if the sensor is currently enabled</p>
     *
     * @return Returns true if the delay is not null
     */
    protected boolean isCurrentlyEnabled() {
        return getCurrentDelay() != null;
    }

    /**
     * <p>Gets the current delay</p>
     *
     * @return Returns the current delay value
     */
    protected Integer getCurrentDelay() {
        return getDelay(getConfigurator().getConfig());
    }

    @Override
    public Bundle getReport() {
        Bundle report = new Bundle();
        report.putString(SPECIAL_KEY_ORIGINATOR, getClass().getSimpleName());

        if (getDelay(configurator.getConfig()) != null) {
            report.putBoolean("enabled", true);
            report.putInt("delay", getDelay(configurator.getConfig()));
        } else
            report.putBoolean("enabled", false);

        return report;
    }
}
