package eu.liveandgov.sensorcollectorv3.Configuration;

/**
 * SensorCollector IntentAPI
 *
 * Describes control commands that can be sent to the SensorCollectorService.
 *
 * Created by hartmann on 9/26/13.
 */
public class IntentAPI {
    /**
     * Enable/Disable the collection of sensor samples.
     * Sensors can be configured in the class {@link SensorCollectionOptions}
     */
    public static final String SAMPLING_ENABLE = "eu.liveandgov.sensorcollectorapi.intent.action.SAMPLING_ENABLE";
    public static final String SAMPLING_DISABLE = "eu.liveandgov.sensorcollectorapi.intent.action.SAMPLING_DISABLE";

    /**
     * Request a status update from the service.
     * Status updates are returned via the RETURN_STATUS intent, and contain the following
     * boolean fields:
     * "sampling"     - true if the service is recording samples
     * "transferring" - true if the service is transfering data to the server
     * "running"      - true if the service is running [DEPRECATED: Always True??]
     */
    public static final String GET_STATUS = "eu.liveandgov.sensorcollectorapi.intent.action.GET_STATUS";
    public static final String RETURN_STATUS = "eu.liveandgov.sensorcollectorapi.intent.return.STATUS";

    /**
     * Add an annotation sample to the sensor stream.
     * The annotation is contained in the "tag" field of the intent.
     */
    public static final String ANNOTATE = "eu.liveandgov.sensorcollectorapi.intent.action.ANNOTATE";

    /**
     * Triggers the data transfer to the server.
     */
    public static final String TRANSFER_SAMPLES = "eu.liveandgov.sensorcollectorapi.intent.action.TRANSFER_SAMPLES";

    /**
     * DEPRECATED: Send Log messages to the GUI
     *
     * The {@code RETURN_LOG} intent contains a "message" field.
     */
    public static final String LOGGING_ENABLE = "eu.liveandgov.sensorcollectorapi.intent.action.LOGGING_ENABLE";
    public static final String LOGGING_DISABLE = "eu.liveandgov.sensorcollectorapi.intent.action.LOGGING_DISABLE";
    public static final String RETURN_LOG = "eu.liveandgov.sensorcollectorapi.intent.return.LOG";

    /**
     * DEPRECATED: Start and Stop the service.
     *
     * This intents should be removed if possible.
     * The service needs to be running after the sampling has finished in order for the
     * Transfer to work.
     */
    public static final String SERVICE_START = "eu.liveandgov.sensorcollectorapi.intent.action.SERVICE_START";
    public static final String SERVICE_STOP = "eu.liveandgov.sensorcollectorapi.intent.action.SERVICE_STOP";


}
