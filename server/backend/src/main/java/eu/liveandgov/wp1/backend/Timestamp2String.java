package eu.liveandgov.wp1.backend;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

public class Timestamp2String {
	private static SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss", Locale.US); 
	public static void main(String[] args) {
		try {
			FileInputStream fstream = new FileInputStream(args[0]);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			ft.setTimeZone(TimeZone.getTimeZone( "Europe/Helsinki" ));
			while ((strLine = br.readLine()) != null) {
				long ts = Long.parseLong(strLine);
				System.out.println(ft.format(ts));
			}

			in.close();
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
	}
}
