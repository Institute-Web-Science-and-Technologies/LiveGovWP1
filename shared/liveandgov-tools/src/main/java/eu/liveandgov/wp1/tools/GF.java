package eu.liveandgov.wp1.tools;

import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import eu.liveandgov.wp1.data.Item;
import eu.liveandgov.wp1.data.impl.GPS;
import eu.liveandgov.wp1.packaging.impl.ItemPackaging;
import eu.liveandgov.wp1.pipeline.impl.*;
import eu.liveandgov.wp1.pipeline.impl.postgis.PointExpander;
import eu.liveandgov.wp1.serialization.impl.ItemSerialization;
import eu.liveandgov.wp1.util.LocalBuilder;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

import static eu.liveandgov.wp1.serialization.SerializationCommons.*;

/**
 * Created by Lukas Härtel on 18.03.14.
 */
public class GF {
    public static void main(String[] rawArgs) throws SQLException {
        // Analyze arguments
        final Multimap<String, String> args = ToolsCommon.commands(
                ToolsCommon.oneOf(
                        "help"
                ),
                ToolsCommon.sequentialShorthand(
                        "f", "filter",
                        "?", "help"
                ), rawArgs);

        try {
            ToolsCommon.config(args, new File("default.config"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (!Iterables.isEmpty(args.get("help")) || Iterables.isEmpty(args.get("filter"))) {
            System.out.println("usage: [options]");
            System.out.println("  GF Filters a sequence of SSF GPS data based on circular predicates");
            System.out.println("  options:");
            System.out.println("    -f, --filter STRING Parsed as lat/lon/radius, creates a filter, negative values for");
            System.out.println("                        for the radius specify an exclusion");
            System.out.println("    -?, --help          Displays the help");
        }

        if (!Iterables.isEmpty(args.get("filter"))) {
            LineIn lip = new LineIn();
            DeSerializer<Item> dsr = new DeSerializer<Item>(ItemSerialization.ITEM_SERIALIZATION);
            ClassFilter<GPS> gcf = new ClassFilter<GPS>(GPS.class);
            GeoFilter gft = new GeoFilter();
            ItemSerializer isr = new ItemSerializer();
            LinesOut lop = new LinesOut(System.out);

            for (String filter : args.get("filter")) {
                final Scanner scanner = new Scanner(filter);
                scanner.useLocale(Locale.ENGLISH);
                scanner.useDelimiter(SLASH_SEMICOLON_SEPARATED);

                final double lat = scanner.nextDouble();
                final double lon = scanner.nextDouble();
                final double radius = scanner.nextDouble();

                gft.addSpherical(lat, lon, radius);
            }

            lip.setConsumer(dsr);
            dsr.setConsumer(gcf);
            gcf.setConsumer(gft);
            gft.setConsumer(isr);
            isr.setConsumer(lop);

            lip.readFrom(System.in);
        }
    }
}
