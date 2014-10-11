package eu.liveandgov.wp1.sensor_collector;

import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;

import com.google.inject.Inject;

import java.util.List;

import eu.liveandgov.wp1.sensor_collector.api.MoraAPI;
import eu.liveandgov.wp1.sensor_collector.api.Trip;
import eu.liveandgov.wp1.sensor_collector.components.Streamer;
import eu.liveandgov.wp1.sensor_collector.components.Writer;
import eu.liveandgov.wp1.sensor_collector.fs.FS;
import eu.liveandgov.wp1.sensor_collector.os.OS;
import eu.liveandgov.wp1.sensor_collector.strategies.Transfer;
import roboguice.service.RoboService;

/**
 * Created by lukashaertel on 08.09.2014.
 */
public abstract class MoraService extends RoboService {
    /**
     * This binder holds and delegates all API calls to their respective components
     */
    private final IBinder api = new MoraAPI.Stub() {
        private Trip activeTrip;
        private boolean isRecording = false;

        private boolean isStreaming = false;

        @Override
        public void startRecording() throws RemoteException {
            // State aware abort
            if (isRecording)
                return;

            // Make a temporary trip
            activeTrip = new Trip("USER", "SECRET", System.currentTimeMillis(), Trip.SPECIAL_TIME_UNSET);

            // Assign the trips sink to the writer
            writer.setSink(fs.writeTrip(activeTrip));

            // Activate the writer dependency
            os.add(writer);

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
            os.remove(writer);

            // Unset sink
            writer.setSink(null);

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
            os.add(streamer);

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
            os.remove(streamer);
        }

        @Override
        public boolean isStreaming() throws RemoteException {
            return isStreaming;
        }

        @Override
        public List<Trip> getTrips() throws RemoteException {
            return fs.listTrips();
        }

        @Override
        public void transferTrip(Trip trip) throws RemoteException {
            // Read the trip and transfer it somewhere
            transfer.transferAllStuffSomewhere(fs.readTrip(trip));
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

    private OS os;
    private FS fs;
    private Transfer transfer;
    private Streamer streamer;
    private Writer writer;

    @Inject
    public void setUp(
            // Top level components
            OS os,
            FS fs,
            // Strategies
            Transfer transfer,
            // Network components
            Streamer streamer,
            Writer writer
    ) {
        // Copy fields
        this.os = os;
        this.fs = fs;
        this.transfer = transfer;
        this.streamer = streamer;
        this.writer = writer;

        // ... and then they connect
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Return the API binder
        return api;
    }

}
