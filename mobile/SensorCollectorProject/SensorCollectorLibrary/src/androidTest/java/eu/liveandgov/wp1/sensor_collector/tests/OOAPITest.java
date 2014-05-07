package eu.liveandgov.wp1.sensor_collector.tests;

import junit.framework.TestCase;

import eu.liveandgov.wp1.pps.api.CalculationResult;
import eu.liveandgov.wp1.pps.api.ooapi.OSMIPPS;

public class OOAPITest extends TestCase {
    private static final double[][] POSITIVE = {{50.343062, 7.587613}, {50.34514, 7.588736}, {50.350108, 7.590339}, {50.358634, 7.595564}, {50.340566, 7.592065}};
    private static final double[][] NEGATIVE = {{50.360733, 7.557225}, {50.359557, 7.559765}, {50.358358, 7.562471}, {50.359766, 7.572712}, {50.355583, 7.573123},
            {50.351232, 7.577668}};

    public void testOSMIPPS() {
        double dia = 33.0;

        OSMIPPS ips = new OSMIPPS(dia / 111132.954, dia / 111132.954, true, 2048, "http://overpass.osm.rambler.ru/cgi/", dia);

        // Positive count/true positive
        final int pc = POSITIVE.length;
        int tp = 0;

        for (double[] p : POSITIVE) {
            if (ips.calculate(p[0], p[1]).type == CalculationResult.CalculationType.IN_PROXIMITY)
                tp++;
            else
                System.err.println(p + " should be in proximity");
        }

        // Negative count/true negative
        final int nc = NEGATIVE.length;
        int tn = 0;

        for (double[] n : NEGATIVE) {
            if (ips.calculate(n[0], n[1]).type == CalculationResult.CalculationType.NOT_IN_PROXIMITY)
                tn++;
            else
                System.err.println(n + " should not be in proximity");
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
        if (tp + fp != 0)
            System.out.println("Precision: " + (tp / (tp + fp)));
        System.out.println();
        if (pc + nc != 0)
            System.out.println("Accuracy: " + ((tp + tn) / (pc + nc)));

        assertEquals(pc, tp);
        assertEquals(nc, tn);
    }

}
