package eu.liveandgov.wp1.collector.persistence;

import java.util.List;

/**
 * Created by cehlen on 9/12/13.
 */
public interface PersistenceInterface {
    /**
     * Saves the given string to retrieve at another time.
     * @param value The string to save
     */
    void save(String value);

    /**
     * Retrieves n Number of lines in a String array. After you got these strings, they will be
     * removed from the persistent storage.
     * @param n Number of sensor events to retrieve
     * @return An arrays of sensor events as strings
     */
    List<String> readLines(int n);


}
