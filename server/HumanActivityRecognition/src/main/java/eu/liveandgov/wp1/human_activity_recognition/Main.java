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


        WindowProducer windowProducer = new WindowProducer(1001, 995);
        csvReader.setConsumer(windowProducer);

        QualityFilter qualityFilter = new QualityFilter(50.0, 10, "windowFreq.log");
        windowProducer.setConsumer(qualityFilter);

        Interpolator interpolator = new Interpolator(50);
        qualityFilter.setConsumer(interpolator);

        PrintProducer<CountWindow> pp = new PrintProducer<CountWindow>("InterpolatedWindow");
        interpolator.setConsumer(pp);

        Persistor<CountWindow> pers = new Persistor<CountWindow>("out.csv");
        pp.setConsumer(pers);

        csvReader.read("Test/test.csv");
    }

}
