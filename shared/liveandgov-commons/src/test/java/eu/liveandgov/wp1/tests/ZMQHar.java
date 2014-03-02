package eu.liveandgov.wp1.tests;

import eu.liveandgov.wp1.data.DataCommons;
import eu.liveandgov.wp1.data.impl.Activity;
import eu.liveandgov.wp1.pipeline.Consumer;
import eu.liveandgov.wp1.pipeline.impl.DeSerializer;
import eu.liveandgov.wp1.pipeline.impl.LinesIn;
import eu.liveandgov.wp1.pipeline.impl.StartsWith;
import eu.liveandgov.wp1.pipeline.impl.ZMQServer;
import eu.liveandgov.wp1.serialization.impl.ActivitySerialization;
import org.zeromq.ZMQ;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * Created by Lukas Härtel on 10.02.14.
 */
public class ZMQHar {
    public static final int CHARS_PER_UNIT = 4;

    public static final char CHAR = '#';

    public static void main(String[] args) throws InterruptedException {
        final ScheduledThreadPoolExecutor ex = new ScheduledThreadPoolExecutor(1);

        LinesIn lip = new LinesIn();

        ZMQServer zcp = new ZMQServer(ex, 50, ZMQ.PULL, "tcp://*:5555");

        StartsWith swa = new StartsWith();
        swa.addPrefix(DataCommons.TYPE_ACTIVITY + ",");

        DeSerializer<Activity> ads = new DeSerializer<Activity>(ActivitySerialization.ACTIVITY_SERIALIZATION);

        Consumer<Activity> man = new Consumer<Activity>() {
            @Override
            public void push(Activity activity) {
                try {
                    System.out.println(activity.activity);
                    PrintStream p = new PrintStream("data.txt");
                    p.print(activity.activity);
                    p.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        };

        lip.setConsumer(zcp);
        zcp.setConsumer(swa);
        swa.setConsumer(ads);
        ads.setConsumer(man);

        lip.readFrom(System.in);
    }
}
