package eu.liveandgov.wp1.human_activity_recognition;

import eu.liveandgov.wp1.Log;
import eu.liveandgov.wp1.connectors.Producer;
import eu.liveandgov.wp1.database.DBHelper;
import eu.liveandgov.wp1.sensors.TaggedMotionSensorValue;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by cehlen on 10/19/13.
 */
public class TaggedMotionSensorValueProducer extends Producer<TaggedMotionSensorValue> {
    public int globalCount = 0;


    public void getFromDatabase(String tag, String id) {
        ResultSet rs = DBHelper.getByTagId(tag, id);
        try {
            int count = 0;
            while(rs.next()) {
                TaggedMotionSensorValue tmsv = new TaggedMotionSensorValue();
                tmsv.tag =  rs.getString("tag");
                tmsv.id =   rs.getString("id");
                tmsv.x =    rs.getFloat("x");
                tmsv.y =    rs.getFloat("y");
                tmsv.z =    rs.getFloat("z");
                tmsv.time = rs.getTime("ts").getTime();
                consumer.push(tmsv);
                count++;
            }
            Log.Log("Count: " + count);
            globalCount += count;
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }
}