package eu.liveandgov.wp1.serialization.impl;

import eu.liveandgov.wp1.data.DataCommons;
import eu.liveandgov.wp1.data.impl.*;
import eu.liveandgov.wp1.serialization.SerializationCommons;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * <p>Serialization of a motion item</p>
 * Created by Lukas Härtel on 08.02.14.
 */
public class VelocitySerialization extends AbstractSerialization<Velocity> {
    /**
     * The one instance of the serialization
     */
    public static final VelocitySerialization VELOCITY_SERIALIZATION = new VelocitySerialization();

    /**
     * Hidden constructor
     */
    protected VelocitySerialization() {
    }

    @Override
    protected void serializeRest(StringBuilder stringBuilder, Velocity velocity) {
        stringBuilder.append(velocity.velocity);
    }

    @Override
    protected Velocity deSerializeRest(String type, long timestamp, String device, Scanner scanner) {
        scanner.useDelimiter(SerializationCommons.SPACE_SEPARATED);

        float velocity = scanner.nextFloat();

        return new Velocity(timestamp, device, velocity);
    }
}
