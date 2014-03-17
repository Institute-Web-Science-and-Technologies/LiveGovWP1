package eu.liveandgov.wp1.tests;

import eu.liveandgov.wp1.pipeline.impl.LinesOut;
import eu.liveandgov.wp1.pipeline.impl.ScanIn;
import eu.liveandgov.wp1.pipeline.impl.ZMQServer;
import org.zeromq.ZMQ;

import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * Created by Lukas Härtel on 10.02.14.
 */
public class ZMQServerPipe {
    public static void main(String[] args) throws InterruptedException {
        final ScheduledThreadPoolExecutor ex = new ScheduledThreadPoolExecutor(1);

        ScanIn lip = new ScanIn();
        ZMQServer zcp = new ZMQServer(ex, 50, ZMQ.PULL, "tcp://*:5555");
        LinesOut loc = new LinesOut(System.out);

        lip.setConsumer(zcp);
        zcp.setConsumer(loc);

        lip.readFrom(System.in);
    }
}
