package eu.liveandgov.wp1.backend.format;

import java.io.IOException;
import java.io.Reader;
import java.util.LinkedList;
import java.util.List;

import org.supercsv.io.CsvListReader;
import org.supercsv.prefs.CsvPreference;

public class Parser {

	public static List<Sample> parse(Reader reader) throws IOException {
		List<Sample> samples = new LinkedList<Sample>();
		CsvListReader csv = new CsvListReader(reader,CsvPreference.STANDARD_PREFERENCE);
		List<String> columns;
		while ((columns = csv.read()) != null) {
			// fill object with fields from row
			Sample sample = new Sample();
			sample.setType(SampleType.valueOf(columns.get(0)));
			sample.setTimestamp(Long.parseLong(columns.get(1)));
			sample.setId(columns.get(2));
			sample.setValue(columns.get(3));

			// write to samples list
			samples.add(sample);
		}
		csv.close();
		return samples;
	}
	
	public static float[] getAccValues(String s) {
		String[] stringValues = s.split(" ");
		if (stringValues.length < 3) {
			System.out.println("Warning: Cannot parse line " + s);
			return null;
		}
		
		float[] floatValues = new float[3];
		for (int i = 0; i < 3; i++) {
			floatValues[i] = Float.parseFloat(stringValues[i]);
		}
		return floatValues;		
	}
}
