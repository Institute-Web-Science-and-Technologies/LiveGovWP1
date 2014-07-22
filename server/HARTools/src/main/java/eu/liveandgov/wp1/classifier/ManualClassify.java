package eu.liveandgov.wp1.classifier;

import eu.liveandgov.wp1.data.FeatureVector;

/**
 * Created by hartmann on 3/8/14.
 */
public class ManualClassify {

    private static final String ON_TABLE = "on_table";
    private static final String SITTING = "sitting";
    private static final String STANDING = "standing";
    private static final String WALKING = "walking";
    private static final String RUNNING = "running";

    public static String classify(FeatureVector v) {
        if (v.s2Var < 1.5) {
            // sitting, standing, on_table
            if (v.tilt < 0.7) {
                if (v.tilt < 0.07 && v.roll < 0.07) {
                    return ON_TABLE;
                } else {
                    return SITTING;
                }
            } else {
                return STANDING;
            }

        } else {
            // walking, running
            if (v.s2Var < 30) {
                return WALKING;
            } else {
                return RUNNING;
            }
        }
    }
}
