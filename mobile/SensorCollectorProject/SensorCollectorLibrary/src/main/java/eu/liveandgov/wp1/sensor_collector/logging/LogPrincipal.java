package eu.liveandgov.wp1.sensor_collector.logging;

import android.os.Environment;

import com.google.common.base.CharMatcher;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.File;

import de.mindpipe.android.logging.log4j.LogConfigurator;
import eu.liveandgov.wp1.sensor_collector.configuration.SensorCollectionOptions;

/**
 * <p>Log principal; helper for log configuration and acquisition</p>
 * Created by lukashaertel on 29.07.2014.
 */
public class LogPrincipal {
    public static final int TAG_LENGTH = 3;

    public static final int NAME_LENGTH = 4;

    /**
     * Uses the class loader to resolve topmost stack trace class below the caller
     *
     * @return Returns the logger for the found class, see {@link #get(Class)}
     */
    public static Logger get() {
        try {
            StackTraceElement[] st = Thread.currentThread().getStackTrace();

            for (int i = 2; i < st.length; i++) {
                Class<?> p = Class.forName(Thread.currentThread().getStackTrace()[i].getClassName());
                if (p != LogPrincipal.class)
                    return get(p);
            }
            throw new ClassNotFoundException("No containing class could be found to associate");
        } catch (ClassNotFoundException e) {
            // Class should really be found, as it is calling the method
            throw new RuntimeException(e);
        }
    }

    public static Logger get(Class<?> item) {
        // Tag bases
        String t = CharMatcher.JAVA_UPPER_CASE.retainFrom(item.getSimpleName());

        // Trim if exceeding the maximum tag length
        if (t.length() > TAG_LENGTH)
            t = t.substring(0, TAG_LENGTH);

        // Make wide-hash
        long wh = item.hashCode();
        if (wh < 0)
            wh += Integer.MIN_VALUE;

        // Convert wide-hash to string
        String i = Long.toString(wh);

        // Compose to be long enough
        String n = t + i.substring(i.length() - (NAME_LENGTH - t.length()));

        return Logger.getLogger(n);
    }


    public static void configure() {
        // Basic configuration
        LogConfigurator logConfigurator = new LogConfigurator();

        // Configure
        logConfigurator.setFileName(Environment.getExternalStorageDirectory() + File.separator + SensorCollectionOptions.LOGFILE_NAME + ".log");
        logConfigurator.setRootLevel(Level.ALL);
        logConfigurator.configure();
    }
}
