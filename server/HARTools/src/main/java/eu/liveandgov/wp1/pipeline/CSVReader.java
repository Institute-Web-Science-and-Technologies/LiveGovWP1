package eu.liveandgov.wp1.pipeline;

import eu.liveandgov.wp1.data.Tuple;
import eu.liveandgov.wp1.data.Window;
import eu.liveandgov.wp1.data.impl.Acceleration;

import java.io.*;

/**
 * Created by cehlen on 07/03/14.
 */
public class CSVReader extends Producer<Tuple<Long, Acceleration>> {
    private WindowPipeline wp;
    public String currentTag = "UNKNOWN";


    public CSVReader(WindowPipeline wp) {
        this.wp = wp;
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
                currentTag = file.getName();
                readFiles(file.listFiles(), file.getName(), tagged);
            } else {
                System.out.println("Reading file " + file.getPath());
                read(file.getPath(), tag, tagged);
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



                String id = value[0];
                long time = Long.parseLong(value[1].replace("\"", ""));
                float x = Float.parseFloat(value[2].replace("\"", ""));
                float y = Float.parseFloat(value[3].replace("\"", ""));
                float z = Float.parseFloat(value[4].replace("\"", ""));

                float[] values = new float[] {x, y, z};

                Acceleration acc = new Acceleration(time, id, values);
                Tuple<Long, Acceleration> t = new Tuple<Long, Acceleration>(0L, acc);
                produce(t);
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
                    wp.clear();
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        }
    }
}
