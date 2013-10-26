package eu.liveandgov.sensorcollectorv3.configuration;

/**
 * SensorCollector IntentAPI
 *
 * Describes control commands that can be sent to the SensorCollectorService.
 *
 * Created by hartmann on 9/26/13.
 */
public class IntentAPI {
    /**
     * Request a status update from the service.
     * Status updates are returned via the RETURN_STATUS intent, and contain the following
     * fields:
     * "sampling"     (bool)    true if the service is recording samples
     * "transferring" (bool)    true if the service is transfering data to the server
     * "har"          (bool)    true if HAR service is running
     * "UserId"       (String)  contains the external ID set using the SET_USER_ID intent.
     */
    public static final String GET_STATUS = "eu.liveandgov.sensorcollectorapi.intent.action.GET_STATUS";
    public static final String RETURN_STATUS = "eu.liveandgov.sensorcollectorapi.intent.return.STATUS";
    public static final String FIELD_SAMPLING = "sampling";
    public static final String FIELD_TRANSFERRING = "transferring";
    public static final String FIELD_HAR = "har";
    public static final String FIELD_ID = "id";

    /**
     * Enable/Disable the collection of sensor samples.
     * Sensors can be configured in the class {@link SensorCollectionOptions}
     */
    public static final String RECORDING_ENABLE =  "eu.liveandgov.sensorcollectorapi.intent.action.RECORDING_ENABLE";
    public static final String RECORDING_DISABLE = "eu.liveandgov.sensorcollectorapi.intent.action.RECORDING_DISABLE";

    /**
     * Triggers the data transfer to the server.
     */
    public static final String TRANSFER_SAMPLES = "eu.liveandgov.sensorcollectorapi.intent.action.TRANSFER_SAMPLES";

    /**
     * Add an annotation sample to the sensor stream.
     * The annotation is contained in the "tag" field of the intent.
     */
    public static final String ANNOTATE = "eu.liveandgov.sensorcollectorapi.intent.action.ANNOTATE";
    public static final String FIELD_ANNOTATION = "tag";

    /**
     * TODO: Set User ID
     *
     * This intent allows to set a user-id, which will be transferred to the backend service.
     * Using this id the records can be linked to users registered in the service center.
     */
    public static final String SET_USER_ID = "eu.liveandgov.sensorcollectorapi.intent.action.SET_USER_ID";
    public static final String FIELD_USER_ID = "id";

    /**
     * TODO: Human Activity Recognition
     *
     * The monitoring of activities begins when START_HAR intent is received.
     * It can be stopped using STOP_HAR. When a new activity is recognized the
     * service sends out a RETURN_ACTIVITY intent, which contains the name of the activity
     * in the "activity" field. A list of supported activities can be found in the file
     * {@link eu.liveandgov.sensorcollectorv3.har.Activities}
     *
     */
    public static final String START_HAR = "eu.liveandgov.sensorminingapi.intent.action.START_HAR";
    public static final String STOP_HAR = "eu.liveandgov.sensorminingapi.intent.action.STOP_HAR";
    public static final String RETURN_ACTIVITY = "eu.liveandgov.sensorcollectorapi.intent.return.ACTIVITY";
    public static final String FIELD_ACTIVITY = "activity";

    /**
     * TODO: Get latest GPS samples
     *
     * Returns the last captured GPS samples, for use in the Service Line Detection service.
     */
    public static final String GET_GPS = "eu.liveandgov.sensorminingapi.intent.action.GET_GPS";
    public static final String FIELD_COUNT = "count";
    public static final String RETURN_GPS = "eu.liveandgov.sensorminingapi.intent.action.GET_GPS";
    public static final String ENTRIES = "entries";


    /**
     * DEPRECATED: Send Log messages to the GUI
     *
     * The {@code RETURN_LOG} intent contains a "message" field.
     */
    public static final String LOGGING_ENABLE = "eu.liveandgov.sensorcollectorapi.intent.action.LOGGING_ENABLE";
    public static final String LOGGING_DISABLE = "eu.liveandgov.sensorcollectorapi.intent.action.LOGGING_DISABLE";
    public static final String RETURN_LOG = "eu.liveandgov.sensorcollectorapi.intent.return.LOG";
    public static final String FIELD_LOG  = "message";

}
