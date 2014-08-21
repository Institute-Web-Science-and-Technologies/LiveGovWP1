package eu.liveandgov.wp1.sensor_collector.configuration;

/**
 * SensorCollector IntentAPI
 *
 * Describes control commands that can be sent to the SensorCollectorService.
 *
 * Created by hartmann on 9/26/13.
 */
public class IntentAPI {
    /**
     * ACTION_GET_STATUS intents, requests a status update from the service.
     * The service responds with a RETURN_STATUS intent, which contains status information
     * in its' extra fields.
     */
    public static final String ACTION_GET_STATUS = "eu.liveandgov.sensorcollectorapi.intent.action.GET_STATUS";
    public static final String RETURN_STATUS = "eu.liveandgov.sensorcollectorapi.intent.return.STATUS";
    public static final String FIELD_SAMPLING       = "sampling";       // true if collecting samples
    public static final String FIELD_TRANSFERRING   = "transferring";   // true if transfering samples
    public static final String FIELD_SAMPLES_STORED = "samples_stored"; // true if samples are left for transfer
    public static final String FIELD_HAR            = "har";            // true if HAR is active
    public static final String FIELD_STATUS_ID      = "id";             // user ID set via Intent

    /**
     * Enable/Disable the collection of sensor samples.
     * Sensors can be configured in the class {@link SensorCollectionOptions}
     */
    public static final String ACTION_RECORDING_ENABLE =  "eu.liveandgov.sensorcollectorapi.intent.action.RECORDING_ENABLE";
    public static final String RECORDING_DISABLE = "eu.liveandgov.sensorcollectorapi.intent.action.RECORDING_DISABLE";

    /**
     * Triggers the data transfer to the server.
     */
    public static final String ACTION_TRANSFER_SAMPLES = "eu.liveandgov.sensorcollectorapi.intent.action.TRANSFER_SAMPLES";

    /**
     * Add an annotation sample to the sensor stream.
     * The annotation is contained in the "tag" field of the intent.
     */
    public static final String ACTION_ANNOTATE = "eu.liveandgov.sensorcollectorapi.intent.action.ANNOTATE";
    public static final String FIELD_ANNOTATION = "tag";
    public static final String VALUE_STOP_RECORDING = "STOP_RECORDING";
    public static final String VALUE_START_RECORDING = "START_RECORDING";

    /**
     * Set User ID
     *
     * This intent allows to set a user-id, which will be transferred to the backend service.
     * Using this id the records can be linked to users registered in the service center.
     */
    public static final String ACTION_SET_ID = "eu.liveandgov.sensorcollectorapi.intent.action.SET_USER_ID";
    public static final String FIELD_USER_ID = "id";

    /**
     * Human Activity Recognition (HAR)
     *
     * The monitoring of activities begins when START_HAR intent is received.
     * It can be stopped using STOP_HAR. When a new activity is recognized the
     * service sends out a RETURN_ACTIVITY intent, which contains the name of the activity
     * in the "activity" field. A list of supported activities can be found in the file
     * {@link eu.liveandgov.wp1.human_activity_recognition.Activities}
     *
     */
    public static final String ACTION_START_HAR = "eu.liveandgov.sensorminingapi.intent.action.START_HAR";
    public static final String ACTION_STOP_HAR = "eu.liveandgov.sensorminingapi.intent.action.STOP_HAR";
    public static final String RETURN_ACTIVITY = "eu.liveandgov.sensorcollectorapi.intent.return.ACTIVITY";
    public static final String FIELD_ACTIVITY = "activity";

}
