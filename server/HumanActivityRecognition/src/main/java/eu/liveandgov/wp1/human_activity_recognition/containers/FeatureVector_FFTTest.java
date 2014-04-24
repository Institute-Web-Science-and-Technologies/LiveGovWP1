package eu.liveandgov.wp1.human_activity_recognition.containers;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by hartmann on 1/28/14.
 */
public class FeatureVector_FFTTest {
    @Test
    public void testToCSV() throws Exception {
        FeatureVector_FFT test = new FeatureVector_FFT();
        test.S2Bins = new Integer[] {1 ,2 ,3};
        test.S2FTBins = new Integer[] {4 ,5 ,6};

        assertEquals(
                ",,0.000000,0.000000,0.000000,0.000000,0.000000,0.000000,0.000000,0.000000,0.000000,0.000000,0.000000,1,2,3,4,5,6",
                test.toCSV()
        );
    }
}
