package eu.liveandgov.wp1.backend;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONObject;

/**
* USAGE:
*
*  mvn clean compile exec:java -Dexec.mainClass=eu.liveandgov.wp1.backend.LiveApiRecorder -Dexec.args="/home/chrisschaefer/ 10000"
*
*/
public class LiveApiRecorder {

	public static void main(String [] args) throws InterruptedException {
		try
		{

			SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd", Locale.US);
			ft.setTimeZone(TimeZone.getTimeZone( "Europe/Helsinki" ));
						
		    String filename= args[0] + ft.format(new Date()) + "-HSL-Live-API-Record.csv";
		    @SuppressWarnings("resource")
			FileWriter fw = new FileWriter(filename,true); //the true will append the new data
		    fw.write("##day,ts,route_id,trip_id,lat,lon,dir,departure\n");
		    int totalLines = 1;
		    while(true) {
			    BufferedReader in = new BufferedReader(new InputStreamReader(new URL(
						"http://mobile-sensing.west.uni-koblenz.de:8080/backend/LiveAPI").openStream()));
	
				String line;
				String jsonString = "";
				while ((line = in.readLine()) != null) {
					jsonString += line;
				}
				
				JSONObject jsonObject = new JSONObject(jsonString);
				JSONArray arr = jsonObject.getJSONArray("vehicles");
				for (int i = 0; i < arr.length(); i++) {
					JSONObject o = arr.getJSONObject(i);
					fw.write(o.get("day") + "," + o.get("ts") + "," + o.get("route_id") + "," + o.get("trip_id") + "," 
					+ o.get("lat") + "," + o.get("lon") + "," + o.get("dir") + "," + o.get("departure") + "\n");
					totalLines++;
				}
				fw.flush();
				System.out.println("Total lines: " + totalLines);
				Thread.sleep(Integer.parseInt(args[1]));
		    }
		}
		catch(IOException ioe)
		{
		    System.err.println("IOException: " + ioe.getMessage());
		}
	}
}

