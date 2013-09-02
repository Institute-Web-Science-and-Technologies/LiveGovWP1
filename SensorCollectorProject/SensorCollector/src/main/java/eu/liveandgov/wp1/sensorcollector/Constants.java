package eu.liveandgov.wp1.sensorcollector;

public class Constants {
    public static final String SERVER = "141.26.71.84";
    public static final String UPLOAD_URL = "http://" + SERVER + ":3001/";
    public static final String FORM_FIELD= "upfile";

    public static final Integer MAX_TRANSFER_SAMPLES = 100;


	public static final String ACTION_SAMPLING_ENABLE = "eu.liveandgov.intent.action.SAMPLING_ENABLE";
	public static final String ACTION_SAMPLING_DISABLE = "eu.liveandgov.intent.action.SAMPLING_DISABLE";
}
