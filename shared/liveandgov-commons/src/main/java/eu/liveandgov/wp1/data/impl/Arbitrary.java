package eu.liveandgov.wp1.data.impl;

import eu.liveandgov.wp1.data.AbstractItem;
import eu.liveandgov.wp1.data.Item;
import eu.liveandgov.wp1.serialization.impl.BasicSerialization;

/**
 * Created by Lukas Härtel on 11.02.14.
 */
public class Arbitrary extends AbstractItem {
    public final String type;
    public final String value;

    public Arbitrary(long timestamp, String device, String type, String value) {
        super(timestamp, device);
        this.type = type;
        this.value = value;
    }

    public Arbitrary(Item header, String type, String value) {
        super(header);
        this.type = type;
        this.value = value;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Arbitrary arbitrary = (Arbitrary) o;

        if (type != null ? !type.equals(arbitrary.type) : arbitrary.type != null) return false;
        if (value != null ? !value.equals(arbitrary.value) : arbitrary.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Arbitrary{" +
                "type='" + type + '\'' +
                ", value='" + value + '\'' +
                '}';
    }

    @Override
    public String createSerializedForm() {
        return BasicSerialization.BASIC_SERIALIZATION.serialize(this);
    }
}
