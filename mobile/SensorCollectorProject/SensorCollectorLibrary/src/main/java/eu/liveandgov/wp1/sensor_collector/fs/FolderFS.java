package eu.liveandgov.wp1.sensor_collector.fs;

import android.content.Context;

import com.google.common.base.Charsets;
import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.io.CharSink;
import com.google.common.io.CharSource;
import com.google.common.io.Files;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import eu.liveandgov.wp1.sensor_collector.api.Trip;
import eu.liveandgov.wp1.sensor_collector.logging.LogPrincipal;

/**
 * <p>Implements a MORA file system from a folder contianing files named in a specific scheme</p>
 * Created by lukashaertel on 30.09.2014.
 */
public class FolderFS implements FS {
    /**
     * Logger interface
     */
    private final Logger logger = LogPrincipal.get();

    /**
     * <p>Android context, needed for root file resolution</p>
     */
    @Inject
    private Context context;

    @Inject
    @Named("eu.liveandgov.wp1.sensor_collector.fs.metaextension")
    private String metaextension = ".meta";

    @Inject
    @Named("eu.liveandgov.wp1.sensor_collector.fs.metacharset")
    private Charset metacharset = Charsets.UTF_8;

    @Inject
    @Named("eu.liveandgov.wp1.sensor_collector.fs.dataextension")
    private String dataextension = ".data";

    @Inject
    @Named("eu.liveandgov.wp1.sensor_collector.fs.datacharset")
    private Charset datacharset = Charsets.UTF_8;

    @Inject
    @Named("eu.liveandgov.wp1.sensor_collector.fs.root")
    private String root = "mora/fs";

    @Inject
    private DateFormat dateFormat;

    /**
     * <p>Obtains the file under which all MORA file system entries are listed</p>
     *
     * @return Returns a new file
     */
    private File getRootFile() {
        return new File(context.getFilesDir(), root);
    }

    /**
     * <p>Lists all meta files in the MORA file system</p>
     *
     * @return Returns a list of files
     */
    private File[] listMetaFiles() {
        return getRootFile().listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                return filename.toLowerCase().endsWith(metaextension);
            }
        });
    }

    /**
     * <p>Generates a new meta file location</p>
     *
     * @return Returns a randomly generated new meta file
     */
    private File newMetaFile() {
        try {
            return File.createTempFile(dateFormat.format(new Date()), metaextension, getRootFile());
        } catch (IOException e) {
            logger.error("Could not create meta file in directory", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * <p>Opens a meta file</p>
     *
     * @param f The file to open
     * @return Returns the file as a JSON object
     */
    private JSONObject openMetafile(File f) {
        try {
            return new JSONObject(Files.toString(f, metacharset));
        } catch (IOException e) {
            logger.error("Error reading trip meta file in file system", e);
            throw new RuntimeException(e);
        } catch (JSONException e) {
            logger.error("Error parsing json in trip meta file", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * <p>Writes a meta file</p>
     *
     * @param f The file to write
     * @param o The metadata to put
     */
    private void putMetaFile(File f, JSONObject o) {
        try {
            Files.write(o.toString(), f, metacharset);
        } catch (IOException e) {
            logger.error("Error writing trip meta file to file system", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * <p>Finds the data file corresponding to a meta file</p>
     *
     * @param metaFile The meta file
     * @return Returns the corresponding file
     */
    private File findDatafile(File metaFile) {
        return new File(metaFile.getParentFile(), metaFile.getName() + dataextension);
    }

    @Override
    public List<Trip> listTrips() {
        List<Trip> trips = Lists.newArrayList();

        // List and process all meta files
        for (File f : listMetaFiles()) {
            JSONObject j = openMetafile(f);

            try {
                trips.add(new Trip(
                        j.getString("userId"),
                        j.getString("userSecret"),
                        j.getLong("startTime"),
                        j.getLong("endTime")));
            } catch (JSONException e) {
                logger.error("Schema error in meta file", e);
                throw new RuntimeException(e);
            }
        }

        return trips;
    }

    @Override
    public CharSource readTrip(Trip trip) {
        // Return the first matching meta files data file as a char source
        for (File f : listMetaFiles()) {
            JSONObject j = openMetafile(f);

            try {
                if (!Objects.equal(trip.userId, j.getString("userId"))) continue;
                if (!Objects.equal(trip.userSecret, j.getString("userSecret"))) continue;
                if (trip.startTime != j.getLong("startTime")) continue;
                if (trip.endTime != j.getLong("endTime")) continue;

                return Files.asCharSource(findDatafile(f), datacharset);
            } catch (JSONException e) {
                logger.error("Schema error in meta file", e);
                throw new RuntimeException(e);
            }
        }

        throw new NoSuchElementException("No trip data file found for parameter " + trip);
    }

    @Override
    public CharSink writeTrip(Trip trip) {
        // Return the first matching meta files data file as a char sink
        for (File f : listMetaFiles()) {
            JSONObject j = openMetafile(f);

            try {
                if (!Objects.equal(trip.userId, j.getString("userId"))) continue;
                if (!Objects.equal(trip.userSecret, j.getString("userSecret"))) continue;
                if (trip.startTime != j.getLong("startTime")) continue;
                if (trip.endTime != j.getLong("endTime")) continue;

                return Files.asCharSink(findDatafile(f), datacharset);
            } catch (JSONException e) {
                logger.error("Schema error in meta file", e);
                throw new RuntimeException(e);
            }
        }

        try {
            // No trip found, put it then
            JSONObject meta = new JSONObject();
            meta.put("userId", trip.userId);
            meta.put("userSecret", trip.userSecret);
            meta.put("startTime", trip.startTime);
            meta.put("endTime", trip.endTime);

            // Generate a place to put the metadata and write it
            File f = newMetaFile();
            putMetaFile(f, meta);

            return Files.asCharSink(findDatafile(f), datacharset);
        } catch (JSONException e) {
            logger.error("Error creating meta data object", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void renameTrip(Trip tripFrom, Trip tripTo) {
        try {
            // TODO: Right now favoring the pattern of delegating to other methods instead of file modifications, as meta data is written here

            // Copy all data
            CharSource source = readTrip(tripFrom);
            CharSink sink = writeTrip(tripTo);

            source.copyTo(sink);

            // Delete trip
            deleteTrip(tripFrom);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteTrip(Trip trip) {
        // Delete the first matching meta file and the corresponding data file
        for (File f : listMetaFiles()) {
            JSONObject j = openMetafile(f);

            try {
                if (!Objects.equal(trip.userId, j.getString("userId"))) continue;
                if (!Objects.equal(trip.userSecret, j.getString("userSecret"))) continue;
                if (trip.startTime != j.getLong("startTime")) continue;
                if (trip.endTime != j.getLong("endTime")) continue;

                if (!f.delete())
                    throw new UnsupportedOperationException("Could not delete meta file for parameter " + trip);
                if (!findDatafile(f).delete())
                    throw new UnsupportedOperationException("Could not delete data file for parameter " + trip);
            } catch (JSONException e) {
                logger.error("Schema error in meta file", e);
                throw new RuntimeException(e);
            }
        }
    }
}
