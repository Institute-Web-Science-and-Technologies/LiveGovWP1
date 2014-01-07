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

        int WINDOW_LENGTH_MS = 5 * 1000;
        int WINDOW_OVERLAP   = WINDOW_LENGTH_MS - 20;

        int SAMPLING_FREQUENCY_HZ = 50;
        int MIN_RECORDING_FREQUENCY = 40;

        double SAMPLE_LENGHT_MS = 1000D / SAMPLING_FREQUENCY_HZ;
        int NO_SAMPLES_PER_WINDOW = (int) (WINDOW_LENGTH_MS / SAMPLE_LENGHT_MS);

        WindowProducer windowProducer = new WindowProducer(WINDOW_LENGTH_MS, WINDOW_OVERLAP);
        csvReader.setConsumer(windowProducer);

        QualityFilter qualityFilter = new QualityFilter(
                MIN_RECORDING_FREQUENCY, "windowFreq.log");
        windowProducer.setConsumer(qualityFilter);

        Interpolator interpolator = new Interpolator(NO_SAMPLES_PER_WINDOW);
        qualityFilter.setConsumer(interpolator);

        Persistor<CountWindow> pers = new Persistor<CountWindow>("out.csv");
        interpolator.setConsumer(pers);

        csvReader.read("/home/hartmann/Desktop/Live+Gov DEVELOPMENT/TrainingData/RUNNING/5");
    }

}
