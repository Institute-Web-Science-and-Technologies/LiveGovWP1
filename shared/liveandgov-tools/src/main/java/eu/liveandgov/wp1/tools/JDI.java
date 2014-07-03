package eu.liveandgov.wp1.tools;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import eu.liveandgov.wp1.data.Item;
import eu.liveandgov.wp1.packaging.impl.ItemPackaging;
import eu.liveandgov.wp1.pipeline.impl.ItemSerializer;
import eu.liveandgov.wp1.pipeline.impl.LegacyJDBC;
import eu.liveandgov.wp1.pipeline.impl.LinesOut;
import eu.liveandgov.wp1.pipeline.impl.UnPack;
import eu.liveandgov.wp1.pipeline.impl.postgis.PointExpander;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Scanner;

import static eu.liveandgov.wp1.serialization.SerializationCommons.SLASH_SEMICOLON_SEPARATED;
import static eu.liveandgov.wp1.serialization.SerializationCommons.nextString;

/**
 * <p>Example usage on Live &amp; Gov server:</p>
 * <p>
 * <code>
 * -h jdbc:postgresql:liveandgov -u liveandgov -p liveandgov "select 'TAG' as type, ts, user_id, tag from sensor_tags inner join trip on sensor_tags.trip_id=trip.trip_id"
 * </code>
 * </p>
 * Created by Lukas Härtel on 11.02.14.
 */
public class JDI {
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
                        "g", "geom",
                        "G", "nogeom",
                        "?", "help"
                ), rawArgs);

        try {
            ToolsCommon.config(args, new File("override.config"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        final String command = ToolsCommon.end(rawArgs);

        if (!Iterables.isEmpty(args.get("help")) || command == null) {
            System.out.println("usage: [options] command");
            System.out.println("  JDI writes all entries of a database query result into the console in form of SSF entries");
            System.out.println("  terminated by new-line, the query has to supply the fields type:String, ts:Long,");
            System.out.println("  user_id:String, followed by the type specific fields");
            System.out.println("  options:");
            System.out.println("    -h, --host STRING     Database url including host, port and table, according to specification of the driver");
            System.out.println("    -u, --user STRING     User for the connection");
            System.out.println("    -p, --password STRING Password for the connection");
            System.out.println("    -d, --driver STRING   Adds a driver class name for JDBC, if none specified PostgreSQL™ will be used");
            System.out.println("    -D, --nodriver        Deactivates the default driver");
            System.out.println("    -g, --geom STRING     Geometry expansion parsed as source/lat target/lon target, default is lonlat/lat/lon");
            System.out.println("                          as well as latlon/lat/lon");
            System.out.println("    -G, --nogeom          Deactivates the default expansion");
            System.out.println("    -?, --help            Displays the help");
        }

        if (command != null) {

            final String host = Iterables.getOnlyElement(args.get("host"));
            final String user = Iterables.getOnlyElement(args.get("user"));
            final String password = Iterables.getOnlyElement(args.get("password"));

            final LegacyJDBC legacyJdbc = new LegacyJDBC(host, user, password);

            if (Iterables.isEmpty(args.get("driver")) && Iterables.isEmpty(args.get("nodriver")))
                legacyJdbc.addDriver("org.postgresql.Driver");

            for (String driver : args.get("driver"))
                legacyJdbc.addDriver(driver);


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

            final UnPack<Item> iup = new UnPack<Item>(ItemPackaging.ITEM_PACKAGING);
            final ItemSerializer isr = new ItemSerializer();
            final LinesOut loc = new LinesOut(System.out);

            legacyJdbc.setConsumer(pex);
            pex.setConsumer(iup);
            iup.setConsumer(isr);
            isr.setConsumer(loc);

            legacyJdbc.readAll(command);
        }
    }
}
