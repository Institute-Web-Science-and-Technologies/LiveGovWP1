package eu.liveandgov.sensorcollectorv3.sensors;

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
    public String type;
    public String id;
    public long startTime;
    public long endTime;
    public float[] x;
    public float[] y;
    public float[] z;
}
