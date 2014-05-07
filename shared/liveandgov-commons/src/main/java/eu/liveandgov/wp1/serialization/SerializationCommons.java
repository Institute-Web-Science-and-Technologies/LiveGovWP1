package eu.liveandgov.wp1.serialization;

import eu.liveandgov.wp1.util.LocalBuilder;
import org.apache.commons.lang3.StringEscapeUtils;

import java.util.Locale;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * <p>Common tools an utilities for the serialization</p>
 * Created by Lukas Härtel on 08.02.14.
 */
public class SerializationCommons {
    /**
     * The comma string
     */
    public static final String COMMA = ",";

    /**
     * The comma-separation pattern
     */
    public static final Pattern COMMA_SEPARATED = Pattern.compile("\\s*,\\s*");

    /**
     * The semi-colon
     */
    public static final String SEMICOLON = ";";

    /**
     * The slash-string
     */
    public static final String SLASH = "/";

    /**
     * The slash or semicolon-separation pattern
     */
    public static final Pattern SLASH_SEMICOLON_SEPARATED = Pattern.compile("\\s*[/;]\\s*");

    /**
     * The colon string
     */
    public static final String COLON = ":";

    /**
     * The colon-separation pattern
     */
    public static final Pattern COLON_SEPARATED = Pattern.compile("\\s*:\\s*");

    /**
     * The space string
     */
    public static final String SPACE = " ";

    /**
     * The space-separation pattern
     */
    public static final Pattern SPACE_SEPARATED = Pattern.compile("\\s+");

    /**
     * The pattern for an escaped string
     */
    public static final Pattern ESCAPED_STRING = Pattern.compile("\\\"(\\\\.|[^\\\"])*\\\"");

    /**
     * Escapes one character into the hex-unicode representation
     *
     * @param target The target to write to
     * @param c      The character to escape
     */
    private static final void unicodeEscape(StringBuilder target, char c) {
        target.append("\\u");
        target.append(String.format(Locale.ENGLISH, "%04x", (int) c));
    }

    /**
     * Escapes the string into a char-buffer i.e. the string builder used to build escape this string
     *
     * @param string The string to escape
     * @return Returns the escaped string builder
     */
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

    /**
     * Unescapes the string
     *
     * @param string The string to unescape
     * @return Returns the unescaped string
     */
    public static final String unescape(String string) {
        string = string.trim();

        if (string.length() < 2) return null;

        return StringEscapeUtils.unescapeJava(string.substring(1, string.length() - 1));
    }

    /**
     * Appends one escaped string to the string builder
     *
     * @param stringBuilder The string builder to write to
     * @param s             The string to write to
     */
    public static void appendString(StringBuilder stringBuilder, String s) {
        stringBuilder.append(escape(s));
    }

    /**
     * True if the scanner has an escaped string or a regular string token
     *
     * @param scanner The scanner to read from
     * @return Returns true of the scanner has a string
     */
    public static boolean hasNextString(Scanner scanner) {
        return scanner.hasNext(ESCAPED_STRING) || scanner.hasNext();
    }

    /**
     * Returns the next escaped string or the next regular string token
     *
     * @param scanner The scanner to read from
     * @return Returns the string read from the scanner
     */
    public static String nextString(Scanner scanner) {
        if (scanner.hasNext(ESCAPED_STRING)) {
            return unescape(scanner.next(ESCAPED_STRING));
        }
        return scanner.next();
    }

    /**
     * Converts an enum to basic text representation
     *
     * @param t   The enum value to convert
     * @param <T> The type of the enum to convert, used for type safety
     * @return Returns the string representing the enum literal
     */
    public static <T extends Enum<T>> String toText(T t) {
        return t.toString().replaceAll("_+", " ").toLowerCase();
    }

    /**
     * Converts a string into the enum literal value
     *
     * @param c   The class of the enum
     * @param v   The string to convert
     * @param <T> The type of the enum to convert, used for type safety and parsing
     * @return Returns the enum literal represented by the string
     */
    public static <T extends Enum<T>> T fromText(Class<T> c, String v) {
        return Enum.valueOf(c, v.replaceAll("\\s+", "_").toUpperCase());
    }
}
