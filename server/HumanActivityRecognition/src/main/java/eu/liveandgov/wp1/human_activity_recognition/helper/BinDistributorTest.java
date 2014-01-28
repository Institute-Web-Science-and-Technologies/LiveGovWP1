package eu.liveandgov.wp1.human_activity_recognition.helper;

import junit.framework.Assert;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: cehlen
 * Date: 28/01/14
 * Time: 16:20
 * To change this template use File | Settings | File Templates.
 */
public class BinDistributorTest {
    @Test
    public void testGetBinsForAxis() throws Exception {
        // Create new BinDistributor
        BinDistributor binDistributor = new BinDistributor(-1, 1, 3);

        // Create Axis
        float axis[] = new float[] {-1, 1, 3, -0.5f, -2, -0.34f};

        int bins[] = binDistributor.getBinsForAxis(axis);

        // Test if we got the right amount of bins.
        Assert.assertEquals(5, bins.length);

        // Test the bin values
        // < min
        Assert.assertEquals(2, bins[0]);

        // Bin #1
        Assert.assertEquals(2, bins[1]);

        // Bin #2
        Assert.assertEquals(0, bins[2]);

        // Bin #3
        Assert.assertEquals(1, bins[3]);

        // > max
        Assert.assertEquals(1, bins[4]);

        // Test a second to see if bins reset
        axis = new float[] {-1};
        bins = binDistributor.getBinsForAxis(axis);
        // Test the bin values
        // < min
        Assert.assertEquals(1, bins[0]);

        // Bin #1
        Assert.assertEquals(0, bins[1]);

        // Bin #2
        Assert.assertEquals(0, bins[2]);

        // Bin #3
        Assert.assertEquals(0, bins[3]);

        // > max
        Assert.assertEquals(0, bins[4]);
    }
}
