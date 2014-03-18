package eu.liveandgov.wp1.tools;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import eu.liveandgov.wp1.data.Item;
import eu.liveandgov.wp1.packaging.impl.ItemPackaging;
import eu.liveandgov.wp1.pipeline.impl.*;

import static eu.liveandgov.wp1.serialization.SerializationCommons.*;

import eu.liveandgov.wp1.pipeline.impl.postgis.PointExpander;
import eu.liveandgov.wp1.util.LocalBuilder;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

/**
 * <p>Example usage on Live &amp; Gov server:</p>
 * <p>
 * <code>
 * -h jdbc:postgresql:liveandgov -u liveandgov -p liveandgov -m TAG/sensor_tags/tag -m ACC/sensor_accelerometer/x/y/z
 * </code>
 * </p>
 * Created by Lukas Härtel on 11.02.14.
 */
public class JDIA {
    public static void main(String[] rawArgs) throws SQLException {
        final Multimap<String, String> args = HashMultimap.create();

        try {
            ToolsCommon.config(args, new File("default.config"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Analyze arguments
        ToolsCommon.commands(args,
                ToolsCommon.oneOf(
                        "help",
                        "nodriver",
                        "nogeom"
                ),
                ToolsCommon.sequentialShorthand(
                        "h", "host",
                        "u", "user",
                        "p", "password",
                        "d", "driver",
                        "D", "nodriver",
                        "m", "map",
                        "g", "geom",
                        "G", "nogeom",
                        "?", "help"
                ), rawArgs);

        try {
            ToolsCommon.config(args, new File("override.config"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (!Iterables.isEmpty(args.get("help")) || Iterables.isEmpty(args.get("map"))) {
            System.out.println("usage: [options]");
            System.out.println("  JDIA writes all entries of a database for SSF specified by mappings from types to tables and columns");
            System.out.println("  options:");
            System.out.println("    -h, --host STRING     Database url including host, port and table, according to specification of the driver");
            System.out.println("    -u, --user STRING     User for the connection");
            System.out.println("    -p, --password STRING Password for the connection");
            System.out.println("    -d, --driver STRING   Adds a driver class name for JDBC, if none specified PostgreSQL™ will be used");
            System.out.println("    -D, --nodriver        Deactivates the default driver");
            System.out.println("    -m, --map STRING      Parsed as type/mapping/extra column/extra columns...");
            System.out.println("                          Parameters may be escaped for spaces");
            System.out.println("    -g, --geom STRING     Geometry expansion parsed as source/lat target/lon target, default is lonlat/lat/lon");
            System.out.println("                          as well as latlon/lat/lon");
            System.out.println("    -G, --nogeom          Deactivates the default expansion");
            System.out.println("    -?, --help            Displays the help");
        }

        if (!Iterables.isEmpty(args.get("map"))) {

            // Get simple params
            final String host = Iterables.getOnlyElement(args.get("host"));
            final String user = Iterables.getOnlyElement(args.get("user"));
            final String password = Iterables.getOnlyElement(args.get("password"));

            // Make type to table and column mapping
            final Map<String, String> ttm = new TreeMap<String, String>();
            final Map<String, Collection<String>> tcm = new TreeMap<String, Collection<String>>();
            for (String m : args.get("map")) {
                final Scanner scanner = new Scanner(m);
                scanner.useLocale(Locale.ENGLISH);
                scanner.useDelimiter(SLASH_SEMICOLON_SEPARATED);

                final String type = nextString(scanner);
                final String table = nextString(scanner);

                final Collection<String> columns = new ArrayList<String>();
                ;
                while (hasNextString(scanner)) {
                    columns.add(nextString(scanner));
                }

                ttm.put(type, table);
                tcm.put(type, columns);
            }

            // Setup database driver
            final JDBC jdbc = new JDBC(host, user, password);

            if (Iterables.isEmpty(args.get("driver")) && Iterables.isEmpty(args.get("nodriver")))
                jdbc.addDriver("org.postgresql.Driver");

            for (String driver : args.get("driver"))
                jdbc.addDriver(driver);

            // Setup PostGIS point expander
            final PointExpander pex = new PointExpander();

            if (Iterables.isEmpty(args.get("geom")) && Iterables.isEmpty(args.get("nogeom"))) {
                pex.addExpansion("lonlat", "lat", "lon");
                pex.addExpansion("latlon", "lat", "lon");
            }

            for (String geom : args.get("geom")) {
                final Scanner scanner = new Scanner(geom);
                scanner.useLocale(Locale.ENGLISH);
                scanner.useDelimiter(SLASH_SEMICOLON_SEPARATED);

                final String point = nextString(scanner);
                final String lat = nextString(scanner);
                final String lon = nextString(scanner);

                pex.addExpansion(point, lat, lon);
            }

            // Setup simpler pipeline
            final UnPack<Item> iup = new UnPack<Item>(ItemPackaging.ITEM_PACKAGING);
            final ItemSerializer isr = new ItemSerializer();
            final LinesOut loc = new LinesOut(System.out);

            // Connect it
            jdbc.setConsumer(pex);
            pex.setConsumer(iup);
            iup.setConsumer(isr);
            isr.setConsumer(loc);

            // Read all mappings to output
            for (Map.Entry<String, String> e : ttm.entrySet()) {
                final String type = e.getKey();
                final String table = e.getValue();
                final Collection<String> columns = tcm.get(type);

                final StringBuilder stringBuilder = LocalBuilder.acquireBuilder();
                stringBuilder.append("select '");
                stringBuilder.append(type);
                stringBuilder.append("' as type,ts,user_id");

                for (String column : columns) {
                    stringBuilder.append(',');
                    stringBuilder.append(column);
                }

                stringBuilder.append(" from ");
                stringBuilder.append(table);
                stringBuilder.append(" inner join trip on ");
                stringBuilder.append(table);
                stringBuilder.append(".trip_id=trip.trip_id");

                jdbc.readAll(stringBuilder.toString());
            }
        }
    }
}
