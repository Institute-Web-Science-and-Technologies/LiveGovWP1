package eu.liveandgov.wp1;

import eu.liveandgov.wp1.database.DBHelper;
import eu.liveandgov.wp1.human_activity_recognition.*;

public class Main {

    //public static final String DEVICE_ID = "61c206d1a77d509e";

    public static void main(String[] args) {

        DBHelper.connect("liveandgov", "liveandgov");

        TaggedMotionSensorValueProducer tmsvp = new TaggedMotionSensorValueProducer();

        TaggedWindowProducer wp = new TaggedWindowProducer(5000, 200);
        tmsvp.setConsumer(wp);

        TaggedFeatureProducer fp = new TaggedFeatureProducer();
        wp.setConsumer(fp);

        CSVFileProducer csvfp = new CSVFileProducer();
        fp.setConsumer(csvfp);


        tmsvp.getFromDatabase("running", "trst13");

        csvfp.close();

        /*SQLHelper helper = new SQLHelper("liveandgov", "liveandgov");

        ArrayList<String> tags = helper.getAllTagsForId(DEVICE_ID);
        for(String t : tags) {
            System.out.println(t);
        }
        TaggedWindow[] windows = helper.getWindows("acc", DEVICE_ID, "test", 1000, new Date(1381334892922L-2*60*60*1000), 5000);
        for (TaggedWindow w : windows) {
            helper.saveFeatureWindow(w);
        }*/
    }


}
