// IMoraAPI.aidl
package eu.liveandgov.wp1.sensor_collector.api;

import android.os.Bundle;

import eu.liveandgov.wp1.sensor_collector.api.Trip;

interface MoraAPI {
    /**
     * Starts the sample recorder
     */
    void startRecording();

    /**
     * Stops the sample recorder
     */
    void stopRecording();

    /**
     * Returns true if the sample recorder is active
     */
    boolean isRecording();


    /**
     * Starts the sample streamer
     */
    void startStreaming();

    /**
     * Stops the sample streamer
     */
    void stopStreaming();

    /**
     * Returns true if the sample streamer is active
     */
    boolean isStreaming();


    /**
     * Returns a container for recording meta information
     */
    List<Trip> getTrips();

    /**
     * Transfers a recording by handle
     */
    void transferTrip(in Trip trip);

    /**
     * Deletes the recording by handle
     */
    void deleteTrip(in Trip trip);

    /**
     * Returns a container for status information
     */
    List<Bundle> getReports();
}
