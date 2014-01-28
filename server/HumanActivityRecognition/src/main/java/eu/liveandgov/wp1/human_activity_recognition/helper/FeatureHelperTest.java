package eu.liveandgov.wp1.human_activity_recognition.helper;

import org.apache.commons.math3.complex.Complex;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by hartmann on 1/28/14.
 */
public class FeatureHelperTest {
    @Test
    public void testPadZero() throws Exception {

        assertArrayEquals(
                FeatureHelper.padZero(new float[]{1, 1, 1, 1}),
                new float[] {1,1,1,1},
                1E-5F // precision
        );

        assertArrayEquals(
                FeatureHelper.padZero(new float[]{1, 1, 1}),
                new float[] {1,1,1,0},
                1E-5F // precision
        );

        assertArrayEquals(
                FeatureHelper.padZero(new float[]{1, 1, 1, 1, 1}),
                new float[] {1,1,1,1,1,0,0,0},
                1E-5F // precision
        );
    }

    @Test
    public void testAbs() throws Exception {
        assertArrayEquals(
                FeatureHelper.Abs(new Complex[] {
                    new Complex(3.0D, 0.0D),
                    new Complex(4.0D, 0.0D),
                    new Complex(0.0D, 5.0D)
                }),
                new float [] { 3 , 4, 5 },
                1E-5F // precision
        );

        assertArrayEquals(
                FeatureHelper.Abs(new Complex[] {}),
                new float [] {},
                1E-5F // precision
        );

        assertArrayEquals(
                FeatureHelper.Abs(new Complex[] {
                        new Complex(1.0D, 1.0D),
                }),
                new float [] {
                        (float) new Complex(1.0D, 1.0D).abs()
                },
                1E-5F // precision
        );
    }

    @Test
    public void testAFFT() throws Exception {
        float [] result =  FeatureHelper.FTAbsolute(new float[]{
                0, 0, 0, 1, 1, 1, -1, -1, -1
        });
    }
}