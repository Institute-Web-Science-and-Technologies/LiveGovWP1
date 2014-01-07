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

    private double frequency;

    public Interpolator(double frequency) {
        this.frequency = frequency;
    }

    public void push(TaggedWindow tw) {

        CountWindow cw = new CountWindow();

        cw.frequency = frequency;
        cw.startTime = tw.startTime;
        cw.endTime = tw.endTime;

        double length = (cw.endTime - cw.startTime) / 1000;

        int numberOfSamples =  (int)Math.floor(length * frequency);

        cw.x = new float[numberOfSamples];
        cw.y = new float[numberOfSamples];
        cw.z = new float[numberOfSamples];


        int indexOfStart = 0;
        int indexOfEnd = 1;

        long currentTime = cw.startTime;
        int currentIndex = 0;

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
            double cX = tw.x[indexOfStart] - mX * startTime;
            double cY = tw.y[indexOfStart] - mY * startTime;
            double cZ = tw.z[indexOfStart] - mZ * startTime;

            // Calculate the value
            // y = m * x + c
            cw.x[currentIndex] = (float)(mX * currentTime + cX);
            cw.y[currentIndex] = (float)(mY * currentTime + cY);
            cw.z[currentIndex] = (float)(mZ * currentTime + cZ);
            currentIndex += 1;
            currentTime += 1 / frequency * 1000;
        }

        consumer.push(cw);
    }

    public void clear() {
        consumer.clear();
    }
}
