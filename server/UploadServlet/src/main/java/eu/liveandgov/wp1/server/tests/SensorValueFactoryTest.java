package eu.liveandgov.wp1.server.tests;

import eu.liveandgov.wp1.server.sensor_helper.SensorValueFactory;
import eu.liveandgov.wp1.server.sensor_helper.SensorValueInterface;
import junit.framework.TestCase;

import java.text.ParseException;

/**
 * User: hartmann
 * Date: 10/22/13
 * Time: 10:52 PM
 */
public class SensorValueFactoryTest extends TestCase {

    public void testAccSample() {
        SensorValueInterface svo = null;
        try {
            svo = SensorValueFactory.parse("ACC,1382474704061,61c206d1a77d509e,0.020690918 0.10997009 9.800659");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        assert(svo != null);
    }
}
