package eu.liveandgov.sensorcollectorv3.configuration;

/**
 * Intents that are NOT part of the official API.
 *
 * Created by hartmann on 10/26/13.
 */
public class ExtendedIntentAPI {

    /**
     * Stream Sensor Samples to the remote server (using zmq).
     */
    public static String START_STREAMING =  "eu.liveandgov.sensorminingapi.intent.action.START_STREAMING";
    public static String STOP_STREAMING = "eu.liveandgov.sensorminingapi.intent.action.STOP_STREAMING";
    // this is part of the GET_STATUS INTENT
    public static final String FIELD_STREAMING = "streaming";

    /**
     * @Deprecated Send Log messages to the GUI
     *
     * The {@code RETURN_LOG} intent contains a "message" field.
     */
    public static final String LOGGING_ENABLE = "eu.liveandgov.sensorcollectorapi.intent.action.LOGGING_ENABLE";
    public static final String LOGGING_DISABLE = "eu.liveandgov.sensorcollectorapi.intent.action.LOGGING_DISABLE";
    public static final String RETURN_LOG = "eu.liveandgov.sensorcollectorapi.intent.return.LOG";
    public static final String FIELD_MESSAGE = "message";


    /**
     * Get latest GPS samples
     *
     * Returns the last captured GPS samples, for use in the Service Line Detection service.
     */
    public static final String ACTION_GET_GPS = "eu.liveandgov.sensorminingapi.intent.action.GET_GPS";
    public static final String FIELD_COUNT = "count";
    public static final String RETURN_GPS = "eu.liveandgov.sensorminingapi.intent.action.GET_GPS";
    public static final String FIELD_ENTRIES = "entries";

}
