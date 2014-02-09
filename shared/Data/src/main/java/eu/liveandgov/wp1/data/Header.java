package eu.liveandgov.wp1.data;

/**
 * Created by Lukas Härtel on 08.02.14.
 */
public final class Header {
    /**
     * Time of the item described by this header
     */
    public final long timestamp;

    /**
     * Device of the item described by this header
     */
    public final String device;

    /**
     * Creates a new header with the given values
     */
    public Header(long timestamp, String device) {
        this.timestamp = timestamp;
        this.device = device;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Header header = (Header) o;

        if (timestamp != header.timestamp) return false;
        if (device != null ? !device.equals(header.device) : header.device != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (timestamp ^ (timestamp >>> 32));
        result = 31 * result + (device != null ? device.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Header{" +
                "timestamp=" + timestamp +
                ", device='" + device + '\'' +
                '}';
    }
}
