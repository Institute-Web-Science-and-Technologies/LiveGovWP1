// Generated with Weka 3.6.10
//
// This code is public domain and comes with no warranty.
//
// Timestamp: Tue Nov 12 16:35:23 CET 2013

package eu.liveandgov.wp1.human_activity_recognition.classifier;


import eu.liveandgov.wp1.human_activity_recognition.Activities;

public class BetterDecisionTree {

    public static double classify(Object[] i) throws Exception {

        double p = Double.NaN;
        p = BetterDecisionTree.N204d080d0(i);
        return p;
    }
    static double N204d080d0(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 0;
        } else if (((Double) i[0]).doubleValue() <= 126.493469) {
            p = BetterDecisionTree.N214b9e0c1(i);
        } else if (((Double) i[0]).doubleValue() > 126.493469) {
            p = BetterDecisionTree.N5c67aece3(i);
        }
        return p;
    }
    static double N214b9e0c1(Object []i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 4;
        } else if (((Double) i[1]).doubleValue() <= 157.278763) {
            p = BetterDecisionTree.N6b7536e72(i);
        } else if (((Double) i[1]).doubleValue() > 157.278763) {
            p = 0;
        }
        return p;
    }
    static double N6b7536e72(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 4;
        } else if (((Double) i[0]).doubleValue() <= 97.999146) {
            p = 4;
        } else if (((Double) i[0]).doubleValue() > 97.999146) {
            p = 2;
        }
        return p;
    }
    static double N5c67aece3(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 3;
        } else if (((Double) i[0]).doubleValue() <= 265.191833) {
            p = 3;
        } else if (((Double) i[0]).doubleValue() > 265.191833) {
            p = 1;
        }
        return p;
    }

    public static String myClassify(Object[] i) throws Exception {
        double p = Double.NaN;
        p = N204d080d0(i);

        if(p == 0D) {
            return Activities.CYCLING;
        } else if (p == 4D){
            return Activities.DRIVING;
        } else if (p == 2D) {
            return Activities.SITTING;
        } else if (p == 1D) {
            return Activities.RUNNING;
        } else if (p == 3D) {
            return Activities.WALKING;
        }

        return Activities.UNKNOWN;
    }
}
