package eu.liveandgov.wp1.collector;

import android.app.Activity;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import eu.liveandgov.wp1.collector.persistence.MockPersister;
import eu.liveandgov.wp1.collector.persistence.PersistenceSQLite;
import eu.liveandgov.wp1.collector.persistence.PersistenceTester;
import eu.liveandgov.wp1.collector.transfer.TransferTest;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Start Recording Service
        Intent intent = new Intent(this, ZmqRecordingService.class);
        startService(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void runTests(View view) {
        new PersistenceTester(new MockPersister());

        // Need to spawn new thread for network connection.
        new Thread(new Runnable() {
            @Override
            public void run() {
                new TransferTest();
            }
        }).start();
    }
}
