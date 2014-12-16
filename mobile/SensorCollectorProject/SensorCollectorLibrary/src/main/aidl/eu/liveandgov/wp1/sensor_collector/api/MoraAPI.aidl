// IMoraAPI.aidl
package eu.liveandgov.wp1.sensor_collector.api;

import android.os.Bundle;

import eu.liveandgov.wp1.sensor_collector.api.Trip;
import eu.liveandgov.wp1.sensor_collector.api.MoraConfig;
import eu.liveandgov.wp1.sensor_collector.api.RecorderConfig;

interface MoraAPI {
    /**
     * Reads the config, result is modifiable but changes are not propagated, call setConfig
     */
    MoraConfig getConfig();

    /**
     * Sets the new config
     */
    void setConfig(in MoraConfig c);

    /**
     * Resets the config
     */
    void resetConfig();


    /**
     * Registers a sample recorder
     */
    void registerRecorder(in RecorderConfig c);

    /**
     * Removes the sample recorder registration
     */
    void unregisterRecorder(in RecorderConfig c);

    /**
     * Gets all registered sample recorders
     */
    List<RecorderConfig> getRecorders();

    /**
     * Returns the value of the sample recorder, in serialized form
     */
    List<String> getRecorderItems(in RecorderConfig c);

    /**
     * Annotates the item stream with a user entered tag
     */
    void annotate(String userTag);

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
    boolean transferTrip(in Trip trip);

    /**
     * Deletes the recording by handle
     */
    void deleteTrip(in Trip trip);


    /**
     * Returns a container for status information
     */
    List<Bundle> getReports();
}
