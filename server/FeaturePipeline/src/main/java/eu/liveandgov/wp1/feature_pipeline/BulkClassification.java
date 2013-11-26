package eu.liveandgov.wp1.feature_pipeline;

import eu.liveandgov.wp1.feature_pipeline.helper.database.DBHelper;
import eu.liveandgov.wp1.feature_pipeline.producers.MotionSensorValueProducer;
import eu.liveandgov.wp1.human_activity_recognition.HarPipeline;
import eu.liveandgov.wp1.human_activity_recognition.connectors.Consumer;

import java.util.List;

//
// To run this script locally open an ssh tunnel to the remote server:
//
// ssh -L 5432:localhost:5432 user@RemoteHost
//
public class BulkClassification {

    public static int WINDOW_LENGTH_IN_MS = 5 * 1000;
    public static int WINDOW_OVERLAP_IN_MS = 0;

    public static void main(String[] args) {

        // SETUP FeatureExtraction Pipeline

        DBHelper.connect("liveandgov", "liveandgov");

        MotionSensorValueProducer sensorValueProducer = new MotionSensorValueProducer();

        HarPipeline harPipeline = new HarPipeline();
        sensorValueProducer.setConsumer(harPipeline);

        harPipeline.setConsumer(new Consumer<String>() {
            public void push(String message) {
                System.out.println(message);
            }
        });

        // Start Classification
        List<String> ids = DBHelper.getAllIds();
        for (String id : ids) {
            List<String> tags = DBHelper.getTagsForId(id);
            for (String tag : tags) {
                Log.Log("New tag/id: " + tag + " " + id);
                sensorValueProducer.getFromDatabase(tag, id);
            }
        }
        Log.Log("Global Count:" + sensorValueProducer.globalCount);
    }


}
