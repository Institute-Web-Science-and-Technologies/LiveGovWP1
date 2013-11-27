package eu.liveandgov.wp1.human_activity_recognition.containers;

/**
 * Created with IntelliJ IDEA.
 * User: cehlen
 * Date: 10/11/13
 * Time: 7:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class TaggedWindow {
    public String tag;
    public String type;
    public String id;
    public long startTime;
    public long endTime;
    public float[] x;
    public float[] y;
    public float[] z;
}
