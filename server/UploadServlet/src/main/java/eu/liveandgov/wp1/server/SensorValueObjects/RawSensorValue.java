package eu.liveandgov.wp1.server.SensorValueObjects;

public class RawSensorValue extends SensorValue {
	public String value;
	
	public RawSensorValue(SampleType type, long timestamp, String id, String value) {
		this.type = type;
		this.timestamp = timestamp;
		this.id = id;
		this.value = value;
	}
	
	public static RawSensorValue fromString(String line){
		String[] fields = line.split(",", 4);
		if (fields.length != 4) throw new IllegalArgumentException("Error parsing string " + line );
		
		return new RawSensorValue(
				SampleType.valueOf(fields[0]), 	// type
				Long.parseLong(fields[1]), 		// timestamp
				fields[2],						// id
				fields[3]					    // value string
				);
	}
	
	public String toString(){
		return String.format("RSV - type:%s ts:%d id:%s val:%s", type, timestamp, id, value);
	}
}
