package eu.liveandgov.wp1.sensor_collector.tests;

import junit.framework.TestCase;

import eu.liveandgov.wp1.sensor_collector.pps.api.ProximityType;
import eu.liveandgov.wp1.sensor_collector.pps.api.ooapi.OSMIPPS;

public class OOAPITest extends TestCase {
    private static final double[][] POSITIVE = {{50.343062, 7.587613}, {50.34514, 7.588736}, {50.350108, 7.590339}, {50.358634, 7.595564}, {50.340566, 7.592065}};
    private static final double[][] NEGATIVE = {{50.360733, 7.557225}, {50.359557, 7.559765}, {50.358358, 7.562471}, {50.359766, 7.572712}, {50.355583, 7.573123},
            {50.351232, 7.577668}};

    public void testOSMIPPS() {
        double dia = 25.0;

        OSMIPPS ips = new OSMIPPS(dia / 111132.954, dia / 111132.954, true, 2048, "http://overpass.osm.rambler.ru/cgi/", dia);

        // Positive count/true positive
        final int pc = POSITIVE.length;
        int tp = 0;

        for (double[] p : POSITIVE) {
            tp += ips.calculate(p[1], p[0]) == ProximityType.IN_PROXIMITY ? 1 : 0;
        }

        // Negative count/true negative
        final int nc = NEGATIVE.length;
        int tn = 0;

        for (double[] n : NEGATIVE) {
            tn += ips.calculate(n[1], n[0]) == ProximityType.NOT_IN_PROXIMITY ? 1 : 0;
        }

        int fp = nc - tn;
        int fn = pc - tp;

        System.out.println("P: " + pc);
        System.out.println("N: " + nc);
        System.out.println();
        System.out.println("TP: " + tp);
        System.out.println("TN: " + tn);
        System.out.println("FP: " + fp);
        System.out.println("FN: " + fn);
        System.out.println();
        System.out.println("Sensitivity: " + (tp / (double) pc));
        System.out.println("Specificity: " + (tn / (double) nc));
        System.out.println();
        System.out.println("Precision: " + (tp / (tp + fp)));
        System.out.println();
        System.out.println("Accuracy: " + ((tp + tn) / (pc + nc)));

        assertEquals(pc, tp);
        assertEquals(nc, tn);
    }

}
