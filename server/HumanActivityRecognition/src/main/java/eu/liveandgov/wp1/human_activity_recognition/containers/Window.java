package eu.liveandgov.wp1.human_activity_recognition.containers;

import eu.liveandgov.wp1.human_activity_recognition.helper.FeatureHelper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: cehlen
 * Date: 10/11/13
 * Time: 7:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class Window {
    private String type;
    private String id;
    private String tag;
    private Date startTime;
    private Date endTime;
    private List<Float> x;
    private List<Float> y;
    private List<Float> z;


    // 1    - type
    // 2    - id
    // 3    - ts
    // 4-6  - values
    // 7    - tag
    public Window(String type, String tag, String id, ResultSet rs) {
        this.x = new ArrayList();
        this.y = new ArrayList();
        this.z = new ArrayList();

        this.type = type;
        this.tag = tag;
        this.id = id;

        try {
            do {
                if(getStartTime() == null) this.startTime = rs.getTime("ts");
                this.x.add(rs.getFloat("x"));
                this.y.add(rs.getFloat("y"));
                this.z.add(rs.getFloat("z"));
                this.endTime = rs.getTime("ts");
            } while(rs.next());
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public String toString() {
        return String.format("[%s %s %s] %tT:%tL - %tT:%tL: %d %d %d ",
                getType(), getTag(), getId(), getStartTime(), getStartTime(), getEndTime(), getEndTime(), x.size(), y.size(), z.size());
    }

    public float[] getX(){
        return convertArray(x);
    }
    public float[] getY(){
        return convertArray(y);
    }
    public float[] getZ(){
        return convertArray(z);
    }

    private float[] convertArray(List<Float> floatList) {
        float[] floatArray = new float[floatList.size()];

        for (int i = 0; i < floatList.size(); i++) {
            Float f = floatList.get(i);
            floatArray[i] = (f != null ? f : Float.NaN); // Or whatever default you want.
        }
        return floatArray;
    }

    // Features
    public float getXMean() {
        return FeatureHelper.mean(getX());
    }
    public float getYMean() {
        return FeatureHelper.mean(getY());
    }
    public float getZMean() {
        return FeatureHelper.mean(getZ());
    }

    public float getXVar() {
        return FeatureHelper.var(getX());
    }
    public float getYVar() {
        return FeatureHelper.var(getY());
    }
    public float getZVar() {
        return FeatureHelper.var(getZ());
    }

    public float getS2Mean() {
        return FeatureHelper.mean(FeatureHelper.S2(getX(), getY(), getZ()));
    }
    public float getS2Var() {
        return FeatureHelper.var(FeatureHelper.S2(getX(), getY(), getZ()));
    }

    public String getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public String getTag() {
        return tag;
    }

    public Date getStartTime() {
        return startTime;
    }

    public Date getEndTime() {
        return endTime;
    }
}
