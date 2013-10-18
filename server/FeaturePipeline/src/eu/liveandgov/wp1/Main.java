package eu.liveandgov.wp1;

import eu.liveandgov.wp1.Features.FeatureHelper;
import eu.liveandgov.wp1.Window.Window;

import java.util.ArrayList;
import java.util.Date;

public class Main {

    public static final String DEVICE_ID = "61c206d1a77d509e";

    public static void main(String[] args) {
        SQLHelper helper = new SQLHelper("liveandgov", "liveandgov");

        ArrayList<String> tags = helper.getAllTagsForId(DEVICE_ID);
        for(String t : tags) {
            System.out.println(t);
        }
        Window[] windows = helper.getWindows("acc", DEVICE_ID, "test", 1000, new Date(1381334892922L-2*60*60*1000), 5000);
        for (Window w : windows) {
            helper.saveFeatureWindow(w);
        }
    }


}
