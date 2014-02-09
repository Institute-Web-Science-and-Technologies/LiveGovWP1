package eu.liveandgov.wp1.serialization;

import org.apache.commons.lang.StringEscapeUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Created by Lukas Härtel on 08.02.14.
 */
public final class SerializationCommons {
    public static final String COMMA = ",";

    public static final Pattern COMMA_SEPARATED = Pattern.compile("\\s*,\\s*");

    public static final String SEMICOLON = ";";

    public static final String SLASH = "/";

    public static final Pattern SLASH_SEMICOLON_SEPARATED = Pattern.compile("\\s*[/;]\\s*");

    public static final String COLON = ":";

    public static final Pattern COLON_SEPARATED = Pattern.compile("\\s*:\\s*");

    public static final String SPACE = " ";

    public static final Pattern SPACE_SEPARATED = Pattern.compile("\\s+");

    public static final Pattern ESCAPED_STRING = Pattern.compile("\\\"(\\\\.|[^\\\"])*\\\"");

    public static final String escape(String string) {
        if (string == null) return "";

        return "\"" + StringEscapeUtils.escapeJava(string) + "\"";
    }

    public static final String unescape(String string) {
        string = string.trim();

        if (string.length() < 2) return null;

        return StringEscapeUtils.unescapeJava(string.substring(1, string.length() - 1));
    }

    public static void appendString(StringBuilder stringBuilder, String s) {
        stringBuilder.append(escape(s));
    }

    public static boolean hasNextString(Scanner scanner) {
        return scanner.hasNext(ESCAPED_STRING) || scanner.hasNext();
    }

    public static String nextString(Scanner scanner) {
        if (scanner.hasNext(ESCAPED_STRING)) {
            return unescape(scanner.next(ESCAPED_STRING));
        }
        return scanner.next();
    }

    public static <T extends Enum<T>> String toText(T t) {
        return t.toString().replaceAll("_+", " ").toLowerCase();
    }

    public static <T extends Enum<T>> T fromText(Class<T> c, String v) {
        return Enum.valueOf(c, v.replaceAll("\\s+", "_").toUpperCase());
    }
}
