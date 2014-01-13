package eu.liveandgov.wp1.human_activity_recognition.producers;

import eu.liveandgov.wp1.human_activity_recognition.connectors.Consumer;
import eu.liveandgov.wp1.human_activity_recognition.connectors.Producer;
import eu.liveandgov.wp1.human_activity_recognition.containers.TaggedWindow;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Created with IntelliJ IDEA.
 * User: cehlen
 * Date: 06/01/14
 * Time: 16:12
 * To change this template use File | Settings | File Templates.
 */
public class QualityFilter extends Producer<TaggedWindow> implements Consumer<TaggedWindow> {

    private double minFreq;

    private Logger logger;

    public QualityFilter(double minFreq, String logFile) {
        this.minFreq = minFreq;

        // Setup logger
        logger = Logger.getLogger("QualityFilter");
        FileHandler fh;

        try {
            fh = new FileHandler(logFile);
            logger.addHandler(fh);

            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);

        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }


    public void push(TaggedWindow tw) {
        double windowFreq = (double)tw.x.length / ((tw.endTime - tw.startTime) / 1000.0);

        // Check if the frequency matches our desired frame
        if (windowFreq < this.minFreq) {
            logger.info("< Frequency: " + windowFreq + " StartTime: " + tw.startTime + " EndTime: " + tw.endTime + " Samples: " + tw.x.length);
            return;
        }

        consumer.push(tw);
    }

    public void clear() {
        consumer.clear();
    }
}
