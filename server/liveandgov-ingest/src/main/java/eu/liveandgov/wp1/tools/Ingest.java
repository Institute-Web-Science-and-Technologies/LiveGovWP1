package eu.liveandgov.wp1.tools;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.sun.rowset.JdbcRowSetImpl;
import eu.liveandgov.wp1.data.Callback;
import eu.liveandgov.wp1.data.Item;
import eu.liveandgov.wp1.data.impl.GPS;
import eu.liveandgov.wp1.pipeline.impl.*;
import eu.liveandgov.wp1.serialization.impl.ItemSerialization;

import javax.sql.rowset.JdbcRowSet;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Scanner;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import static eu.liveandgov.wp1.serialization.SerializationCommons.SLASH_SEMICOLON_SEPARATED;

/**
 * Created by Lukas Härtel on 11.05.2014.
 */
public class Ingest {
    private static class Trip {
        public final int trip_id;

        public final String user_id;

        public final long start_ts;

        public final long end_ts;

        public final String name;

        private Trip(int trip_id, String user_id, long start_ts, long end_ts, String name) {
            this.trip_id = trip_id;
            this.user_id = user_id;
            this.start_ts = start_ts;
            this.end_ts = end_ts;
            this.name = name;
        }
    }

    private static Trip currentTrip = null;

    public static void main(String[] rawArgs) throws SQLException, IOException {
        ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(2);

        String url = "jdbc:jdbc:postgresql:data";
        String username = "user";
        String password = "default";

        final String tripTable = "trip";
        final DB<Trip, Trip> trip = new DB<Trip, Trip>(scheduledExecutorService, url, username, password, tripTable) {
            @Override
            protected Trip read(RowReader rowReader) throws SQLException {
                int trip_id = (Integer) rowReader.read("trip_id");
                String user_id = (String) rowReader.read("user_id");
                long start_ts = (Long) rowReader.read("start_ts");
                long end_ts = (Long) rowReader.read("end_ts");
                String name = (String) rowReader.read("name");

                return new Trip(trip_id, user_id, start_ts, end_ts, name);
            }

            @Override
            protected void write(Trip trip, RowWriter rowWriter) throws SQLException {
                if (rowWriter.required().contains("trip_id"))
                    rowWriter.write("trip_id", trip.trip_id);

                if (rowWriter.required().contains("user_id"))
                    rowWriter.write("user_id", trip.user_id);

                if (rowWriter.required().contains("start_ts"))
                    rowWriter.write("start_ts", trip.start_ts);

                if (rowWriter.required().contains("end_ts"))
                    rowWriter.write("end_ts", trip.end_ts);

                if (rowWriter.required().contains("name"))
                    rowWriter.write("name", trip.name);
            }
        };

        // If insertion to trips resulted in a new key, store it
        trip.keysGenerated.register(new Callback<Trip>() {
            @Override
            public void call(Trip trip) {
                currentTrip = trip;
            }
        });


//        final Multimap<String, String> args = HashMultimap.create();
//
//        try {
//            ToolsCommon.config(args, new File("default.config"));
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//
//        // Analyze arguments
//        ToolsCommon.commands(args,
//                ToolsCommon.oneOf(
//                        "help"
//                ),
//                ToolsCommon.sequentialShorthand(
//                        "f", "filter",
//                        "?", "help"
//                ), rawArgs);
//
//        try {
//            ToolsCommon.config(args, new File("override.config"));
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//
//        if (!Iterables.isEmpty(args.get("help")) || Iterables.isEmpty(args.get("filter"))) {
//            System.out.println("usage: [options]");
//            System.out.println("  GF Filters a sequence of SSF GPS data based on circular predicates");
//            System.out.println("  options:");
//            System.out.println("    -f, --filter STRING Parsed as lat/lon/radius, creates a filter, negative values for");
//            System.out.println("                        for the radius specify an exclusion");
//            System.out.println("    -?, --help          Displays the help");
//        }
//
//        if (!Iterables.isEmpty(args.get("filter"))) {
//            LineIn lip = new LineIn();
//            DeSerializer<Item> dsr = new DeSerializer<Item>(ItemSerialization.ITEM_SERIALIZATION);
//            ClassFilter<GPS> gcf = new ClassFilter<GPS>(GPS.class);
//            GeoFilter gft = new GeoFilter();
//            ItemSerializer isr = new ItemSerializer();
//            LinesOut lop = new LinesOut(System.out);
//
//            for (String filter : args.get("filter")) {
//                final Scanner scanner = new Scanner(filter);
//                scanner.useLocale(Locale.ENGLISH);
//                scanner.useDelimiter(SLASH_SEMICOLON_SEPARATED);
//
//                final double lat = scanner.nextDouble();
//                final double lon = scanner.nextDouble();
//                final double radius = scanner.nextDouble();
//
//                gft.addSpherical(lat, lon, radius);
//            }
//
//            lip.setConsumer(dsr);
//            dsr.setConsumer(gcf);
//            gcf.setConsumer(gft);
//            gft.setConsumer(isr);
//            isr.setConsumer(lop);
//
//            lip.readFrom(System.in);
//        }
    }
}
