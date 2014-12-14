package eu.liveandgov.wp1.sensor_collector.fs;

import com.google.common.io.ByteSource;
import com.google.common.io.CharSource;

/**
 * <p>Pairs up a name with reading methods</p>
 * Created by lukashaertel on 01.12.2014.
 */
public class DataSource {
    /**
     * <p>The source name of this readable</p>
     */
    public final String name;

    /**
     * The readable as a source of bytes
     */
    public final ByteSource byteSource;

    /**
     * The readable as a source of characters
     */
    public final CharSource charSource;

    /**
     * <p>Constructs the readable with the given values</p>
     *
     * @param name       The name of the readable, typically a corresponding filename
     * @param byteSource The source of bytes
     * @param charSource The source of characters
     */
    public DataSource(String name, ByteSource byteSource, CharSource charSource) {
        this.name = name;
        this.byteSource = byteSource;
        this.charSource = charSource;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DataSource dataSource = (DataSource) o;

        if (byteSource != null ? !byteSource.equals(dataSource.byteSource) : dataSource.byteSource != null)
            return false;
        if (charSource != null ? !charSource.equals(dataSource.charSource) : dataSource.charSource != null)
            return false;
        if (name != null ? !name.equals(dataSource.name) : dataSource.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (byteSource != null ? byteSource.hashCode() : 0);
        result = 31 * result + (charSource != null ? charSource.hashCode() : 0);
        return result;
    }
}
