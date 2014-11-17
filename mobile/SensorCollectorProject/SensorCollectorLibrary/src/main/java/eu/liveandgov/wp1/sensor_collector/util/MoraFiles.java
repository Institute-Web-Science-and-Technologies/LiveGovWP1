package eu.liveandgov.wp1.sensor_collector.util;

import android.support.annotation.NonNull;

import com.google.common.io.Closer;
import com.google.common.io.Files;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * <p>Utility method for interaction with files</p>
 * Created by lukashaertel on 17.11.2014.
 */
public class MoraFiles {
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

}
