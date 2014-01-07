package eu.liveandgov.wp1.human_activity_recognition;

import eu.liveandgov.wp1.human_activity_recognition.containers.CountWindow;
import eu.liveandgov.wp1.human_activity_recognition.containers.TaggedWindow;
import eu.liveandgov.wp1.human_activity_recognition.helper.Persistor;
import eu.liveandgov.wp1.human_activity_recognition.producers.*;


/**
 * Created with IntelliJ IDEA.
 * User: cehlen
 * Date: 06/01/14
 * Time: 21:23
 * To change this template use File | Settings | File Templates.
 */
public class Main {

    public static void main(String args[]) {
        CSVReader csvReader = new CSVReader();


        WindowProducer windowProducer = new WindowProducer(5000, 4995);
        csvReader.setConsumer(windowProducer);

        QualityFilter qualityFilter = new QualityFilter(50.0, 1.0, "windowFreq.log");
        windowProducer.setConsumer(qualityFilter);


        Interpolator interpolator = new Interpolator(50.0);
        qualityFilter.setConsumer(interpolator);

        Persistor<CountWindow> p = new Persistor("out.csv");
        interpolator.setConsumer(p);

        csvReader.read("/Users/cehlen/TrainingData/WALKING/12");
    }

}
