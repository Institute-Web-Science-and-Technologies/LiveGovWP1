package eu.liveandgov.wp1.pipeline;

import eu.liveandgov.wp1.data.Tuple;
import eu.liveandgov.wp1.data.Window;

/**
 * Created by cehlen on 25/02/14.
 */
public class InterpolationPipeline extends Pipeline<Tuple<Long, Window>, Tuple<Long, Window>>  {

    private int numberOfSamples;

    public InterpolationPipeline(int numberOfSamples) {
        this.numberOfSamples = numberOfSamples;
    }

    @Override
    public void push(Tuple<Long, Window> longWindowTuple) {
        Window w = longWindowTuple.right;
        if (w.x.length < 2) {
            System.out.println("Dropped window of length is smaller than 2");
            return;
        }

        Window new_window = new Window();

        new_window.startTime = w.startTime;
        new_window.endTime = w.endTime;

        double length = (w.endTime - w.startTime);

        double spacing = length / (double)numberOfSamples;

        new_window.x = new float[numberOfSamples];
        new_window.y = new float[numberOfSamples];
        new_window.z = new float[numberOfSamples];


        int indexOfStart = 0;
        int indexOfEnd = 1;

        long currentTime = new_window.startTime;

        // Calculate each value
        for (int i = 0; i < numberOfSamples; i++) {

            // Get the right samples to interpolate
            try {
            while(!(w.time[indexOfStart] <= currentTime && currentTime <= w.time[indexOfEnd])) {
                // Here we just iterate through all times we have and try to find a window that fits
                indexOfStart = indexOfEnd;
                indexOfEnd += 1;
            }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            long startTime = w.time[indexOfStart];
            long endTime = w.time[indexOfEnd];

            // Calculate slope
            double mX = (w.x[indexOfEnd] - w.x[indexOfStart]) / (endTime - startTime);
            double mY = (w.y[indexOfEnd] - w.y[indexOfStart]) / (endTime - startTime);
            double mZ = (w.z[indexOfEnd] - w.z[indexOfStart]) / (endTime - startTime);

            // Calculate the y-intercept
            new_window.x[i] = (float)(w.x[indexOfStart] + mX * (currentTime - startTime));
            new_window.y[i] = (float)(w.y[indexOfStart] + mY * (currentTime - startTime));
            new_window.z[i] = (float)(w.z[indexOfStart] + mZ * (currentTime - startTime));

            // Calculate the value
            // y = m * x + c
//            cw.x[i] = (float)(mX * currentTime + cX);
//            cw.y[i] = (float)(mY * currentTime + cY);
//            cw.z[i] = (float)(mZ * currentTime + cZ);
            currentTime += spacing;
        }

        Tuple<Long, Window> t = new Tuple<Long, Window>(longWindowTuple.left, new_window);
        produce(t);
    }
}
