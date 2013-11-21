package eu.liveandgov.wp1;

import eu.liveandgov.wp1.database.DBHelper;
import eu.liveandgov.wp1.human_activity_recognition.*;

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

        // TODO:
        // FOR ID IN TEST_TABLE:
        //     FOR TAG IN TEST_TABLE:
                    tmsvp.getFromDatabase("running", "trst13");
        //           RESET PIPELINE
                    csvfp.close();

    }


}
