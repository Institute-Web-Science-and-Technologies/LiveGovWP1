package eu.liveandgov.wp1.pps.api.gi;


import eu.liveandgov.wp1.pps.api.CalculationResult;
import eu.liveandgov.wp1.pps.api.ProximityService;

import java.io.*;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * <p>Grid Index ProximityType Service, stores calculated values in a two-dimensional index. Acts as a
 * database for calculation results approximated to the nearest field of the specified resolution.</p>
 * <p>Stores no more than `storeDegree` values, old values are truncated.</p>
 *
 * @author lukashaertel
 */
public abstract class GridIndexPS implements ProximityService {
    private final static String LOG_TAG = "GIPS";

    /**
     * Main index
     */
    private final ConcurrentMap<Field, CalculationResult> calculated;

    /**
     * Horizontal resolution of the grid in degrees (division of longitude)
     */
    private final double horizontalResolution;

    /**
     * Vertical resolution of the grid in degrees (division of latitude)
     */
    private final double verticalResolution;

    /**
     * Specify this as true to calculate the value of an index by its centroid rather than its first calling parameters
     */
    private final boolean byCentroid;

    /**
     * Storage level of the index, maximum of stored indices
     */
    private int storeDegree;

    public GridIndexPS(double horizontalResolution, double verticalResolution, boolean byCentroid, int storeDegree) {
        this.calculated = new ConcurrentHashMap<Field, CalculationResult>();
        this.horizontalResolution = horizontalResolution;
        this.verticalResolution = verticalResolution;
        this.byCentroid = byCentroid;
        this.storeDegree = storeDegree;
    }

    /**
     * Saves the database to the given file
     *
     * @param file The destination file
     */
    public void save(File file) throws IOException {
        final FileOutputStream fileOutputStream = new FileOutputStream(file);
        final ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);

        objectOutputStream.writeInt(calculated.size());
        for (Entry<Field, CalculationResult> indexEntry : calculated.entrySet()) {
            objectOutputStream.writeObject(indexEntry.getKey());
            indexEntry.getValue().writeTo(objectOutputStream);
        }

        objectOutputStream.close();
        fileOutputStream.close();
    }

    /**
     * Tries to save the database to the given file, ignores upcoming exceptions
     *
     * @param file The destination file
     */
    public void trySave(File file) {
        try {
            save(file);
        } catch (IOException e) {
        }
    }

    /**
     * Loads the database from the given file
     *
     * @param file The source file
     */
    public void load(File file) throws IOException, ClassNotFoundException {
        final FileInputStream fileInputStream = new FileInputStream(file);
        final ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

        final int stored = objectInputStream.readInt();
        for (int i = 0; i < stored; i++) {
            final Field field = (Field) objectInputStream.readObject();
            final CalculationResult value = CalculationResult.readFrom(objectInputStream);

            calculated.put(field, value);
        }

        objectInputStream.close();
        fileInputStream.close();
    }

    /**
     * Tries to load the database from the given file
     *
     * @param file The source file
     */
    public void tryLoad(File file) {
        try {
            load(file);
        } catch (InvalidClassException e) {
            file.delete();
        } catch (IOException e) {
        } catch (ClassNotFoundException e) {
        }
    }

    /**
     * Horizontal resolution of the grid in degrees (division of longitude)
     */
    public double getHorizontalResolution() {
        return horizontalResolution;
    }

    /**
     * Vertical resolution of the grid in degrees (division of latitude)
     */
    public double getVerticalResolution() {
        return verticalResolution;
    }

    /**
     * True if the index is supposed to calculate the value of an index by its centroid rather than its first calling parameters
     */
    public boolean isByCentroid() {
        return byCentroid;
    }

    /**
     * Storage level of the index, maximum of stored indices
     */
    public int getStoreDegree() {
        return storeDegree;
    }

    /**
     * Changes the store level of the index, the maximum of stored indices
     *
     * @param storeDegree The new degree of storage
     */
    public void setStoreDegree(int storeDegree) {
        assert storeDegree > 0;

        this.storeDegree = storeDegree;

        assertStoreDegree();
    }

    /**
     * Assert that the store degree is met
     */
    private final void assertStoreDegree() {
        final Iterator<Entry<Field, CalculationResult>> it = calculated.entrySet().iterator();

        while (calculated.size() > storeDegree) {
            it.next();
            it.remove();
        }
    }

    @Override
    public CalculationResult calculate(double lat, double lon) {
        if (byCentroid) {
            lon = Math.round(lon / verticalResolution) * verticalResolution + verticalResolution / 2.0;
            lat = Math.round(lat / horizontalResolution) * horizontalResolution + horizontalResolution / 2.0;
        }

        final Field at = new Field(Math.round(lat / horizontalResolution), Math.round(lon / verticalResolution));

        CalculationResult result = calculated.get(at);
        if (result == null) {
            result = calculateContains(lat, lon);
            switch (result.type) {
                case NOT_IN_PROXIMITY:
                case IN_PROXIMITY:
                case NO_DECISION:
                    calculated.put(at, result);
                    assertStoreDegree();
                    break;
            }
        }

        return result;
    }

    /**
     * This method calculated the proximityType status of the given values, unindexed.
     */
    protected abstract CalculationResult calculateContains(double lat, double lon);
}
