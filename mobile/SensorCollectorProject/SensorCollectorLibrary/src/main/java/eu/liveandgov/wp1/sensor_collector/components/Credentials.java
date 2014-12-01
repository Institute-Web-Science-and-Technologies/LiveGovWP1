package eu.liveandgov.wp1.sensor_collector.components;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.apache.commons.math3.random.RandomDataGenerator;

import eu.liveandgov.wp1.sensor_collector.api.MoraConfig;
import eu.liveandgov.wp1.sensor_collector.config.ConfigListener;
import eu.liveandgov.wp1.sensor_collector.config.Configurator;
import eu.liveandgov.wp1.sensor_collector.util.MoraStrings;

/**
 * TODO Fill in the blank
 * Created by lukashaertel on 17.11.2014.
 */
@Singleton
public class Credentials {
    @Inject
    public Credentials(Configurator configurator){
        configurator.initListener(new ConfigListener() {
            @Override
            public void updated(MoraConfig was, MoraConfig config) {
                user = config.user;
                secret = MoraStrings.randomAlphanumeric(config.hashCode(), config.secretLength);
            }
        }, true);
    }

    /**
     * The user identity
     */
    public String user;

    /**
     * The user secret
     */
    public String secret;
}
