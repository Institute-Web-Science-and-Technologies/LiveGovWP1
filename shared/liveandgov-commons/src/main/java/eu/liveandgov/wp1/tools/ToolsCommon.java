package eu.liveandgov.wp1.tools;

import com.google.common.base.Function;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.sun.org.apache.xpath.internal.operations.Bool;

/**
 * <p>Utilities for command-line tools</p>
 * Created by Lukas Härtel on 10.02.14.
 */
public class ToolsCommon {
    /**
     * Parses a set of commands
     *
     * @param flag               The function describing if the given item is a flag, flags will me assigned to true
     * @param shorthandExpansion The expansion function expanding shorthands to full commands
     * @param args               The arguments to parse
     * @return Returns a multimap from key to values
     */
    public static Multimap<String, String> commands(Function<String, Boolean> flag, Function<String, String> shorthandExpansion, String[] args) {
        final Multimap<String, String> result = HashMultimap.create();

        for (int i = 0; i < args.length; i++) {
            final String key;
            final String value;
            if (args[i].startsWith("--")) {
                key = args[i].substring(2).trim();
                if (flag.apply(key)) {
                    value = "true";
                } else {
                    i++;
                    value = args[i];
                }
            } else if (args[i].startsWith("-")) {
                key = shorthandExpansion.apply(args[i].substring(1).trim());
                if (flag.apply(key)) {
                    value = "true";
                } else {
                    i++;
                    value = args[i];
                }
            } else continue;

            result.put(key, value);
        }

        return result;
    }

    /**
     * Returns the last string of the params
     *
     * @param args The The arguments to parse
     * @return Returns the last item
     */
    public static String end(String[] args) {
        return args.length == 0 ? null : args[args.length - 1];
    }

    /**
     * Creates the one of function
     *
     * @param keys The keys to be accepted
     * @return Returns a predicate function
     */
    public static Function<String, Boolean> oneOf(final String... keys) {
        return new Function<String, Boolean>() {
            @Override
            public Boolean apply(String s) {
                for (String key : keys) {
                    if (s.equals(key)) return true;
                }
                return false;
            }
        };
    }

    /**
     * Maps a sequence of strings to a mapping function that maps the key at the even index to the value of the subsequent odd index
     *
     * @param shAndExpansion The keys and the values interspersed
     * @return Returns a mapping function
     */
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
