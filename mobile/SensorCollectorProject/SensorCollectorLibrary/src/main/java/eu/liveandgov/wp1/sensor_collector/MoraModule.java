package eu.liveandgov.wp1.sensor_collector;

import com.google.inject.AbstractModule;

import eu.liveandgov.wp1.sensor_collector.os.BasicOS;
import eu.liveandgov.wp1.sensor_collector.os.OS;

/**
 * Created by lukashaertel on 08.09.2014.
 */
public class MoraModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(OS.class).to(BasicOS.class);
    }
}
