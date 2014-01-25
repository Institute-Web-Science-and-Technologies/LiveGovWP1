package eu.liveandgov.wp1.sensor_collector.pps.api.gi;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import eu.liveandgov.wp1.sensor_collector.pps.api.Proximity;
import eu.liveandgov.wp1.sensor_collector.pps.api.ProximityService;

/**
 * Grid Index Proximity Service, stores calculated values in a two-dimensional index. Acts as a
 * database for calculation results approximated to the nearest field of the specified resolution.
 * Stores no more than `storeDegree` values, old values are truncated.
 *
 * @author lukashaertel
 */
public abstract class GridIndexPS implements ProximityService {
    private final static String LOG_TAG = "GIPS";

    /**
     * Main index
     */
    private final LinkedHashMap<Field, Proximity> calculated;

    /**
     * Horizontal resolution of the grid in degrees (division of longitude)
     */
    private final double horizontalResultion;

    /**
     * Vertical resolution of the grid in degrees (division of latitude)
     */
    private final double verticalResulution;

    /**
     * Specify this as true to calculate the value of an index by its centroid rather than its
     * first calling parameters
     */
    private final boolean byCentroid;

    /**
     * Storage level of the index, maximum of stored indice
     */
    private int storeDegree;

    public GridIndexPS(double horizontalResultion, double verticalResulution, boolean byCentroid, int storeDegree) {
        this.calculated = new LinkedHashMap<Field, Proximity>();
        this.horizontalResultion = horizontalResultion;
        this.verticalResulution = verticalResulution;
        this.byCentroid = byCentroid;
        this.storeDegree = storeDegree;
    }

    /**
     * Saves the database to the given file
     */
    public void save(File file) throws IOException {
        final FileOutputStream fileOutputStream = new FileOutputStream(file);
        final ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);

        objectOutputStream.writeInt(calculated.size());
        for (Entry<Field, Proximity> indexEntry : calculated.entrySet()) {
            objectOutputStream.writeObject(indexEntry.getKey());
            objectOutputStream.writeObject(indexEntry.getValue());
        }

        Log.d(LOG_TAG, "Persisted " + calculated.size() + " indices");

        objectOutputStream.close();
        fileOutputStream.close();
    }

    public void trySave(File file) {
        try {
            save(file);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error saving index", e);
        }
    }

    /**
     * Loads the database from the given file
     */
    public void load(File file) throws IOException, ClassNotFoundException {
        final FileInputStream fileInputStream = new FileInputStream(file);
        final ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

        final int stored = objectInputStream.readInt();
        for (int i = 0; i < stored; i++) {
            final Field field = (Field) objectInputStream.readObject();
            final Proximity proximity = (Proximity) objectInputStream.readObject();
            calculated.put(field, proximity);

            Log.d(LOG_TAG, field + " => " + proximity);
        }

        objectInputStream.close();
        fileInputStream.close();
    }

    public void tryLoad(File file) {
        try {
            load(file);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error loading index", e);
        } catch (ClassNotFoundException e) {
            Log.e(LOG_TAG, "Error loading index", e);
        }
    }

    public double getHorizontalResultion() {
        return horizontalResultion;
    }

    public double getVerticalResulution() {
        return verticalResulution;
    }

    public boolean isByCentroid() {
        return byCentroid;
    }

    public int getStoreDegree() {
        return storeDegree;
    }

    public void setStoreDegree(int storeDegree) {
        assert storeDegree > 0;

        this.storeDegree = storeDegree;

        assertStoreDegree();
    }

    private final void assertStoreDegree() {
        final Iterator<Entry<Field, Proximity>> it = calculated.entrySet().iterator();

        while (calculated.size() > storeDegree) {
            it.next();
            it.remove();
        }
    }

    @Override
    public Proximity calculate(double lat, double lon) {
        if (byCentroid) {
            lon = Math.round(lon / verticalResulution) * verticalResulution + verticalResulution / 2.0;
            lat = Math.round(lat / horizontalResultion) * horizontalResultion + horizontalResultion / 2.0;
        }

        final Field at = new Field((long) Math.round(lat / horizontalResultion), (long) Math.round(lon / verticalResulution));

        Proximity result = calculated.get(at);
        if (result == null) {
            result = calculateContains(lat, lon);
            if (result != Proximity.ERROR) {
                calculated.put(at, result);

                assertStoreDegree();
            }
        }

        return result;
    }

    /**
     * This method calculated the proximity status of the given values, unindexed.
     */
    protected abstract Proximity calculateContains(double lat, double lon);
}
