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
    public static int WINDOW_OVERLAP_IN_MS = 200;

    public static void main(String[] args) {

        // SETUP FeatureExtraction Pipeline

        DBHelper.connect("liveandgov", "liveandgov");

        TaggedMotionSensorValueProducer tmsvp = new TaggedMotionSensorValueProducer();

        TaggedWindowProducer wp = new TaggedWindowProducer(WINDOW_LENGTH_IN_MS, WINDOW_OVERLAP_IN_MS);
        tmsvp.setConsumer(wp);

        TaggedFeatureProducer fp = new TaggedFeatureProducer();
        wp.setConsumer(fp);

        CSVFileProducer csvfp = new CSVFileProducer();
        fp.setConsumer(csvfp);

        // Start Classification


        List<String> ids = DBHelper.getAllIds();
        for (String id : ids) {
            List<String> tags = DBHelper.getTagsForId(id);
            for (String tag : tags) {
                Log.Log("New tag/id: " + tag + " " + id);
                tmsvp.getFromDatabase(tag, id);
                wp.clear();
            }
        }
        csvfp.close();
        Log.Log("Global Count:" + tmsvp.globalCount);
        // TODO:
        // FOR ID IN TEST_TABLE:
        //     FOR TAG IN TEST_TABLE:

//        //           RESET PIPELINE
//

    }


}
