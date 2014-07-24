package eu.liveandgov.wp1.tests;

import com.google.common.base.Function;
import eu.liveandgov.wp1.data.impl.Acceleration;
import eu.liveandgov.wp1.data.impl.Motion;
import eu.liveandgov.wp1.pipeline.impl.*;
import eu.liveandgov.wp1.serialization.impl.MotionSerialization;
import org.zeromq.ZMQ;

import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * Created by Lukas Härtel on 10.02.14.
 */
public class ZMQAccVis {
    public static final int CHARS_PER_UNIT = 4;

    public static final char SPACE = '-';
    public static final char CHAR = '#';

    public static void main(String[] args) throws InterruptedException {
        final ScheduledThreadPoolExecutor ex = new ScheduledThreadPoolExecutor(1);

        ScanIn lip = new ScanIn();

        ZMQServer zcp = new ZMQServer(ex, 50, ZMQ.PULL, "tcp://*:5555");

        StartsWith swa = new StartsWith();
        swa.addPrefix("ACC");


        DeSerializer<Motion> mds = new DeSerializer<Motion>(MotionSerialization.MOTION_SERIALIZATION);

        Transformation<Motion, String> out = new Transformation<Motion, String>(new Function<Motion, String>() {
            @Override
            public String apply(Motion motion) {
                final Acceleration acceleration = (Acceleration) motion;

                final double asq = acceleration.values[0] * acceleration.values[0] +
                        acceleration.values[1] * acceleration.values[1] +
                        acceleration.values[2] * acceleration.values[2];

                final double a = Math.sqrt(asq);

                StringBuilder sb = new StringBuilder();
                for (int i = 1; i < a * CHARS_PER_UNIT; i++) {
                    sb.append(SPACE);
                }
                sb.append(CHAR);

                return sb.toString();
            }
        });

        LinesOut loc = new LinesOut(System.out);

        lip.setConsumer(zcp);
        zcp.setConsumer(swa);
        swa.setConsumer(mds);
        mds.setConsumer(out);
        out.setConsumer(loc);

        lip.readFrom(System.in);
    }
}
