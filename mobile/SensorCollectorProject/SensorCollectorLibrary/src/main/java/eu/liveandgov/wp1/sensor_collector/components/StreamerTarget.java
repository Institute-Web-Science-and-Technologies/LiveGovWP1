package eu.liveandgov.wp1.sensor_collector.components;

import android.os.Bundle;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.apache.log4j.Logger;
import org.zeromq.ZMQ;

import java.util.concurrent.ScheduledExecutorService;

import eu.liveandgov.wp1.data.Item;
import eu.liveandgov.wp1.pipeline.impl.ZMQClient;
import eu.liveandgov.wp1.sensor_collector.api.MoraConfig;
import eu.liveandgov.wp1.sensor_collector.config.ConfigListener;
import eu.liveandgov.wp1.sensor_collector.config.Configurator;
import eu.liveandgov.wp1.sensor_collector.logging.LogPrincipal;
import eu.liveandgov.wp1.sensor_collector.os.Reporter;
import eu.liveandgov.wp1.sensor_collector.os.SampleTarget;
import eu.liveandgov.wp1.sensor_collector.serial.ItemSerializer;

/**
 * TODO: Relying on common ZMQ client, may be optimized and pulled into app, lots of constants here
 * Created by lukashaertel on 07.10.2014.
 */
@Singleton
public class StreamerTarget implements SampleTarget, Reporter {
    /**
     * Logger interface
     */
    private static final Logger logger = LogPrincipal.get();

    @Inject
    ItemSerializer itemSerializer;

    private final ZMQClient zmqClient;

    private String address;

    //  ZMQ.Socket s;
    @Inject
    public StreamerTarget(Configurator configurator, ScheduledExecutorService scheduledExecutorService) {
        // Listen for configuration changes
        configurator.initListener(new ConfigListener() {
            @Override
            public void updated(MoraConfig was, MoraConfig config) {
                logger.info("Streamer now pointing to " + config.streaming);
                address = config.streaming;
            }
        }, true);

        zmqClient = new ZMQClient(scheduledExecutorService, 5000, ZMQ.PUB) {
            @Override
            protected String getAddress() {
                return address;
            }

            @Override
            protected void configure(ZMQ.Socket socket) {
                super.configure(socket);

                // Reconnect starts with half a second
                socket.setReconnectIVL(500L);
                // Maximum rate is fifteen seconds
                socket.setReconnectIVLMax(15L * 1000L);
            }
        };
    }

    @Override
    public void handle(Item item) {
        zmqClient.push(itemSerializer.serialize(item) + "\r\n");
    }

    @Override
    public boolean isSilent() {
        return false;
    }

    @Override
    public Bundle getReport() {
        Bundle report = new Bundle();
        report.putString(SPECIAL_KEY_ORIGINATOR, getClass().getSimpleName());

        report.putString("address", address);
        return report;
    }
}
