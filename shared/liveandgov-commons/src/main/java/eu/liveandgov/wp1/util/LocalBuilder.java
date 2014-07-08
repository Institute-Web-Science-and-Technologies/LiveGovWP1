package eu.liveandgov.wp1.util;


/**
 * <p>Local builder provides a caller with an existing string builder, nested string builder acquisitions are managed
 * by cycling through a larger set of builders</p>
 * Created by Lukas Härtel on 07.03.14.
 */
public class LocalBuilder {
    /**
     * Maximum nesting of string builders
     */
    @Deprecated
    public static final int MAXIMUM_NESTED = 128;

    /**
     * Initial capacity of the string builders
     */
    public static final int INITIAL_CAPACITY = 256;

    /**
     * Acquires one string builder
     */
    public static StringBuilder acquireBuilder() {
        return new StringBuilder(INITIAL_CAPACITY);
    }

}
