package eu.liveandgov.wp1.packaging;

import java.math.BigDecimal;

/**
 * <p>Common tools an utilities for the packaging</p>
 * Created by Lukas Härtel on 17.03.14.
 */
public class PackagingCommons {
    /**
     * Common field for type
     */
    public static final String FIELD_TYPE = "type";

    /**
     * Common field for time stamp
     */
    public static final String FIELD_TIMESTAMP = "ts";

    /**
     * Common field for user identifier
     */
    public static final String FIELD_DEVICE = "user_id";

    /**
     * Returns the byte value of the object
     *
     * @param o An object of a type mappable to byte
     * @return Returns the byte
     */
    public static byte getByte(Object o) {
        if (o instanceof Byte) return (Byte) o;
        if (o instanceof Short) return (byte) (short) (Short) o;
        if (o instanceof Integer) return (byte) (int) (Integer) o;
        if (o instanceof Double) return (byte) (double) (Double) o;
        if (o instanceof BigDecimal) return ((BigDecimal) o).byteValue();
        if (o instanceof String) return Byte.valueOf((String) o);

        throw new UnsupportedOperationException();
    }

    /**
     * Returns the short value of the object
     *
     * @param o An object of a type mappable to short
     * @return Returns the short
     */
    public static short getShort(Object o) {
        if (o instanceof Byte) return (short) (byte) (Byte) o;
        if (o instanceof Short) return (Short) o;
        if (o instanceof Integer) return (short) (int) (Integer) o;
        if (o instanceof Double) return (short) (double) (Double) o;
        if (o instanceof BigDecimal) return ((BigDecimal) o).shortValue();
        if (o instanceof String) return Short.valueOf((String) o);

        throw new UnsupportedOperationException();
    }

    /**
     * Returns the int value of the object
     *
     * @param o An object of a type mappable to int
     * @return Returns the int
     */
    public static int getInt(Object o) {
        if (o instanceof Byte) return (int) (byte) (Byte) o;
        if (o instanceof Short) return (int) (short) (Short) o;
        if (o instanceof Integer) return (Integer) o;
        if (o instanceof Double) return (int) (double) (Double) o;
        if (o instanceof BigDecimal) return ((BigDecimal) o).intValue();
        if (o instanceof String) return Integer.valueOf((String) o);

        throw new UnsupportedOperationException();
    }

    /**
     * Returns the float value of the object
     *
     * @param o An object of a type mappable to float
     * @return Returns the float
     */
    public static float getFloat(Object o) {
        if (o instanceof Byte) return (float) (byte) (Byte) o;
        if (o instanceof Short) return (float) (short) (Short) o;
        if (o instanceof Integer) return (float) (int) (Integer) o;
        if (o instanceof Float) return (Float) o;
        if (o instanceof Double) return (float) (double) (Double) o;
        if (o instanceof BigDecimal) return ((BigDecimal) o).floatValue();
        if (o instanceof String) return Float.valueOf((String) o);

        throw new UnsupportedOperationException();
    }

    /**
     * Returns the double value of the object
     *
     * @param o An object of a type mappable to double
     * @return Returns the double
     */
    public static double getDouble(Object o) {
        if (o instanceof Byte) return (double) (byte) (Byte) o;
        if (o instanceof Short) return (double) (short) (Short) o;
        if (o instanceof Integer) return (double) (int) (Integer) o;
        if (o instanceof Float) return (double) (float) (Float) o;
        if (o instanceof Double) return (Double) o;
        if (o instanceof BigDecimal) return ((BigDecimal) o).doubleValue();
        if (o instanceof String) return Double.valueOf((String) o);

        throw new UnsupportedOperationException();
    }
}
