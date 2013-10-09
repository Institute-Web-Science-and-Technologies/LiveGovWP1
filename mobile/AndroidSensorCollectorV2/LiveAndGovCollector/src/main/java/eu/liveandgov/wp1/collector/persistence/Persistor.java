package eu.liveandgov.wp1.collector.persistence;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by hartmann on 9/15/13.
 */
public class Persistor {
    private static final String LOG_TAG = "PERS";
    public static int BUFFER_SIZE = 5000; // No of recorded strings.

    private final Context context;

    private Buffer inBuffer = new Buffer(BUFFER_SIZE);
    private Buffer outBuffer = new Buffer(BUFFER_SIZE);

    public static final int NO_ROT_FILES = 100;
    public static final String PREFIX = "SENSOR_ROT_";
    private int inFileNo = 0;
    private int outFileNo = 0;

    public Persistor(Context context){
        this.context = context;
    }
    
    public synchronized void push(String s){
        if(!inBuffer.isFull()) {
            inBuffer.push(s);
        } else {
            Log.i(LOG_TAG, "inBuffer full");
            File inf = nextInFile();
            if (inf == null) return; // drop if buffer is full
            persistBuffer(inBuffer, inf);
            inBuffer.reset();
        }
    }

    public synchronized String pull(){
        if( outBuffer.isEmpty() ){
            Log.i(LOG_TAG, "outBuffer empty");
            File of = nextOutFile();
            if (of == null) return null;
            outBuffer = readBuffer(of);
        }
        return outBuffer.pull();
    }

    private Buffer readBuffer(File file) {
        Log.i(LOG_TAG, "Reading Buffer " + file.getName() );
        try{
            FileInputStream fin = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fin);
            Buffer out = (Buffer) ois.readObject();
            ois.close();
            return out;
        } catch (IOException e){
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private File nextOutFile() {
        if ( outFileNo == inFileNo ) return null; // Do not get ahead of inFile
        int nextOutFileNo = ((outFileNo + 1) % NO_ROT_FILES);
        if ( nextOutFileNo == inFileNo ) return null; // ring buffer full;
        outFileNo = nextOutFileNo;
        return getFile(outFileNo);
    }

    private File nextInFile() {
        int nextInFileNo = ((inFileNo + 1) % NO_ROT_FILES);
        if ( nextInFileNo == outFileNo ) return null; // ring buffer full;
        inFileNo = nextInFileNo;
        return getFile(inFileNo);
    }

    private void persistBuffer(Buffer inBuffer, File file){
        Log.i(LOG_TAG, "Persisting Buffer to " + file.getName() );
        try{
            FileOutputStream fout = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            oos.writeObject(inBuffer);
            oos.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private File getFile(int id){
        return new File(context.getFilesDir(), PREFIX + id + ".log");
    }

}
