package eu.liveandgov.wp1.collector;

import android.app.Activity;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.Menu;

import eu.liveandgov.wp1.collector.persistence.MockPersister;
import eu.liveandgov.wp1.collector.persistence.PersistenceSQLite;
import eu.liveandgov.wp1.collector.persistence.PersistenceTester;
import eu.liveandgov.wp1.collector.transfer.TransferTest;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        runTests();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void runTests() {
        new PersistenceTester(new MockPersister());
        new TransferTest( (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE));

        }


}
