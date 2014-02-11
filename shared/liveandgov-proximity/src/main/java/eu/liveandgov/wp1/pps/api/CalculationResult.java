package eu.liveandgov.wp1.pps.api;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by Lukas Härtel on 11.02.14.
 */
public class CalculationResult {
    public static enum CalculationType {
        IN_PROXIMITY, NOT_IN_PROXIMITY, NO_DECISION, ERROR
    }

    public final CalculationType type;

    public final String identifier;

    public CalculationResult(CalculationType type, String identifier) {
        this.type = type;
        this.identifier = identifier;
    }

    public void writeTo(ObjectOutputStream oos) throws IOException {
        oos.writeObject(type);
        oos.writeObject(identifier);
    }

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
