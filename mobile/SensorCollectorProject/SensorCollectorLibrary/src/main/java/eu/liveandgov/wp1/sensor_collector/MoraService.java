package eu.liveandgov.wp1.sensor_collector;

import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.List;

import eu.liveandgov.wp1.sensor_collector.api.MoraAPI;
import eu.liveandgov.wp1.sensor_collector.api.MoraConfig;
import eu.liveandgov.wp1.sensor_collector.api.RecorderConfig;
import eu.liveandgov.wp1.sensor_collector.api.Trip;
import eu.liveandgov.wp1.sensor_collector.components.BluetoothSource;
import eu.liveandgov.wp1.sensor_collector.components.Credentials;
import eu.liveandgov.wp1.sensor_collector.components.GActSource;
import eu.liveandgov.wp1.sensor_collector.components.HARSource;
import eu.liveandgov.wp1.sensor_collector.components.ItemBuffer;
import eu.liveandgov.wp1.sensor_collector.components.LocationSource;
import eu.liveandgov.wp1.sensor_collector.components.NotifierSource;
import eu.liveandgov.wp1.sensor_collector.components.SensorSource.AccelerometerSource;
import eu.liveandgov.wp1.sensor_collector.components.SensorSource.GravitySource;
import eu.liveandgov.wp1.sensor_collector.components.SensorSource.LinearAccelerationSource;
import eu.liveandgov.wp1.sensor_collector.components.SensorSource.MagnetometerSource;
import eu.liveandgov.wp1.sensor_collector.components.SensorSource.RotationSource;
import eu.liveandgov.wp1.sensor_collector.components.StreamerTarget;
import eu.liveandgov.wp1.sensor_collector.components.TagSource;
import eu.liveandgov.wp1.sensor_collector.components.TelephonySource;
import eu.liveandgov.wp1.sensor_collector.components.WifiSource;
import eu.liveandgov.wp1.sensor_collector.components.WriterTarget;
import eu.liveandgov.wp1.sensor_collector.config.Configurator;
import eu.liveandgov.wp1.sensor_collector.fs.FS;
import eu.liveandgov.wp1.sensor_collector.logging.LogPrincipal;
import eu.liveandgov.wp1.sensor_collector.os.OS;
import eu.liveandgov.wp1.sensor_collector.rec.Recorder;
import eu.liveandgov.wp1.sensor_collector.transfer.TransferExecutor;

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

        private boolean recording = false;

        private boolean streaming = false;


        @Override
        public MoraConfig getConfig() {
            return configurator.getConfig();
        }

        @Override
        public void setConfig(MoraConfig config) {
            configurator.setConfig(config);

            sendStatusUpdate();
        }

        @Override
        public void resetConfig() {
            configurator.resetConfig();

            sendStatusUpdate();
        }

        @Override
        public void registerRecorder(RecorderConfig config) {
            recorder.registerRecorder(config);

            sendStatusUpdate();
        }

        @Override
        public void unregisterRecorder(RecorderConfig config) {
            recorder.unregisterRecorder(config);

            sendStatusUpdate();
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
            if (recording)
                return;

            // Make a temporary trip
            activeTrip = new Trip(credentials.user, credentials.secret, System.currentTimeMillis(), Trip.SPECIAL_TIME_UNSET);

            // Assign the trips sink to the writer
            writerTarget.setSink(fs.writeTrip(activeTrip));

            // Status update after new trip
            sendStatusUpdate();

            // Activate the writer dependency
            os.addTarget(writerTarget);

            // Set recording flag
            recording = true;

            // Status update after state change
            sendStatusUpdate();
        }

        @Override
        public void stopRecording() throws RemoteException {
            // State aware abort
            if (!recording)
                return;

            // Clear recording flag
            recording = false;

            // Status update after state change
            sendStatusUpdate();

            // Deactivate writer dependency
            os.removeTarget(writerTarget);

            // Unset sink
            writerTarget.setSink(null);

            // Move temporary trip
            fs.renameTrip(activeTrip, new Trip(activeTrip.userId, activeTrip.userSecret, activeTrip.startTime, System.currentTimeMillis()));

            // Unset trip
            activeTrip = null;

            // Status update after trip renamed
            sendStatusUpdate();
        }

        @Override
        public boolean isRecording() throws RemoteException {
            return recording;
        }

        @Override
        public void startStreaming() throws RemoteException {
            // State aware abort
            if (streaming)
                return;

            // Add streamer dependency
            os.addTarget(streamerTarget);

            // Set streaming flag
            streaming = true;

            // Status update after state change
            sendStatusUpdate();
        }

        @Override
        public void stopStreaming() throws RemoteException {
            // State aware abort
            if (!streaming)
                return;

            // Remove streamer dependency
            os.removeTarget(streamerTarget);

            // Clear streaming flag
            streaming = false;

            // Status update after state change
            sendStatusUpdate();

        }

        @Override
        public boolean isStreaming() throws RemoteException {
            return streaming;
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

            sendStatusUpdate();
        }

        @Override
        public List<Bundle> getReports() throws RemoteException {
            // Get all implemented reports
            return os.getReports();
        }
    };

    @Inject
    @Named("eu.liveandgov.wp1.sensor_collector.logfileName")
    String logfileName;

    /**
     * Credentials field holding user and secret
     */
    @Inject
    Credentials credentials;

    /**
     * The configurator managing configuration changes
     */
    @Inject
    Configurator configurator;

    /**
     * The system controlling sample source activation and distribution
     */
    @Inject
    OS os;

    /**
     * The system wrapping the storage of trips
     */
    @Inject
    FS fs;

    /**
     * The delegate for transfer to hosts
     */
    @Inject
    TransferExecutor transferExecutor;


    /**
     * The destination for any sample source
     */
    @Inject
    ItemBuffer itemBuffer;


    // Below are all sample source currently available

    @Inject
    BluetoothSource bluetoothSource;

    @Inject
    GActSource gActSource;

    @Inject
    HARSource harSource;

    @Inject
    LocationSource locationSource;

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
    TelephonySource telephonySource;

    @Inject
    WifiSource wifiSource;

    @Inject
    NotifierSource notifierSource;

    // Targets that may be added and removed satisfying Mora API standards

    /**
     * The streamer streaming to a network node
     */
    @Inject
    StreamerTarget streamerTarget;

    /**
     * The writer serializing the items to the FS
     */
    @Inject
    WriterTarget writerTarget;

    /**
     * Recorder system for sample snapshots
     */
    @Inject
    Recorder recorder;

    @Override
    protected void startup() throws IOException {
        LogPrincipal.configure(logfileName);

        // Load the config
        configurator.loadConfig();
        configurator.resetConfig();

        // Add the connectors
        os.addReporter(itemBuffer);


        // Add the sensors
        os.addSource(bluetoothSource);
        os.addReporter(bluetoothSource);

        os.addSource(gActSource);
        os.addReporter(gActSource);

        os.addSource(harSource);
        os.addReporter(harSource);

        os.addSource(locationSource);
        os.addReporter(locationSource);

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

        os.addSource(telephonySource);
        os.addReporter(telephonySource);

        os.addSource(wifiSource);
        os.addReporter(wifiSource);

        os.addSource(notifierSource);

        // Add the targets
        os.addReporter(streamerTarget);

        os.addReporter(writerTarget);

        os.addTarget(recorder);
        os.addReporter(recorder);


        // Sanitize file system
        for (Trip t : fs.listTrips(false))
            fs.deleteTrip(t);

        os.startConnector();
    }

    // TODO Publisher and GPS cache(latter is partly implemented by recorders)

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
