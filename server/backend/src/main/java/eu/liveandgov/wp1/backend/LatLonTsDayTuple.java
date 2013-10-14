package eu.liveandgov.wp1.backend;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class LatLonTsDayTuple {

	private float m_lat;
	private float m_lng;
	private int m_date;
	private int m_time;
	private String m_day;
	// Lat,Lon,Timestamp,DayOfWeek
	// 60.1652805,24.95296666,2013-09-23 20:06:36,Mon
	private Pattern pattern = Pattern.compile("(.+),(.+),(.+) (.+),(.+)");

	public LatLonTsDayTuple() {
	}
	public LatLonTsDayTuple(String csvLine) {
		Matcher matcher = pattern.matcher(csvLine);

		if (matcher.find()) {
			m_lat = Float.parseFloat(matcher.group(1));
			m_lng = Float.parseFloat(matcher.group(2));
			m_date = Integer.parseInt(matcher.group(3).replace("-", ""));
			m_time = Integer.parseInt(matcher.group(4).replace(":", ""));
			m_day = matcher.group(5);
		}
		System.out.println( m_day + " " + m_date + " " + m_time + " " + m_lat + " " + m_lng);
	}
	public String getLonLatPoint() {
		return "POINT(" + m_lng + " " + m_lat + ")";
	}
	public int getDateAsInt() {
		return m_date;
	}
	public int getDaytimeAsInt() {
		return m_time;
	}
	public String getDayOfWeek() {
		return m_day;
	}
}
