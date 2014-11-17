package eu.liveandgov.wp1.sensor_collector.components;

import com.google.inject.Inject;

import org.apache.log4j.Logger;

import eu.liveandgov.wp1.data.Item;
import eu.liveandgov.wp1.sensor_collector.api.MoraConfig;
import eu.liveandgov.wp1.sensor_collector.config.ConfigListener;
import eu.liveandgov.wp1.sensor_collector.config.Configurator;
import eu.liveandgov.wp1.sensor_collector.logging.LogPrincipal;
import eu.liveandgov.wp1.sensor_collector.os.SampleTarget;

/**
 * Created by lukashaertel on 07.10.2014.
 */
public class StreamerTarget implements SampleTarget {
    /**
     * Logger interface
     */
    private static final Logger logger = LogPrincipal.get();

    //  ZMQ.Socket s;
    @Inject
    public StreamerTarget(Configurator configurator) {
        // Listen for configuration changes
        configurator.initListener(new ConfigListener() {
            @Override
            public void updated(MoraConfig was, MoraConfig config) {
                logger.info("Streamer now pointing to " + config.streaming);
            }
        }, true);
        //  ZMQ.Context x = ZMQ.context(1);
        //  s = x.socket(ZMQ.PUB);
        //  s.connect("tcp://liveandgov.uni-koblenz.de:5555");

    }

    @Override
    public void handle(Item item) {
        System.out.println(item.toSerializedForm());
    }
}
