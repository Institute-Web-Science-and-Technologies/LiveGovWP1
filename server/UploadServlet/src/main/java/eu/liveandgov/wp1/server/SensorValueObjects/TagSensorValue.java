package eu.liveandgov.wp1.server.SensorValueObjects;

public class TagSensorValue extends SensorValue {

	public final SampleType type = SampleType.TAG;
	public String tag;

	public static TagSensorValue fromRSV(RawSensorValue rsv) {
		TagSensorValue out = new TagSensorValue();

		out.timestamp = rsv.timestamp;
		out.id = rsv.id;

		out.tag = rsv.value;

		return out;
	}

	public String toString() {
		return String.format("TSV - type:%s ts:%d id:%s tag:%s", type,
				timestamp, id, tag);
	}

}
