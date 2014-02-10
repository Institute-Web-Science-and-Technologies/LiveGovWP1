package eu.liveandgov.wp1.tools;

import com.google.common.base.Function;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Lukas Härtel on 10.02.14.
 */
public class ToolsCommon {
    public static Multimap<String, String> commands(Function<String, String> shorthandExpansion, String[] args) {
        final Multimap<String, String> result = HashMultimap.create();

        for (int i = 0; i < args.length; i += 2) {
            final String key;
            final String value;
            if (args[i].startsWith("--")) {
                key = args[i].substring(2).trim();
                value = args[i + 1];
            } else if (args[i].startsWith("-")) {
                key = shorthandExpansion.apply(args[i].substring(1).trim());
                value = args[i + 1];
            } else continue;

            result.put(key, value);
        }

        return result;
    }

    public static String end(String[] args) {
        return args.length == 0 ? null : args[args.length - 1];
    }

    public static Function<String, String> sequentialShorthand(final String... shAndExpansion) {
        return new Function<String, String>() {
            @Override
            public String apply(String s) {
                for (int i = 0; i < shAndExpansion.length; i += 2) {
                    if (s.equals(shAndExpansion[i])) return shAndExpansion[i + 1];
                }
                return null;
            }
        };
    }
}
