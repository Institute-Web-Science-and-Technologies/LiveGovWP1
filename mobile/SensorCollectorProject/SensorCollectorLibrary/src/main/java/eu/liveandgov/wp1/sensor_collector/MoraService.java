package eu.liveandgov.wp1.sensor_collector;

import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;

import com.google.inject.Inject;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.List;

import eu.liveandgov.wp1.sensor_collector.api.MoraAPI;
import eu.liveandgov.wp1.sensor_collector.api.MoraConfig;
import eu.liveandgov.wp1.sensor_collector.api.RecorderConfig;
import eu.liveandgov.wp1.sensor_collector.api.Trip;
import eu.liveandgov.wp1.sensor_collector.components.Credentials;
import eu.liveandgov.wp1.sensor_collector.components.ItemBuffer;
import eu.liveandgov.wp1.sensor_collector.components.SensorSource.*;
import eu.liveandgov.wp1.sensor_collector.components.StreamerTarget;
import eu.liveandgov.wp1.sensor_collector.components.TagSource;
import eu.liveandgov.wp1.sensor_collector.components.WriterTarget;
import eu.liveandgov.wp1.sensor_collector.config.Configurator;
import eu.liveandgov.wp1.sensor_collector.fs.FS;
import eu.liveandgov.wp1.sensor_collector.logging.LogPrincipal;
import eu.liveandgov.wp1.sensor_collector.os.OS;
import eu.liveandgov.wp1.sensor_collector.rec.Recorder;
import eu.liveandgov.wp1.sensor_collector.strategies.TransferExecutor;

/**
 * <p>The service coordinating the connection and control of the recording and transfer components</p>
 * Created by lukashaertel on 08.09.2014.
 */
public class MoraService extends BaseMoraService {
    /**
     * Logger interface
     */
    private static final Logger logger = LogPrincipal.get();

    /**
     * This binder holds and delegates all API calls to their respective components
     */
    private final IBinder api = new MoraAPI.Stub() {
        private Trip activeTrip;

        private boolean isRecording = false;

        private boolean isStreaming = false;


        @Override
        public MoraConfig getConfig() {
            return configurator.getConfig();
        }

        @Override
        public void setConfig(MoraConfig config) {
            configurator.setConfig(config);
        }

        @Override
        public void resetConfig() {
            configurator.resetConfig();
        }

        @Override
        public void registerRecorder(RecorderConfig config) {
            recorder.registerRecorder(config);
        }

        @Override
        public void unregisterRecorder(RecorderConfig config) {
            recorder.unregisterRecorder(config);
        }

        @Override
        public List<RecorderConfig> getRecorders() {
            return recorder.getRecorders();
        }

        @Override
        public List<String> getRecorderItems(RecorderConfig config) {
            return recorder.getRecorderItems(config);
        }

        @Override
        public void annotate(String userTag) {
            tagSource.annotate(userTag);
        }


        @Override
        public void startRecording() throws RemoteException {
            // State aware abort
            if (isRecording)
                return;

            // Make a temporary trip
            activeTrip = new Trip(credentials.user, credentials.secret, System.currentTimeMillis(), Trip.SPECIAL_TIME_UNSET);

            // Assign the trips sink to the writer
            writerTarget.setSink(fs.writeTrip(activeTrip));

            // Activate the writer dependency
            os.addTarget(writerTarget);

            // Set recording flag
            isRecording = true;
        }

        @Override
        public void stopRecording() throws RemoteException {
            // State aware abort
            if (!isRecording)
                return;

            // Clear recording flag
            isRecording = false;

            // Deactivate writer dependency
            os.removeTarget(writerTarget);

            // Unset sink
            writerTarget.setSink(null);

            // Move temporary trip
            fs.renameTrip(activeTrip, new Trip(activeTrip.userId, activeTrip.userSecret, activeTrip.startTime, System.currentTimeMillis()));

            // Unset trip
            activeTrip = null;
        }

        @Override
        public boolean isRecording() throws RemoteException {
            return isRecording;
        }

        @Override
        public void startStreaming() throws RemoteException {
            // State aware abort
            if (isStreaming)
                return;

            // Add streamer dependency
            os.addTarget(streamerTarget);

            // Set streaming flag
            isStreaming = true;
        }

        @Override
        public void stopStreaming() throws RemoteException {
            // State aware abort
            if (!isStreaming)
                return;

            // Clear streaming flag
            isStreaming = false;

            // Remove streamer dependency
            os.removeTarget(streamerTarget);
        }

        @Override
        public boolean isStreaming() throws RemoteException {
            return isStreaming;
        }

        @Override
        public List<Trip> getTrips() throws RemoteException {
            return fs.listTrips(true);
        }

        @Override
        public boolean transferTrip(Trip trip) throws RemoteException {
            // Read the trip and transfer it somewhere
            try {
                return transferExecutor.transfer(trip, fs.readTrip(trip));
            } catch (IOException e) {
                logger.error("Error transferring trip " + trip, e);
                throw new RuntimeException(e);
            }
        }

        @Override
        public void deleteTrip(Trip trip) throws RemoteException {
            // Delete the trip
            fs.deleteTrip(trip);
        }

        @Override
        public List<Bundle> getReports() throws RemoteException {
            // Get all implemented reports
            return os.getReports();
        }
    };

    @Inject
    Credentials credentials;

    @Inject
    Configurator configurator;

    @Inject
    OS os;

    @Inject
    Recorder recorder;

    @Inject
    FS fs;


    @Inject
    TransferExecutor transferExecutor;

    @Inject
    ItemBuffer itemBuffer;


    @Inject
    AccelerometerSource accelerometerSource;

    @Inject
    LinearAccelerationSource linearAccelerationSource;

    @Inject
    GravitySource gravitySource;

    @Inject
    MagnetometerSource magnetometerSource;

    @Inject
    RotationSource rotationSource;

    @Inject
    TagSource tagSource;


    @Inject
    StreamerTarget streamerTarget;

    @Inject
    WriterTarget writerTarget;

    @Override
    protected void startup() throws IOException {
        // Load the config
        configurator.loadConfig();

        // DEBUG ONLY
        configurator.resetConfig();


        // Add the connectors
        os.addReporter(itemBuffer);

        os.addReporter(recorder);

        // Add the sensors
        os.addSource(accelerometerSource);
        os.addReporter(accelerometerSource);

        os.addSource(linearAccelerationSource);
        os.addReporter(linearAccelerationSource);

        os.addSource(gravitySource);
        os.addReporter(gravitySource);

        os.addSource(magnetometerSource);
        os.addReporter(magnetometerSource);

        os.addSource(rotationSource);
        os.addReporter(rotationSource);

        os.addSource(tagSource);
        os.addReporter(tagSource);

        // Add the targets
        os.addReporter(writerTarget);

        // Sanitize file system
        for (Trip t : fs.listTrips(false))
            fs.deleteTrip(t);

        os.startConnector();
    }

    @Override
    protected void shutdown() throws IOException {
        os.stopConnector();

        // Save the config
        configurator.storeConfig();
    }

    @Override
    protected boolean activateStandAlone() {
        // If no activity at backend, service may terminate
        return os.isActive();
    }

    @Override
    protected IBinder getBinder() {
        return api;
    }

}
