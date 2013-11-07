package eu.liveandgov.wp1.hsllivelogger;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

public class HSLLiveLogger {

	private static final String REQUEST = "http://83.145.232.209:10001/?type=vehicles&lng1=20&lat1=60&lng2=30&lat2=70&online=1";
	private static final int DELAY = 1000;
	private static final String LOG = "";

	public static List<String> poll() throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(new URL(
				REQUEST).openStream()));
		long ts = System.currentTimeMillis();
		LinkedList<String> lines = new LinkedList<String>();
		while (in.ready()) {
			lines.add(ts + ";" + in.readLine());
		}
		return lines;
	}

	@SuppressWarnings("resource")
	public static void main(String[] args) {
		FileWriter out = null;
		try {
			out = new FileWriter(LOG);
		} catch (IOException e) {
			System.err.println("Could not open logfile. Exiting.");
			System.exit(-1);
		}
		System.out.println("Starting polling.");
		while (true) {
			try {
				for (String line : poll()) {
					out.write(line + "\n");
				}
				out.flush();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			try {
				Thread.sleep(DELAY);
			} catch (InterruptedException e2) {
				e2.printStackTrace();
			}
		}

	}

}
