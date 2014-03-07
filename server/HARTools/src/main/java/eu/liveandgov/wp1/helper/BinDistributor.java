package eu.liveandgov.wp1.helper;

/**
 * Created with IntelliJ IDEA.
 * User: cehlen
 * Date: 28/01/14
 * Time: 16:17
 * To change this template use File | Settings | File Templates.
 */
public class BinDistributor {
    private float max;
    private float min;
    private int bin_count;
    private int [] bins;

    /**
     * @param min Value for lowest bin (Check: min < value)
     * @param max Value for highest bin (Check: max >= value)
     * @param n number of bins between min and max
     */
    public BinDistributor(float min, float max, int n) {
        this.max = max;
        this.min = min;
        this.bin_count = n;
    }

    public int [] getBinsForAxis(float[] axis) {
        this.bins = new int [bin_count + 2]; // default initialized with 0

        for (float v : axis) {
            if (v < min) {
                bins[0] += 1;
            } else if (v >= max) {
                bins[bin_count + 1] += 1;
            } else {
                int index = (int) Math.floor((bin_count * (v - min)) / (max - min)) + 1;
                bins[index] += 1;
            }
        }
        return bins;
    }
}