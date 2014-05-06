package eu.liveandgov.wp1.human_activity_recognition;

import eu.liveandgov.wp1.human_activity_recognition.containers.CountWindow;
import eu.liveandgov.wp1.human_activity_recognition.containers.FeatureVector;
import eu.liveandgov.wp1.human_activity_recognition.containers.FeatureVector_FFT;
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

        int WINDOW_LENGTH_MS = 20 * 1000;
        int WINDOW_OVERLAP   = WINDOW_LENGTH_MS - 1000;

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

        FeatureProducer featureProducer = new FeatureProducer();
        interpolator.setConsumer(featureProducer);

        Persistor<FeatureVector_FFT> pers = new Persistor<FeatureVector_FFT>("out.csv");
        featureProducer.setConsumer(pers);

        csvReader.readDir("/Users/cehlen/Downloads/new_split/TEST", true);
    }

}
