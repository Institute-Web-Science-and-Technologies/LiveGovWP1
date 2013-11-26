package eu.liveandgov.wp1;

import eu.liveandgov.wp1.database.DBHelper;
import eu.liveandgov.wp1.human_activity_recognition.*;

import java.util.List;

//
// To run this script locally open an ssh tunnel to the remote server:
//
// ssh -L 5432:localhost:5432 user@RemoteHost
//
public class Main {

    public static int WINDOW_LENGTH_IN_MS = 5 * 1000;
    public static int WINDOW_OVERLAP_IN_MS = WINDOW_LENGTH_IN_MS - 20;

    public static void main(String[] args) {

        // SETUP FeatureExtraction Pipeline

        DBHelper.connect("liveandgov", "liveandgov");

        TaggedMotionSensorValueProducer sensorValueProducer = new TaggedMotionSensorValueProducer();

        TaggedWindowProducer windowProducer = new TaggedWindowProducer(WINDOW_LENGTH_IN_MS, WINDOW_OVERLAP_IN_MS);
        sensorValueProducer.setConsumer(windowProducer);

        TaggedFeatureProducer featureProducer = new TaggedFeatureProducer();
        windowProducer.setConsumer(featureProducer);

        CSVFileProducer csvFileProducer = new CSVFileProducer();
        featureProducer.setConsumer(csvFileProducer);

        // Start Classification
        List<String> ids = DBHelper.getAllIds();
        for (String id : ids) {
            List<String> tags = DBHelper.getTagsForId(id);
            for (String tag : tags) {
                Log.Log("New tag/id: " + tag + " " + id);
                sensorValueProducer.getFromDatabase(tag, id);
                windowProducer.clear();
            }
        }
        csvFileProducer.close();
        Log.Log("Global Count:" + sensorValueProducer.globalCount);
    }


}
