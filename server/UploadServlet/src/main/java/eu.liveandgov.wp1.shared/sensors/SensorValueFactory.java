package eu.liveandgov.wp1.shared.sensors;

import eu.liveandgov.wp1.shared.sensors.sensor_value_objects.*;

import java.text.ParseException;
import java.util.HashSet;
import java.util.Set;

/**
 * User: hartmann
 * Date: 10/22/13
 */


public class SensorValueFactory {

    private static final Set<String> SampleTypeValues = new HashSet<String>();

    static {
        for (SampleType t: SampleType.values()) {
            SampleTypeValues.add(t.toString());
        }
    }

    /**
     * Factory Method that parses a .ssf-line to the corresponding AbstractSensorValue Object.
     *
     * @param line
     * @return SVO      AbstractSensorValue Object. Null if type not found.
     */
    public static SensorValueInterface parse(String line) throws ParseException {
        try {
            // split csv
            String[] fields = line.split(",", 4);
            if (fields.length != 4) throw new ParseException("Error parsing csv " + line,0);

            // parse fields
            if (! SampleTypeValues.contains(fields[0])){
                return null; // sensor type not supported
            }
            SampleType type = SampleType.valueOf(fields[0]);

            long timestamp = Long.parseLong(fields[1]);
            String id = fields[2];
            String value = fields[3];

            switch (type) {
                case ACC:
                case LAC:
                case GRA:
                    return parseMotionSensorValue(type, timestamp, id, value);
                case GPS:
                    return new GPSSensorValue(timestamp, id, value);
                case ACT:
                    return new GoogleActivitySensorValue(timestamp, id, value);
                case TAG:
                    return new TagSensorValue(timestamp, id, value);
            }
        } catch (NumberFormatException e){
            throw new ParseException("Error timestamp in " + line,0);
        } catch (IllegalArgumentException e){
            throw new ParseException("Error parsing sample-type. Is the type registered in SampleType-enum? " + line,0);
        }
        // nothing found?
        return null;
        // throw new ParseException("Sensor type not found in " + line, 0);
    }

    public static SensorValueInterface parseMotionSensorValue(SampleType type, long timestamp, String id, String value)
            throws ParseException {

        String[] stringValues = value.split(" ");
        if (stringValues.length != 3) throw new ParseException("Cannot parse value " + value,0);

        try {
            float x = Float.parseFloat(stringValues[0]);
            float y = Float.parseFloat(stringValues[1]);
            float z = Float.parseFloat(stringValues[2]);

            switch (type) {
                case ACC:
                    return new AccSensorValue(timestamp, id, x, y, z);
                case LAC:
                    return new LacSensorValue(timestamp, id, x, y, z);
                case GRA:
                    return new GraSensorValue(timestamp, id, x, y, z);
            }

        } catch(IllegalArgumentException e){
            throw new ParseException("Error parsing float values in " + value,0);
        }

        // nothing found?
        throw new ParseException("MotionSensor type not supported:" + type, 0);
    }

}
