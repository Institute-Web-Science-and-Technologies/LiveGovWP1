package eu.liveandgov.wp1.backend;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import org.json.JSONObject;

public class LatLonTsDayTuple {
	
	private float m_lat;
	private float m_lng;
	private Date m_dateTime;
	private String m_day;
	// Lat,Lon,Timestamp,DayOfWeek
	// 60.1652805,24.95296666,2013-09-23 20:06:36,Mon
	private Pattern pattern = Pattern.compile("(.+),(.+),(.+),(.+)");
	private SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss", Locale.US); 
	
	public LatLonTsDayTuple() {
		m_lat = 0;
		m_lng = 0;
		m_dateTime = new Date(0);
		m_day = "";
	}

	public LatLonTsDayTuple(String csvLine) throws ParseException {
		Matcher matcher = pattern.matcher(csvLine);
		ft.setTimeZone(TimeZone.getTimeZone( "Europe/Helsinki" ));

		if (matcher.find()) {
			m_lat = Float.parseFloat(matcher.group(1));
			m_lng = Float.parseFloat(matcher.group(2));			
			m_dateTime = ft.parse(matcher.group(3));
			m_day = matcher.group(4);
		}
//		System.out.println( m_day + " " + String.format("%tF %tT", m_dateTime, m_dateTime ) + " " + m_lat + " " + m_lng);
//		System.out.println(getAbbreviatedWeekdayName() + " " + getDateDigitsOnly() + " " + getDaytimeDigitsOnly());
	}
	public long getTime() {
		return m_dateTime.getTime();
	}
	public LatLonTsDayTuple(float lat, float lng){
			m_lat = lat;
			m_lng = lng;
	}
	public String getLonLatPoint() {
		return String.format(Locale.US, "POINT(%.5f %.5f)", m_lng, m_lat);
	}
	public String getDaytimeDigitsOnly() {
		return getDaytimeDigitsOnly(m_dateTime);
	}
	public String getWeekdayName() {
		SimpleDateFormat ft = new SimpleDateFormat ("EEEE", Locale.US);
		ft.setTimeZone(TimeZone.getTimeZone( "Europe/Helsinki" ));
		return ft.format(m_dateTime);
	}

	public String getBetweenTimeClause(String colName, int toleranceInMinutes) {
		long ONE_MINUTE_IN_MILLIS=60000;
		long t = m_dateTime.getTime();
		Date d0 = new Date(t - (toleranceInMinutes * ONE_MINUTE_IN_MILLIS));
		Date d1 = new Date(t + (toleranceInMinutes * ONE_MINUTE_IN_MILLIS));
		return colName + ">=" + getDaytimeSecSinceMidnight(d0) + " AND " + colName + "<" + getDaytimeSecSinceMidnight(d1);
	}
	public String getISO8601Date() {
		SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd", Locale.US);
		ft.setTimeZone(TimeZone.getTimeZone( "Europe/Helsinki" ));
		return ft.format(m_dateTime);
	}
	private String getDaytimeDigitsOnly(Date d) {
		return String.format(Locale.US,"%tH%tM%tS", d, d, d );
	}
	private int getDaytimeSecSinceMidnight(Date d) {

		Calendar calendar = GregorianCalendar.getInstance(TimeZone.getTimeZone("Europe/Helsinki")); // creates a new calendar instance
		calendar.setTime(d);   // assigns calendar to given date 
//		System.out.println( calendar.get(Calendar.HOUR_OF_DAY) + " " + calendar.get(Calendar.MINUTE)+ " " + calendar.get(Calendar.SECOND));
		return calendar.get(Calendar.HOUR_OF_DAY) * 3600 + calendar.get(Calendar.MINUTE) * 60 + calendar.get(Calendar.SECOND);
	}
	
	public String getBoundingBox(double distanceInMeter) {
    	double hypotenuse = Math.sqrt(distanceInMeter*distanceInMeter + distanceInMeter*distanceInMeter);
    	double[] topRight = DivideAndConquerGtfs.greatCircleEndPositionLatLon(m_lat, m_lng, Math.PI/4.0, hypotenuse);
    	LatLonTsDayTuple tr = new LatLonTsDayTuple((float) topRight[0],(float)topRight[1]);
    	double[] bottomLeft = DivideAndConquerGtfs.greatCircleEndPositionLatLon(m_lat, m_lng, 5*Math.PI/4.0, hypotenuse);
    	LatLonTsDayTuple bl = new LatLonTsDayTuple((float) bottomLeft[0],(float)bottomLeft[1]);
    	return "ST_MakeBox2D(St_geometryfromtext('"+tr.getLonLatPoint()+"',4326),St_geometryfromtext('"+bl.getLonLatPoint()+"',4326))";
	}

	public double getLat() {
		return m_lat;
	}
	public double getLon() {
		return m_lng;
	}
	
	public JSONObject getJson() {
		JSONObject responseJSON = new JSONObject();
		responseJSON.put("lat", m_lat);
		responseJSON.put("lng", m_lng);
		ft.setTimeZone(TimeZone.getTimeZone( "Europe/Helsinki" ));
		responseJSON.put("ts", ft.format(m_dateTime));
		responseJSON.put("day", m_day);
		return responseJSON;
	}
}
