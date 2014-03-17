package eu.liveandgov.wp1.tools;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import eu.liveandgov.wp1.data.Item;
import eu.liveandgov.wp1.packaging.impl.ItemPackaging;
import eu.liveandgov.wp1.pipeline.impl.*;

import static eu.liveandgov.wp1.serialization.SerializationCommons.*;

import eu.liveandgov.wp1.util.LocalBuilder;

import java.sql.SQLException;
import java.util.*;

/**
 * <p>Example usage on Live &amp; Gov server:</p>
 * <p>
 *     <code>
 *        -h jdbc:postgresql:liveandgov -u liveandgov -p liveandgov -m TAG/sensor_tags/tag -m ACC/sensor_accelerometer/x/y/z
 *     </code>
 * </p>
 * Created by Lukas Härtel on 11.02.14.
 */
public class JDIA {
    public static void main(String[] rawArgs) throws SQLException {
        // Analyze arguments
        final Multimap<String, String> args = ToolsCommon.commands(
                ToolsCommon.oneOf(
                        "help"
                ),
                ToolsCommon.sequentialShorthand(
                        "h", "host",
                        "u", "user",
                        "p", "password",
                        "d", "driver",
                        "m", "map",
                        "?", "help"
                ), rawArgs);


        final String command = ToolsCommon.end(rawArgs);

        if (!Iterables.isEmpty(args.get("help")) || command == null) {
            System.out.println("usage: [options] command");
            System.out.println("  JDI writes all entries of a database for SSF specified by mappings from types to tables and columns");
            System.out.println("  options:");
            System.out.println("    -h, --host STRING     Database url including host, port and table, according to specification of the driver");
            System.out.println("    -u, --user STRING     User for the connection");
            System.out.println("    -p, --password STRING Password for the connection");
            System.out.println("    -d, --driver STRING   Adds a driver class name for JDBC, if none specified PostgreSQL™ will be used");
            System.out.println("    -m, --map STRING      Parsed as type/mapping/extra column/extra columns...");
            System.out.println("                          Parameters may be escaped for spaces");
            System.out.println("    -?, --help            Displays the help");
        }

        if (command != null) {

            final String host = Iterables.getOnlyElement(args.get("host"));
            final String user = Iterables.getOnlyElement(args.get("user"));
            final String password = Iterables.getOnlyElement(args.get("password"));

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

            final JDBC jdbc = new JDBC(host, user, password);

            if (Iterables.isEmpty(args.get("driver")))
                jdbc.addDriver("org.postgresql.Driver");

            for (String driver : args.get("driver"))
                jdbc.addDriver(driver);

            final UnPack<Item> iup = new UnPack<Item>(ItemPackaging.ITEM_PACKAGING);
            final ItemSerializer isr = new ItemSerializer();
            final LinesOut loc = new LinesOut(System.out);

            jdbc.setConsumer(iup);
            iup.setConsumer(isr);
            isr.setConsumer(loc);

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
