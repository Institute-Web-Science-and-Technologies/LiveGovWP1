package eu.liveandgov.wp1.tests;

import eu.liveandgov.wp1.pipeline.impl.LinesIn;
import eu.liveandgov.wp1.pipeline.impl.LinesOut;
import eu.liveandgov.wp1.pipeline.impl.ZMQClient;
import org.zeromq.ZMQ;

import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * Created by Lukas Härtel on 10.02.14.
 */
public class ZMQClientPipe {
    public static void main(String[] args) throws InterruptedException {
        final ScheduledThreadPoolExecutor ex = new ScheduledThreadPoolExecutor(1);

        LinesIn lip = new LinesIn();
        ZMQClient zcp = new ZMQClient(ex, 50, ZMQ.PUSH) {

            @Override
            protected String getAddress() {
                return "tcp://lg:5556";
            }
        };
        LinesOut loc = new LinesOut(System.out);

        lip.setConsumer(zcp);
        zcp.setConsumer(loc);

        lip.readFrom(System.in);
    }
}
