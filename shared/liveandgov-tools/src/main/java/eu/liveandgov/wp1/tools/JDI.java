package eu.liveandgov.wp1.tools;

import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import eu.liveandgov.wp1.data.Item;
import eu.liveandgov.wp1.packaging.impl.ItemPackaging;
import eu.liveandgov.wp1.pipeline.impl.ItemSerializer;
import eu.liveandgov.wp1.pipeline.impl.JDBC;
import eu.liveandgov.wp1.pipeline.impl.LinesOut;
import eu.liveandgov.wp1.pipeline.impl.UnPack;

import java.sql.SQLException;

/**
 * <p>Example usage on Live &amp; Gov server:</p>
 * <p>
 *     <code>
 *          -h jdbc:postgresql:liveandgov -u liveandgov -p liveandgov "select 'TAG' as type, ts, user_id, tag from sensor_tags inner join trip on sensor_tags.trip_id=trip.trip_id"
 *     </code>
 * </p>
 * Created by Lukas Härtel on 11.02.14.
 */
public class JDI {
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
                        "?", "help"
                ), rawArgs);


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
            System.out.println("    -?, --help            Displays the help");
        }

        if (command != null) {

            final String host = Iterables.getOnlyElement(args.get("host"));
            final String user = Iterables.getOnlyElement(args.get("user"));
            final String password = Iterables.getOnlyElement(args.get("password"));

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

            jdbc.readAll(command);
        }
    }
}
