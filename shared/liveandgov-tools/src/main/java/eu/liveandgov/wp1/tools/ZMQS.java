package eu.liveandgov.wp1.tools;

import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import eu.liveandgov.wp1.pipeline.impl.LinesIn;
import eu.liveandgov.wp1.pipeline.impl.LinesOut;
import eu.liveandgov.wp1.pipeline.impl.ZMQServer;

import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * Created by Lukas Härtel on 10.02.14.
 */
public class ZMQS {
    public static void main(String[] rawArgs) {
        // Analyze arguments
        final Multimap<String, String> args = ToolsCommon.commands(
                ToolsCommon.oneOf(
                        "help"
                ),
                ToolsCommon.sequentialShorthand(
                        "i", "interval",
                        "m", "mode",
                        "t", "topic",
                        "?", "help"
                ), rawArgs);


        // Get main parameter
        final String address = ToolsCommon.end(rawArgs);

        if (!Iterables.isEmpty(args.get("help")) || address == null) {
            System.out.println("usage: [options] address");
            System.out.println("  Opens a ZMQ server with the options specified");
            System.out.println("  options:");
            System.out.println("    -i, --interval INTEGER Poll interval for the response reader");
            System.out.println("    -m, --mode INTEGER ZMQ socket mode");
            System.out.println("    -t, --topic STRING Adds a topic to the subscriptions");
            System.out.println("    -?, --help Displays the help");
        }

        if (address != null) {
            // Convert arguments
            final long interval = Long.valueOf(Iterables.getFirst(args.get("interval"), "50"));
            final int mode = Integer.valueOf(Iterables.getFirst(args.get("mode"), "7"));

            final ScheduledThreadPoolExecutor ex = new ScheduledThreadPoolExecutor(1);

            LinesIn lip = new LinesIn();
            ZMQServer zcp = new ZMQServer(ex, interval, mode, address);
            LinesOut loc = new LinesOut(System.out);

            for (String topic : args.get("topic")) {
                zcp.subscribe(topic);
            }

            lip.setConsumer(zcp);
            zcp.setConsumer(loc);

            lip.readFrom(System.in);
        }
    }
}
