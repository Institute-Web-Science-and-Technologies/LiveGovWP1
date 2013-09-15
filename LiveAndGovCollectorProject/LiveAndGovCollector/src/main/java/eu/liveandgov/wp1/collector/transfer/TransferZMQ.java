package eu.liveandgov.wp1.collector.transfer;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jeromq.ZMQ;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by hartmann on 9/15/13.
 */
public class TransferZMQ implements TransferInterface {

    private ZMQ.Context c;
    private ZMQ.Socket s;

    /**
     * Setup ZMQ Socket connection (async)
     */
    public void setup(){
        c = ZMQ.context();
        s = c.socket(ZMQ.PUSH);
        s.connect("tcp://" + TransferManagerConfig.REMOTE_HOST + ":5555");
    }


    /**
     * Transfers a the content String to the server via ZMQ push/pull socket
     * BLocks until transfer ist finished;
     **/
    public synchronized boolean transferData(String content) {
        s.send(content);
        return true;
    }
}
