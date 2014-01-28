package eu.liveandgov.wp1.human_activity_recognition.containers;

/**
 * Created with IntelliJ IDEA.
 * User: cehlen
 * Date: 06/01/14
 * Time: 18:44
 * To change this template use File | Settings | File Templates.
 */
public class CountWindow {
    public String type;
    public String id;
    public String tag;
    public long startTime;
    public long endTime;
    public float[] x;
    public float[] y;
    public float[] z;
    public double frequency;

    public String toString() {
        String result = id;
        if (tag != null && tag.length() != 0) {
            result += ",\"" + tag + "\",";
        }
        for (int i = 0; i < x.length; i++) {
            result += x[i] + "," + y[i] + "," + z[i];
            if ((i + 1) != x.length) {
                result += ",";
            }
        }
        return result;
    }

}
