package eu.liveandgov.wp1.serialization;

import eu.liveandgov.wp1.util.LocalBuilder;
import org.apache.commons.lang3.StringEscapeUtils;

import java.util.Locale;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Created by Lukas Härtel on 08.02.14.
 */
public class SerializationCommons {
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

    private static final void unicodeEscape(StringBuilder target, char c) {
        target.append("\\u");
        target.append(String.format(Locale.ENGLISH, "%04x", c));
    }

    public static final CharSequence escape(String string) {
        if (string == null) return "";

        // Acquire the pair of builder and writer thereon
        final StringBuilder stringBuilder = LocalBuilder.acquireBuilder();

        stringBuilder.append('\"');

        // Use manual escaping to pipe the escaped string into the string builder
        final int l = string.length();
        for (int i = 0; i < l; i++) {
            final char c = string.charAt(i);
            switch (c) {
                // Catch special escape characters
                case '\"':
                    stringBuilder.append("\\\"");
                    break;
                case '\\':
                    stringBuilder.append("\\\\");
                    break;
                case '\b':
                    stringBuilder.append("\\b");
                    break;
                case '\n':
                    stringBuilder.append("\\n");
                    break;
                case '\t':
                    stringBuilder.append("\\t");
                    break;
                case '\f':
                    stringBuilder.append("\\f");
                    break;
                case '\r':
                    stringBuilder.append("\\r");
                    break;
                default:
                    // Outside of the range, use unicode escaping
                    if (c < 32 || c > 0x7f)
                        unicodeEscape(stringBuilder, c);
                    else
                        stringBuilder.append(c);
                    break;
            }
        }
        stringBuilder.append('\"');

        return stringBuilder;
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
