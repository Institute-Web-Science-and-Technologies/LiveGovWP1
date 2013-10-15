package eu.liveandgov.wp1.backend;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class LatLonTsDayTuple {
	
	private float m_lat;
	private float m_lng;
	private Date m_dateTime;
	private String m_day;
	// Lat,Lon,Timestamp,DayOfWeek
	// 60.1652805,24.95296666,2013-09-23 20:06:36,Mon
	private Pattern pattern = Pattern.compile("(.+),(.+),(.+),(.+)");
	private SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss"); 
	public LatLonTsDayTuple() {
	}
	public LatLonTsDayTuple(String csvLine) throws ParseException {
		Matcher matcher = pattern.matcher(csvLine);

		if (matcher.find()) {
			m_lat = Float.parseFloat(matcher.group(1));
			m_lng = Float.parseFloat(matcher.group(2));
			m_dateTime = ft.parse(matcher.group(3));
			m_day = matcher.group(4);
		}
//		System.out.println( m_day + " " + String.format("%tF %tT", m_dateTime, m_dateTime ) + " " + m_lat + " " + m_lng);
//		System.out.println(getAbbreviatedWeekdayName() + " " + getDateDigitsOnly() + " " + getDaytimeDigitsOnly());
	}
	public String getLonLatPoint() {
		return String.format(Locale.US, "POINT(%.5f %.5f)", m_lng, m_lat);
	}
	public String getDateDigitsOnly() {
		return String.format(Locale.US,"%tY%tm%td", m_dateTime, m_dateTime, m_dateTime );
	}
	public String getDaytimeDigitsOnly() {
		return getDaytimeDigitsOnly(m_dateTime);
	}
	public String getWeekdayName() {
		return String.format(Locale.US,"%tA", m_dateTime );
	}
	public String getUTC() {
		return String.format(Locale.US,"%tF %tT", m_dateTime, m_dateTime );
	}
	public String getBetweenTimeClause(int toleranceInMinutes) {
		long ONE_MINUTE_IN_MILLIS=60000;
		long t = m_dateTime.getTime();
		Date d0 = new Date(t - (toleranceInMinutes * ONE_MINUTE_IN_MILLIS));
		Date d1 = new Date(t + (toleranceInMinutes * ONE_MINUTE_IN_MILLIS));
		return getDaytimeDigitsOnly(d0) + " AND " + getDaytimeDigitsOnly(d1);
	}
	public String getISO8601Date() {
		return String.format(Locale.US,"%tF", m_dateTime );
	}
	private String getDaytimeDigitsOnly(Date d) {
		return String.format(Locale.US,"%tH%tM%tS", d, d, d );
	}
}
