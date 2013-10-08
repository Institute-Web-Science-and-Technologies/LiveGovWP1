package eu.liveandgov.wp1.backend.SensorValueObjects;

import eu.liveandgov.wp1.backend.format.SampleType;

public class GoogleActivitySensorValue extends SensorValue {
	public final SampleType type = SampleType.ACT;
	public String activity;

	public static GoogleActivitySensorValue fromRSV(RawSensorValue rsv) {
		GoogleActivitySensorValue out = new GoogleActivitySensorValue();

		out.timestamp = rsv.timestamp;
		out.id = rsv.id;

		out.activity = rsv.value;

		return out;
	}

	public String toString() {
		return String.format("GASV - type:%s ts:%d id:%s tag:%s", type,
				timestamp, id, activity);
	}
}
