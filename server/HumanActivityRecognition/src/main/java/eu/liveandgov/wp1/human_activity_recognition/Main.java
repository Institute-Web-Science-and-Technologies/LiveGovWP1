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

        PrintProducer<TaggedWindow> pp = new PrintProducer<TaggedWindow>("TaggedWindow");
        windowProducer.setConsumer(pp);

        QualityFilter qualityFilter = new QualityFilter(50.0, 10.0, "windowFreq.log");
        pp.setConsumer(qualityFilter);

        PrintProducer<TaggedWindow> qfpp = new PrintProducer<TaggedWindow>("Quality Filter");
        qualityFilter.setConsumer(qfpp);

        Interpolator interpolator = new Interpolator(50);
        qfpp.setConsumer(interpolator);

        PrintProducer<CountWindow> ipp = new PrintProducer<CountWindow>("INTERPOLATOR");
        interpolator.setConsumer(ipp);

        Persistor<CountWindow> p = new Persistor<CountWindow>("out.csv");
        ipp.setConsumer(p);

        csvReader.read("/Users/cehlen/TrainingData/RUNNING/10");
    }

}
