package eu.liveandgov.sensorcollectorv3;

import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class MainActivity_OLD extends Activity {
    static boolean firstRun = true;
    static TextView logView;
    public static Persistor P;
    public static TransferThread T;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        logView = (TextView) findViewById(R.id.log);

        if (!firstRun) return;

        P = new FilePersistor(this);

        new Thread(new SensorThread(this)).start();

        T = new TransferThread(P);
        new Thread(T).start();

        Handler H = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message inputMessage) {
                setText((String) inputMessage.getData().get("msg"));
            }
        };
        new Thread(new MonitorThread(H)).start();

        firstRun = false;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    public void onTransferClick(View v){
        T.trigger();
    }

    public static void setText(String s){
        logView.setText(s);
    }

}
