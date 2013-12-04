package eu.liveandgov.wp1.backend;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;


public class LiveApiRecorder {

	public static void main(String [] args) throws InterruptedException {
		try
		{
			Date now = new Date();
			
		    String filename= LiveAPI.getHelsinkiDateAsSimpleDateString(now) + " HSL Live API Record.csv";
		    FileWriter fw = new FileWriter(filename,true); //the true will append the new data
		    fw.write("##day,ts,route_id,trip_id,lat,lon,dir,departure\n");
		    
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
					fw.flush();
					Thread.sleep(10000);
				}
		    }
		}
		catch(IOException ioe)
		{
		    System.err.println("IOException: " + ioe.getMessage());
		}
	}
}

