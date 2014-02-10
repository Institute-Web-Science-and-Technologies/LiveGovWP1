package eu.liveandgov.wp1.tools;

import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import eu.liveandgov.wp1.pipeline.implementations.LineInProducer;
import eu.liveandgov.wp1.pipeline.implementations.LineOutConsumer;
import eu.liveandgov.wp1.pipeline.implementations.ZMQClientPipeline;
import eu.liveandgov.wp1.pipeline.implementations.ZMQServerPipeline;

import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * Created by Lukas Härtel on 10.02.14.
 */
public class ZMQS {
    public static void main(String[] rawArgs) {
        // Analyze arguments
        final Multimap<String, String> args = ToolsCommon.commands(ToolsCommon.sequentialShorthand(
                "i", "interval",
                "m", "mode",
                "?", "help"
        ), rawArgs);


        // Get main parameter
        final String address = ToolsCommon.end(rawArgs);

        if (!Iterables.isEmpty(args.get("help")) || address == null) {
            System.out.println("usage: [options] address");
            System.out.println("  options:");
            System.out.println("    -i, --interval Poll interval for the response reader");
            System.out.println("    -m, --mode ZMQ socket mode");
            System.out.println("    -?, --help Displays the help");
        }

        if (address != null) {
            // Convert arguments
            final long interval = Long.valueOf(Iterables.getFirst(args.get("interval"), "50"));
            final int mode = Integer.valueOf(Iterables.getFirst(args.get("mode"), "7"));

            final ScheduledThreadPoolExecutor ex = new ScheduledThreadPoolExecutor(1);

            LineInProducer lip = new LineInProducer();
            ZMQServerPipeline zcp = new ZMQServerPipeline(ex, interval, mode, address);
            LineOutConsumer loc = new LineOutConsumer(System.out);

            lip.setConsumer(zcp);
            zcp.setConsumer(loc);

            lip.readFrom(System.in);
        }
    }
}
