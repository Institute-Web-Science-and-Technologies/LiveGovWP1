package eu.liveandgov.wp1.collector.persistence;

import android.util.Log;

import java.io.Serializable;
import java.util.NoSuchElementException;

/**
 * Created by hartmann on 9/15/13.
 */
public class Buffer implements Serializable {

    private final int capacity;
    private final String[] buffer;
    private int supremum = 0;

    public Buffer(int capacity){
        Log.i("FA", "Creating Buffer of Capacity " + capacity);
        this.capacity = capacity;
        this.buffer   = new String[this.capacity];
    }

    public void reset() {
        for (int i = 0; i< capacity; i++){
            buffer[i] = null;
        }
        supremum = 0;
    }

    public void push(String s) {
        // if (!supInRange(supremum)) throw new IllegalStateException("supremum " + supremum + " of " + capacity);
        if (isFull()) throw new IndexOutOfBoundsException("Buffer Full " + supremum);
        buffer[supremum] = s;
        supremum += 1;
    }

    public String pull() {
        // if (!supInRange(supremum)) throw new IllegalStateException("supremum " + supremum + " of " + capacity);
        if (isEmpty()) throw new IndexOutOfBoundsException("Buffer Empty " + supremum);
        supremum -= 1;
        String out = buffer[supremum];
        buffer[supremum] = null;
        return out;
    }


    public String[] getBuffer(){
        return buffer;
    }

    public boolean isFull() {
        return supremum == capacity;
    }

    public boolean isEmpty() {
        return supremum == 0;
    }

    private boolean supInRange(int i){
        return ( (0 >= i) && (i < capacity) );
    }
}
