package eu.liveandgov.wp1.human_activity_recognition.producers;

import eu.liveandgov.wp1.human_activity_recognition.connectors.Consumer;
import eu.liveandgov.wp1.human_activity_recognition.connectors.Producer;
import eu.liveandgov.wp1.human_activity_recognition.containers.CountWindow;
import eu.liveandgov.wp1.human_activity_recognition.containers.TaggedWindow;

/**
 * Created with IntelliJ IDEA.
 * User: cehlen
 * Date: 06/01/14
 * Time: 17:58
 * To change this template use File | Settings | File Templates.
 */
public class Interpolator extends Producer<CountWindow> implements Consumer<TaggedWindow> {

    private int numberOfSamples;

    public Interpolator(int numberOfSamples) {
        this.numberOfSamples = numberOfSamples;
    }

    public void push(TaggedWindow tw) {

        CountWindow cw = new CountWindow();

        cw.startTime = tw.startTime;
        cw.endTime = tw.endTime;

        double length = (cw.endTime - cw.startTime);

        double spacing = length / (double)numberOfSamples;

        cw.x = new float[numberOfSamples];
        cw.y = new float[numberOfSamples];
        cw.z = new float[numberOfSamples];


        int indexOfStart = 0;
        int indexOfEnd = 1;

        long currentTime = cw.startTime;

        // Calculate each value
        for (int i = 0; i < numberOfSamples; i++) {

            // Get the right samples to interpolate
            while(!(tw.time[indexOfStart] <= currentTime && currentTime <= tw.time[indexOfEnd])) {
                // Here we just iterate through all times we have and try to find a window that fits
                indexOfStart = indexOfEnd;
                indexOfEnd += 1;
            }

            long startTime = tw.time[indexOfStart];
            long endTime = tw.time[indexOfEnd];

            // Calculate slope
            double mX = (tw.x[indexOfEnd] - tw.x[indexOfStart]) / (endTime - startTime);
            double mY = (tw.y[indexOfEnd] - tw.y[indexOfStart]) / (endTime - startTime);
            double mZ = (tw.z[indexOfEnd] - tw.z[indexOfStart]) / (endTime - startTime);

            // Calculate the y-intercept
            cw.x[i] = (float)(tw.x[indexOfStart] + mX * (currentTime - startTime));
            cw.y[i] = (float)(tw.y[indexOfStart] + mY * (currentTime - startTime));
            cw.z[i] = (float)(tw.z[indexOfStart] + mZ * (currentTime - startTime));

            // Calculate the value
            // y = m * x + c
//            cw.x[i] = (float)(mX * currentTime + cX);
//            cw.y[i] = (float)(mY * currentTime + cY);
//            cw.z[i] = (float)(mZ * currentTime + cZ);
            currentTime += spacing;
        }

        consumer.push(cw);
    }

    public void clear() {
        consumer.clear();
    }
}
