package eu.liveandgov.wp1.helper;

import static org.junit.Assert.assertTrue;

/**
 * Created by hartmann on 3/7/14.
 */
public class BinDistributorTest {

    BinDistributor BD;

    @org.junit.Before
    public void setUp() throws Exception {
        BD = new BinDistributor(0,100,10);
    }

    @org.junit.After
    public void tearDown() throws Exception {

    }

    @org.junit.Test
    public void testGetBinsForAxis() throws Exception {
        int [] res = BD.getBinsForAxis(new float[]{0});
        assertTrue(res[0] == 0); // < min
        assertTrue(res[1] == 1); // [0,10)
    }

    @org.junit.Test
    public void testGetBinsForAxis2() throws Exception {
        int [] res = BD.getBinsForAxis(new float[]{-1});
        assertTrue(res[0] == 1); // [-inf, 0)
        assertTrue(res[1] == 0); // [0,10)
        assertTrue(res[2] == 0); // [10, 20)
        assertTrue(res[10] == 0); // [90, 100)
        assertTrue(res[11] == 0); // >= 100
    }

    @org.junit.Test
    public void testGetBinsForAxis3() throws Exception {
        int [] res = BD.getBinsForAxis(new float[]{0,0,0,0});
        assertTrue(res[0] == 0); // < min
        assertTrue(res[1] == 4); // [0,10]
        assertTrue(res[2] == 0); // [10, 20)
        assertTrue(res[10] == 0); // [90, 100)
        assertTrue(res[11] == 0); // >= 100
    }

    @org.junit.Test
    public void testGetBinsForAxis4() throws Exception {
        int [] res = BD.getBinsForAxis(new float[]{99,100});
        assertTrue(res[0] == 0); // < min
        assertTrue(res[1] == 0); // [0,10]
        assertTrue(res[2] == 0); // [10, 20)
        assertTrue(res[10] == 1); // [90, 100)
        assertTrue(res[11] == 1); // >= 100
    }
}
