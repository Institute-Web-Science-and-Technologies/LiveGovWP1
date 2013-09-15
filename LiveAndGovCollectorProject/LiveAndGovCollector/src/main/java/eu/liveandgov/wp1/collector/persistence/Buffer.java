package eu.liveandgov.wp1.collector.persistence;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.NoSuchElementException;

/**
 * Created by hartmann on 9/15/13.
 */
public class Buffer implements Serializable {

    private int CAPACITY;
    private ArrayList<String> B;
    private int position = 0;
    private ArrayList<String> buffer;

    public Buffer(int capacity){
        CAPACITY = capacity;
        B = new ArrayList<String>(CAPACITY);
        Log.i("FA", "Created Buffer of Capacity " + CAPACITY);
    }

    public void push(String s) throws IndexOutOfBoundsException {
        B.set(position, s);
        position++;
    }

    public String pull() throws NoSuchElementException {
        String out = B.get(position);
        B.set(position, null);
        position--;
        return out;
    }

    public void reset() {
        B = new ArrayList<String>(CAPACITY);
        position = 0;
    }

    public ArrayList<String> getBuffer(){
        return B;
    }

    public boolean isFull() {
        return position >= CAPACITY;
    }

    public boolean isEmpty() {
        return position == 0;
    }

    public void setBuffer(ArrayList<String> buffer) {
        this.buffer = buffer;
    }
}
