package eu.liveandgov.sensorcollectorv3.har;

/**
 * Created by cehlen on 10/19/13.
 */
public class ActivityRecognition {

    public static double classify(Object[] i) throws Exception {

        double p = Double.NaN;
        p = ActivityRecognition.N2cf889c825(i);
        return p;
    }

    static double N2cf889c825(Object[] i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 0;
        } else if (((Double) i[0]).doubleValue() <= 103.159134) {
            p = ActivityRecognition.N711dc08826(i);
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
            p = ActivityRecognition.N584b5abc27(i);
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
        p = ActivityRecognition.N2cf889c825(i);
        if(p == 0.0) {
            return "walking";
        } else {
            return "running";
        }
    }
}
