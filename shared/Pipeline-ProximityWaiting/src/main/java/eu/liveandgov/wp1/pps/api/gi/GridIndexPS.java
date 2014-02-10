package eu.liveandgov.wp1.pps.api.gi;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import eu.liveandgov.wp1.data.Tuple;
import eu.liveandgov.wp1.pps.api.ProximityService;

/**
 * Grid Index ProximityType Service, stores calculated values in a two-dimensional index. Acts as a
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
    private final LinkedHashMap<Field, Tuple<Boolean, String>> calculated;

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
        this.calculated = new LinkedHashMap<Field, Tuple<Boolean, String>>();
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
        for (Entry<Field, Tuple<Boolean, String>> indexEntry : calculated.entrySet()) {
            objectOutputStream.writeObject(indexEntry.getKey());
            objectOutputStream.writeObject(indexEntry.getValue().left);
            objectOutputStream.writeObject(indexEntry.getValue().right);
        }

        objectOutputStream.close();
        fileOutputStream.close();
    }

    public void trySave(File file) {
        try {
            save(file);
        } catch (IOException e) {
            e.printStackTrace();
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
            final Boolean left = (Boolean) objectInputStream.readObject();
            final String right = (String) objectInputStream.readObject();
            calculated.put(field, Tuple.create(left, right));
        }

        objectInputStream.close();
        fileInputStream.close();
    }

    public void tryLoad(File file) {
        try {
            load(file);
        } catch (InvalidClassException e) {
            file.delete();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
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
        final Iterator<Entry<Field, Tuple<Boolean, String>>> it = calculated.entrySet().iterator();

        while (calculated.size() > storeDegree) {
            it.next();
            it.remove();
        }
    }

    @Override
    public Tuple<Boolean, String> calculate(double lat, double lon) {
        if (byCentroid) {
            lon = Math.round(lon / verticalResulution) * verticalResulution + verticalResulution / 2.0;
            lat = Math.round(lat / horizontalResultion) * horizontalResultion + horizontalResultion / 2.0;
        }

        final Field at = new Field((long) Math.round(lat / horizontalResultion), (long) Math.round(lon / verticalResulution));

        Tuple<Boolean, String> result = calculated.get(at);
        if (result == null) {
            result = calculateContains(lat, lon);
            if (result != null) {
                calculated.put(at, result);

                assertStoreDegree();
            }
        }

        return result;
    }

    /**
     * This method calculated the proximityType status of the given values, unindexed.
     */
    protected abstract Tuple<Boolean, String> calculateContains(double lat, double lon);
}
