package eu.liveandgov.wp1.tools;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import eu.liveandgov.wp1.pipeline.impl.*;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by Lukas Härtel on 11.02.14.
 */
public class JDJ {
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
                        "help"
                ),
                ToolsCommon.sequentialShorthand(
                        "h", "host",
                        "u", "user",
                        "p", "password",
                        "d", "driver",
                        "l", "left",
                        "r", "right",
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
            System.out.println("  JDJ writes all entries of a database query result into the console in form of complete JSON objects");
            System.out.println("  surrounded by optional prefix or postfix, terminated by new-line");
            System.out.println("  options:");
            System.out.println("    -h, --host STRING     Database url including host, port and table, according to specification of the driver");
            System.out.println("    -u, --user STRING     User for the connection");
            System.out.println("    -p, --password STRING Password for the connection");
            System.out.println("    -d, --driver STRING   Adds a driver class name for JDBC, if none specified PostgreSQL™ will be used");
            System.out.println("    -l, --left STRING     Sets the prefix, defaults to empty");
            System.out.println("    -r, --right STRING    Sets the suffix, defaults to empty");
            System.out.println("    -?, --help            Displays the help");
        }

        if (command != null) {

            final String host = Iterables.getOnlyElement(args.get("host"));
            final String user = Iterables.getOnlyElement(args.get("user"));
            final String password = Iterables.getOnlyElement(args.get("password"));
            final String prefix = Iterables.getOnlyElement(args.get("left"), null);
            final String suffix = Iterables.getOnlyElement(args.get("right"), null);

            final LegacyJDBC legacyJdbc = new LegacyJDBC(host, user, password);

            if (Iterables.isEmpty(args.get("driver")))
                legacyJdbc.addDriver("org.postgresql.Driver");

            for (String driver : args.get("driver"))
                legacyJdbc.addDriver(driver);

            final MapJSON mjs = new MapJSON();
            final FromJSON fjs = new FromJSON();
            final Surround sur = new Surround(prefix, suffix);
            final LinesOut loc = new LinesOut(System.out);

            legacyJdbc.setConsumer(mjs);
            mjs.setConsumer(fjs);
            fjs.setConsumer(sur);
            sur.setConsumer(loc);

            legacyJdbc.readAll(command);
        }
    }
}
