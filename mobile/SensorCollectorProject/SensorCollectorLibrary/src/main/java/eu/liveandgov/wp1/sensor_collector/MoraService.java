package eu.liveandgov.wp1.sensor_collector;

import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;

import com.google.inject.Inject;

import java.util.List;

import eu.liveandgov.wp1.sensor_collector.api.MoraAPI;
import eu.liveandgov.wp1.sensor_collector.api.Trip;
import eu.liveandgov.wp1.sensor_collector.os.OS;
import roboguice.service.RoboService;

/**
 * Created by lukashaertel on 08.09.2014.
 */
public abstract class MoraService extends RoboService {
    /**
     * This binder holds and delegates all API calls to their respective components
     */
    private final IBinder api = new MoraAPI.Stub() {
        @Override
        public void startRecording() throws RemoteException {
        }

        @Override
        public void stopRecording() throws RemoteException {

        }

        @Override
        public boolean isRecording() throws RemoteException {
            return false;
        }

        @Override
        public void startStreaming() throws RemoteException {

        }

        @Override
        public void stopStreaming() throws RemoteException {

        }

        @Override
        public boolean isStreaming() throws RemoteException {
            return false;
        }

        @Override
        public List<Trip> getTrips() throws RemoteException {
            return null;
        }

        @Override
        public void transferTrip(Trip trip) throws RemoteException {

        }

        @Override
        public void deleteTrip(Trip trip) throws RemoteException {
        }

        @Override
        public List<Bundle> getReports() throws RemoteException {
            return os.getReports();
        }
    };

    @Inject
    private OS os;


    @Override
    public IBinder onBind(Intent intent) {
        // Return the API binder
        return api;
    }

}
