package eu.liveandgov.wp1.human_activity_recognition.classifier;

import eu.liveandgov.wp1.human_activity_recognition.Activities;

/**
 * Created by cehlen on 10/19/13.
 */
public class CrappyDecisionTree {

    public static double classify(Object[] i) throws Exception {

        double p = Double.NaN;
        p = CrappyDecisionTree.N2cf889c825(i);
        return p;
    }

    static double N2cf889c825(Object[] i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 0;
        } else if (((Double) i[0]).doubleValue() <= 103.159134) {
            p = CrappyDecisionTree.N711dc08826(i);
        } else if (((Double) i[0]).doubleValue() > 103.159134) {
            p = 1;
        }
        return p;
    }

    static double N711dc08826(Object[] i) {
        double p = Double.NaN;
        if (i[3] == null) {
            p = 0;
        } else if (((Double) i[3]).doubleValue() <= 4.136329) {
            p = 0;
        } else if (((Double) i[3]).doubleValue() > 4.136329) {
            p = CrappyDecisionTree.N584b5abc27(i);
        }
        return p;
    }

    static double N584b5abc27(Object[] i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 1;
        } else if (((Double) i[2]).doubleValue() <= 0.143865) {
            p = 1;
        } else if (((Double) i[2]).doubleValue() > 0.143865) {
            p = 0;
        }
        return p;
    }

    public static String myClassify(Object[] i) throws Exception {
        double p = Double.NaN;
        p = CrappyDecisionTree.N2cf889c825(i);
        if(p == 0.0) {
            return Activities.WALKING;
        } else {
            return Activities.RUNNING;
        }
    }
}
