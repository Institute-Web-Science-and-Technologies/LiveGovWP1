package eu.liveandgov.wp1.tools;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import eu.liveandgov.wp1.data.impl.GPS;
import eu.liveandgov.wp1.data.impl.Waiting;
import eu.liveandgov.wp1.pipeline.impl.LinesOut;
import eu.liveandgov.wp1.pipeline.impl.ScanIn;
import eu.liveandgov.wp1.pipeline.impl.Transformation;
import eu.liveandgov.wp1.pps.PPSPipeline;
import eu.liveandgov.wp1.pps.api.AggregatingPS;
import eu.liveandgov.wp1.pps.api.csv.StaticIPS;
import eu.liveandgov.wp1.pps.api.gi.GridIndexPS;
import eu.liveandgov.wp1.pps.api.ooapi.OSMIPPS;
import eu.liveandgov.wp1.serialization.Serializations;
import eu.liveandgov.wp1.serialization.impl.GPSSerialization;
import eu.liveandgov.wp1.serialization.impl.WaitingSerialization;
import eu.liveandgov.wp1.waiting.WaitingPipeline;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.Callable;

/**
 * Created by Lukas Härtel on 11.02.14.
 */
public class WEV {
    public static void main(String[] rawArgs) {
        final Multimap<String, String> args = HashMultimap.create();

        try {
            ToolsCommon.config(args, new File("default.config"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Analyze arguments
        ToolsCommon.commands(args,
                ToolsCommon.oneOf(
                        "help"
                ),
                ToolsCommon.sequentialShorthand(
                        "h", "hres",
                        "v", "vres",
                        "c", "centroid",
                        "s", "storedegree",
                        "d", "distance",
                        "l", "local",
                        "r", "remote",
                        "k", "key",
                        "t", "time",
                        "?", "help"
                ), rawArgs);

        try {
            ToolsCommon.config(args, new File("override.config"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (!Iterables.isEmpty(args.get("help"))) {
            System.out.println("usage: [options]");
            System.out.println("  options:");
            System.out.println("    -h, --hres DOUBLE         Horizontal resolution of the grid in degrees");
            System.out.println("    -v, --vres DOUBLE         Vertical resolution of the grid in degrees");
            System.out.println("    -c, --centroid BOOLEAN    True if check is by centroid instead of first entry point");
            System.out.println("    -s, --storedegree INTEGER Amount of results to be cached");
            System.out.println("    -d, --distance DOUBLE     Maximum distance to identify an object as proxy");
            System.out.println("    -l, --local STRING        Adds a .csv file to the local sources");
            System.out.println("    -r, --remote STRING       Adds a overpass url to the remote sources");
            System.out.println("    -k, --key STRING          The key of proximity objects to filter");
            System.out.println("    -t, --time LONG           The time considered waiting time");
            System.out.println("    -?, --help                Displays the help");
        }

        final double hres = Double.valueOf(Iterables.getFirst(args.get("hres"), "4.0484081228265274681719312721498e-10"));
        final double vres = Double.valueOf(Iterables.getFirst(args.get("vres"), "4.0484081228265274681719312721498e-10"));
        final boolean centroid = Boolean.valueOf(Iterables.getFirst(args.get("centroid"), "true"));
        final int storedegree = Integer.valueOf(Iterables.getFirst(args.get("storedegree"), "8192"));
        final double distance = Double.valueOf(Iterables.getFirst(args.get("distance"), "25.0"));
        final String key = Iterables.getFirst(args.get("key"), "proximity");
        final long time = Long.valueOf(Iterables.getFirst(args.get("time"), "30000"));

        final AggregatingPS aps = new AggregatingPS();

        final Map<GridIndexPS, String> storesOf = new HashMap<GridIndexPS, String>();

        for (final String local : args.get("local")) {
            final Scanner scanner = new Scanner(local);
            scanner.useDelimiter(";");

            final int id = scanner.nextInt();
            final int lat = scanner.nextInt();
            final int lon = scanner.nextInt();
            final boolean universal = scanner.nextBoolean();
            final String store = scanner.next();
            scanner.skip(scanner.delimiter());
            final String file = scanner.nextLine();

            final StaticIPS ps = new StaticIPS(hres, vres, centroid, storedegree, new Callable<InputStream>() {
                @Override
                public InputStream call() throws IOException {
                    return new FileInputStream(file);
                }
            }, universal, id, lat, lon, distance);

            storesOf.put(ps, store);
            ps.tryLoad(new File(store + ".store"));

            aps.getProximityServices().add(ps);
        }

        for (final String remote : args.get("remote")) {
            final Scanner scanner = new Scanner(remote);
            scanner.useDelimiter(";");

            final String store = scanner.next();
            scanner.skip(scanner.delimiter());
            final String url = scanner.nextLine();

            final OSMIPPS ps = new OSMIPPS(hres, vres, centroid, storedegree, url, distance);

            storesOf.put(ps, store);
            ps.tryLoad(new File(store + ".store"));

            aps.getProximityServices().add(ps);
        }

        // Setup input
        final ScanIn input = new ScanIn();

        // Setup parser
        final Transformation<String, GPS> deSerializer = new Transformation<String, GPS>(Serializations.deSerialization(GPSSerialization.GPS_SERIALIZATION));
        input.setConsumer(deSerializer);

        // Setup proximity eu.liveandgov.wp1.pipeline
        final PPSPipeline ppsPipeline = new PPSPipeline(key, aps);
        deSerializer.setConsumer(ppsPipeline);

        // Setup waiting eu.liveandgov.wp1.pipeline
        final WaitingPipeline waitingPipeline = new WaitingPipeline(key, time);
        ppsPipeline.setConsumer(waitingPipeline);

        // Setup serializer
        final Transformation<Waiting, String> serializer = new Transformation<Waiting, String>(Serializations.serialization(WaitingSerialization.WAITING_SERIALIZATION));
        waitingPipeline.setConsumer(serializer);

        // Setup output
        final LinesOut output = new LinesOut(System.out);
        serializer.setConsumer(output);

        // Begin transaction
        input.readFrom(System.in);

        for (Map.Entry<GridIndexPS, String> e : storesOf.entrySet()) {
            e.getKey().trySave(new File(e.getValue() + ".store"));
        }
    }


}
