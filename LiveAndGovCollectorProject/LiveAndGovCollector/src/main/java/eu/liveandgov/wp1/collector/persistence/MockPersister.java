package eu.liveandgov.wp1.collector.persistence;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Mock class for testing, that just caches the lines in memory.
 *
 * Created by hartmann on 9/13/13.
 */
public class MockPersister implements PersistenceInterface {

    LinkedList<String> Q = new LinkedList<String>();

    @Override
    public void save(String value) {
        Q.addLast(value);
    }

    @Override
    public List<String> readLines(int n) {
        List<String> out = new ArrayList<String>(n);
        for (int i = 0; i < n; i++){
            out.add(i,Q.removeFirst());
        }
        return out;
    }

    public String pull(){
        return Q.removeFirst();
    }

    @Override
    public int getRecordCount() {
        return Q.size();
    }
}
