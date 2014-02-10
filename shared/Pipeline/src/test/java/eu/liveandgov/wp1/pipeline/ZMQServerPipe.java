package eu.liveandgov.wp1.pipeline;

import eu.liveandgov.wp1.pipeline.implementations.LineInProducer;
import eu.liveandgov.wp1.pipeline.implementations.LineOutConsumer;
import eu.liveandgov.wp1.pipeline.implementations.ZMQClientPipeline;
import eu.liveandgov.wp1.pipeline.implementations.ZMQServerPipeline;
import org.jeromq.ZMQ;

import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * Created by Lukas Härtel on 10.02.14.
 */
public class ZMQServerPipe {
    public static void main(String[] args) throws InterruptedException {
        final ScheduledThreadPoolExecutor ex = new ScheduledThreadPoolExecutor(1);

        LineInProducer lip = new LineInProducer();
        ZMQServerPipeline zcp = new ZMQServerPipeline(ex, 50, ZMQ.PULL, "tcp://*:5555");
        LineOutConsumer loc = new LineOutConsumer(System.out);

        lip.setConsumer(zcp);
        zcp.setConsumer(loc);

        lip.readFrom(System.in);
    }
}
