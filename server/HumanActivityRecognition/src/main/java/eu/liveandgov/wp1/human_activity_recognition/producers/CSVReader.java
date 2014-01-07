package eu.liveandgov.wp1.human_activity_recognition.producers;

import eu.liveandgov.wp1.human_activity_recognition.connectors.Producer;
import eu.liveandgov.wp1.human_activity_recognition.containers.MotionSensorValue;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: cehlen
 * Date: 06/01/14
 * Time: 21:26
 * To change this template use File | Settings | File Templates.
 */
public class CSVReader extends Producer<MotionSensorValue> {

    public CSVReader() {

    }


    public void read(String filePath) {
        BufferedReader br = null;
        String line = "";

        try {
            br = new BufferedReader(new FileReader(filePath));
            while ((line = br.readLine()) != null) {
                String[] value = line.split(",");

                MotionSensorValue msv = new MotionSensorValue();
                msv.id = value[0];
                msv.time = Long.parseLong(value[1].replace("\"", ""));
                msv.x = Float.parseFloat(value[2].replace("\"", ""));
                msv.y = Float.parseFloat(value[3].replace("\"", ""));
                msv.z = Float.parseFloat(value[4].replace("\"", ""));

                consumer.push(msv);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        }
    }
}
