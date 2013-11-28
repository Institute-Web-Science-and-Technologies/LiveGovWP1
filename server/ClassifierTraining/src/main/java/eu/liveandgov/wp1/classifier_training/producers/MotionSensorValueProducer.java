package eu.liveandgov.wp1.classifier_training.producers;

import eu.liveandgov.wp1.classifier_training.Log;
import eu.liveandgov.wp1.classifier_training.helper.database.DBHelper;
import eu.liveandgov.wp1.human_activity_recognition.connectors.Producer;
import eu.liveandgov.wp1.human_activity_recognition.containers.MotionSensorValue;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by cehlen on 10/19/13.
 */
public class MotionSensorValueProducer extends Producer<MotionSensorValue> {
    public int globalCount = 0;

    public void getFromDatabase(String tag, String id) {
        ResultSet rs = DBHelper.getByTagId(tag, id);
        try {
            int count = 0;
            while(rs.next()) {
                MotionSensorValue tmsv = new MotionSensorValue();
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