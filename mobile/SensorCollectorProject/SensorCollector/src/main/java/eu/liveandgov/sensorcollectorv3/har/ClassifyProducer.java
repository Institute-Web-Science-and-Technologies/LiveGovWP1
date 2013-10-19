package eu.liveandgov.sensorcollectorv3.har;

import android.util.Log;

import eu.liveandgov.sensorcollectorv3.connector.Consumer;
import eu.liveandgov.sensorcollectorv3.connector.Producer;

/**
 * Created by cehlen on 10/19/13.
 */
public class ClassifyProducer extends Producer<String> implements Consumer<FeatureVector> {

    @Override
    public void push(FeatureVector m) {
        try {
            String activity = ActivityRecognition.myClassify(m.toWekaObjArr());
            Log.i("HAR_CLASS", activity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
