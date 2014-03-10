package eu.liveandgov.wp1.human_activity_recognition.helper;

import eu.liveandgov.wp1.human_activity_recognition.containers.CountWindow;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: cehlen
 * Date: 28/01/14
 * Time: 16:17
 * To change this template use File | Settings | File Templates.
 */
public class BinDistributor {
    private class Bin {
        public final float min;
        public final float max;

        private List<Float> values;

        public Bin(float min, float max) {
            this.min = min;
            this.max = max;
            values = new ArrayList<Float>();
        }

        public boolean addIfInside(float value) {
            if (value > min && value <= max) {
                values.add(value);
                return true;
            }
            return false;
        }

        public void clear() {
            values.clear();
        }

        public int getCount() {
            return values.size();
        }
    }

    private Bin[] bins;

    /**
     *
     * @param min Value for lowest bin (Check: min < value)
     * @param max Value for highest bin (Check: max >= value)
     * @param n number of bins between min and max
     */
    public BinDistributor(float min, float max, int n) {
        bins = new Bin[n+2];

        // < min bin
        bins[0] = new Bin(-1 * Float.MAX_VALUE, min);

        // > max bin
        bins[n+1] = new Bin(max, Float.MAX_VALUE);

        // Fill the remaining windows
        float range = max - min;
        float step = range/n;
        float currentMin = min;
        float currentMax = min + step;
        for (int i = 0; i < n; i++) {
            bins[i+1] = new Bin(currentMin, currentMax);
            currentMin = currentMax;
            currentMax += step;
        }
    }

    public Integer [] getBinsForAxis(float[] axis) {
        for (float value : axis) {
            int i = 0;
            while(!bins[i].addIfInside(value)) i++;
        }
        Integer counts[] = new Integer[bins.length];
        for (int i = 0; i < bins.length; i++) {
            counts[i] = bins[i].getCount();
            bins[i].clear();
        }
        return counts;
    }
}
