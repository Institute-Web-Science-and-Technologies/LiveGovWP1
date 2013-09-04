package eu.liveandgov.wp1.backend.SensorValueObjects;

import java.util.LinkedList;
import java.util.Queue;



public class SampleWindow<ValueClass extends SensorValue> {
	public long startTime;
	private Queue<ValueClass> vQ = new LinkedList<ValueClass>();
	public int capacity;
	
	public SampleWindow(int capacity) {
		this.capacity = capacity;
	}
	
	public void add(ValueClass v) {
		vQ.add(v);
		if (vQ.size() > capacity) vQ.remove();
		if (vQ.size() > capacity + 1) throw new IllegalStateException("Queue size exeeds capacity.");
	}
	
	public boolean isFull() {
		return (vQ.size() >= capacity);
	}

	public ValueClass[] getValues(){
		return (ValueClass[]) vQ.toArray();
	}
	
	public String toString(){
		return String.format("SW - ts:%d cap:%d size:%d", startTime, capacity, vQ.size());
	}
		
}