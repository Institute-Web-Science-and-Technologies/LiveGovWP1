package eu.liveandgov.wp1.sensor_collector.util;

import android.support.annotation.NonNull;

import com.google.common.base.Charsets;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hasher;
import com.google.common.io.ByteSink;
import com.google.common.io.ByteSource;
import com.google.common.io.CharSource;
import com.google.common.io.Closer;
import com.google.common.io.Files;
import com.google.common.io.LineProcessor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

/**
 * <p>Utility method for interaction with files</p>
 * Created by lukashaertel on 17.11.2014.
 */
public class MoraIO {
    /**
     * Sets the content of the file from an object
     *
     * @param it The object to write
     * @param x  The file to write to
     * @throws IOException Exception thrown from the underlying operations
     */
    public static void setContent(Object it, File x) throws IOException {
        Closer closer = Closer.create();
        try {
            // Create all containing folders
            Files.createParentDirs(x);

            FileOutputStream f = closer.register(new FileOutputStream(x));
            ObjectOutputStream o = closer.register(new ObjectOutputStream(f));

            o.writeObject(it);
        } finally {
            closer.close();
        }
    }

    /**
     * Gets the content of teh file or returns the default value
     *
     * @param x   The file to read from
     * @param def The default value, may not be null
     * @param <T> The type of the object
     * @return Returns the object cast or the default value
     */
    public static <T> T getContent(File x, @NonNull T def) {
        try {
            @SuppressWarnings("unchecked")
            Class<? extends T> c = (Class<? extends T>) def.getClass();

            return getContent(x, c);
        } catch (IOException e) {
            return def;
        }
    }

    /**
     * Gets the content of the file as a cast object
     *
     * @param x   The file to read from
     * @param c   The class of the object
     * @param <T> The type of the object
     * @return Returns the object cast
     * @throws IOException Exception thrown from the underlying operations
     */
    public static <T> T getContent(File x, Class<T> c) throws IOException {
        return c.cast(getContent(x));
    }

    /**
     * Gets the content of the file
     *
     * @param x The file to read from
     * @return Returns the object
     * @throws IOException
     */
    public static Object getContent(File x) throws IOException {
        Closer closer = Closer.create();
        try {
            FileInputStream f = closer.register(new FileInputStream(x));
            ObjectInputStream o = closer.register(new ObjectInputStream(f));

            return o.readObject();
        } catch (ClassNotFoundException e) {
            throw closer.rethrow(e);
        } finally {
            closer.close();
        }
    }

    /**
     * <p>Computes the hash of a char source by feeding all lines to a hasher provided by a hash function</p>
     *
     * @param hashFunction The hash function to use
     * @param charSource   The char source to hash
     * @return Returns a hash code
     */
    public static HashCode hash(final HashFunction hashFunction, CharSource charSource) {
        try {
            return charSource.readLines(new LineProcessor<HashCode>() {
                // Make the hasher to aggregate
                private Hasher hasher = hashFunction.newHasher();

                @Override
                public boolean processLine(String line) throws IOException {
                    // Put the line to the hasher and continue
                    hasher.putString(line, Charsets.UTF_16);

                    return true;
                }

                @Override
                public HashCode getResult() {
                    // Return the hash value
                    return hasher.hash();
                }
            });
        } catch (IOException e) {
            // Should not occur
            throw new RuntimeException(e);
        }
    }

    /**
     * <p>Creates a compressing byte sink</p>
     *
     * @param sink The sink to compress
     * @return Returns a new byte sink
     */
    public static ByteSink compress(final ByteSink sink) {
        class CompressedByteSink extends ByteSink {
            @Override
            public OutputStream openStream() throws IOException {
                return new GZIPOutputStream(sink.openStream());
            }

            @Override
            public OutputStream openBufferedStream() throws IOException {
                return new GZIPOutputStream(sink.openBufferedStream());
            }
        }

        // If sink is already compressed, return it
        if (sink instanceof CompressedByteSink)
            return sink;

        // Otherwise return the compressed sink
        return new CompressedByteSink();
    }

    /**
     * File header of GZIP files
     */
    public static byte[] GZIP_MAGIC_NUMBER = {31, -117};

    /**
     * <p>Takes a byte source and compresses it with GZIP, if it is not compressed already</p>
     *
     * @param s The byte source to compress
     * @return Returns a new byte source if compression was added
     * @throws IOException Thrown by the file magic checking
     */
    public static ByteSource compressUncompressed(final ByteSource s) throws IOException {
        if (isCompressed(s))
            return s;
        else
            return new ByteSource() {
                @Override
                public InputStream openStream() throws IOException {
                    // Read stream and compress on the fly
                    return new GZIPCompressingInputStream(s.openStream());
                }

                @Override
                public InputStream openBufferedStream() throws IOException {
                    // Read stream and compress on the fly
                    return new GZIPCompressingInputStream(s.openBufferedStream());
                }
            };
    }

    /**
     * <p>Takes a byte source and decompresses it from GZIP, if it is compressed</p>
     *
     * @param s The byte source to decompress
     * @return Returns a new byte source if compression was removed
     * @throws IOException Thrown by the file magic checking
     */
    public static ByteSource decompressCompressed(final ByteSource s) throws IOException {
        if (!isCompressed(s))
            return s;
        else
            return new ByteSource() {
                @Override
                public InputStream openStream() throws IOException {
                    // Read stream and decompress on the fly
                    return new GZIPConcatInputStream(s.openStream());
                }

                @Override
                public InputStream openBufferedStream() throws IOException {
                    // Read stream and decompress on the fly
                    return new GZIPConcatInputStream(s.openBufferedStream());
                }
            };
    }

    private static boolean isCompressed(ByteSource s) throws IOException {

        return s.slice(0, GZIP_MAGIC_NUMBER.length).contentEquals(ByteSource.wrap(GZIP_MAGIC_NUMBER));
    }
}
