package eu.liveandgov.wp1.pps.api;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * <p>Result of a proximity calculation</p>
 * Created by Lukas Härtel on 11.02.14.
 */
public class CalculationResult {
    /**
     * Type of proximity calculation
     */
    public static enum CalculationType {
        /**
         * In proxmity of object
         */
        IN_PROXIMITY,
        /**
         * Not in any proximity
         */
        NOT_IN_PROXIMITY,
        /**
         * Cannot make decision for the given input
         */
        NO_DECISION,
        /**
         * Error during calculation
         */
        ERROR
    }

    /**
     * Type of proximity calculated
     */
    public final CalculationType type;

    /**
     * Identifier of the proxy object
     */
    public final String identifier;

    /**
     * Creates a new instance with the given values
     *
     * @param type       Type of proximity calculated
     * @param identifier Identifier of the proxy object
     */
    public CalculationResult(CalculationType type, String identifier) {
        this.type = type;
        this.identifier = identifier;
    }

    /**
     * Writes this result to the target stream
     *
     * @param oos The object output stream to write to
     * @throws IOException Thrown if the object could not be written
     */
    public void writeTo(ObjectOutputStream oos) throws IOException {
        oos.writeObject(type);
        oos.writeObject(identifier);
    }

    /**
     * Reads a result from a source stream
     *
     * @param ois The object input stream to read from
     * @return Returns the next calculation result
     * @throws IOException Thrown if the object could not be read
     */
    public static CalculationResult readFrom(ObjectInputStream ois) throws IOException {
        try {
            final CalculationType type = (CalculationType) ois.readObject();
            final String identifier = (String) ois.readObject();

            return new CalculationResult(type, identifier);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CalculationResult that = (CalculationResult) o;

        if (identifier != null ? !identifier.equals(that.identifier) : that.identifier != null) return false;
        if (type != that.type) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (identifier != null ? identifier.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "CalculationResult{" +
                "type=" + type +
                ", identifier='" + identifier + '\'' +
                '}';
    }
}
