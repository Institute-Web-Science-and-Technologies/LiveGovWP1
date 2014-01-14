package eu.liveandgov.wp1.human_activity_recognition.producers;

import eu.liveandgov.wp1.human_activity_recognition.connectors.Producer;
import eu.liveandgov.wp1.human_activity_recognition.containers.MotionSensorValue;

import java.io.*;

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

    public void readDir(String dir, boolean tagged) {

        File[] files = new File(dir).listFiles();
        readFiles(files, "UNKNOWN", tagged);
    }

    private void readFiles(File[] files, String tag, boolean tagged) {
        // Iterate through each file and call recursively if we encounter an directory
        for (File file : files) {
            if (file.isDirectory()) {
                System.out.println("Changing Tag to " + file.getName());
                readFiles(file.listFiles(), file.getName(), tagged);
            } else {
                System.out.println("Reading file " + file.getPath());
                read(file.getPath(), tag, tagged);
                consumer.clear();
            }
        }
    }


    public void read(String filePath, String tag,  boolean tagged) {
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
                if (tagged)
                    msv.tag = tag;

                consumer.push(msv);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println(filePath + " not valid format");
        } finally {
            if (br != null) {
                try {
                    br.close();
                    consumer.clear();
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        }
    }
}
