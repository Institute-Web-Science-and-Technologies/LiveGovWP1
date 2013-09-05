/**
 * Persistent Queue implementation for JAVA
 *
 * Copyright (C) 2009 Mario Hahnemann
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see <http://www.gnu.org/licenses/>.
 */

package de.snipworks.queue.persistentqueue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * This class implements one queue that stores all data to files in file
 * system. In difference to other implementation this class is quite small and
 * it doesn't holds information in memory to avoid out of memory trouble.
 *
 * <br><br><b>This implemantion comes with same interface as implementation of
 * Gabor Cselle mail@gaborcselle.com
 * <br>http://www.gaborcselle.com/writings/java/persistent_queue.html</b>
 *
 * 
 * @author Mario Hahnemann
 * @param <E> type of elements that have to be stored into queue
 */
public class PersistenceQueue < E extends Serializable > implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final int ENTRIES_PER_FILE = 50;

    private static final String CONTROL_EXTENSION = ".ctl";

    private static final String TMP_EXTENSION = ".tmp";
    
    private int entriesPerFile = ENTRIES_PER_FILE; 

    private Control controlClass = null;

    private String controlFileName = null;

    private String tmpFileName = null;

    private String baseName;

    // Cache for last not removed element;
    private E lastElement;

    /* All data will be read from stream, so it is not possible to go back.
     * peek() will read but not remove elements. To hold elements in file and
     * to have possibility to read elements again (also for remove()) this attributes
     * holds last element read from stream
     */
    private boolean markRemoved = true;

    /* cache for read stream, so it is not necessary to open new stream with each read
     * please note that caching of object streams is not working in JDK 1.5 (not tested 
     * for JDK 1.6)
     */
    private FileInputStream readStream = null;
    
    // remember for last read file
    private String lastInputFile = "";

    /* cache for write stream, so it is not necessary to open new stream with each write
     * please note that caching of object streams is not working in JDK 1.5 (not tested 
     * for JDK 1.6)
     */
    private FileOutputStream writeStream = null;

    // remember for last write file
    private String lastOutputFile = "";

    /**
     * Public contructor for Queue. 
     * @param filename the base file name for queue store. Please note there will be at least 2
     * files created *.ctl as control file and *.(timestamp) for data.
     * @throws java.io.IOException if it is impossible to create the necessary files
     */
    public PersistenceQueue(final String filename) throws IOException {
        initQueue(filename);
        }

    /**
     * Public contructor for Queue.
     * @param filename the base file name for queue store. Please note there will be at least 2
     * files created *.ctl as control file and *.(timestamp) for data.
     * @param filesize sets the number of entries per file (default = 5 and impossible to go under
     * this count)
     * @throws java.io.IOException IOException if it is impossible to create the necessary files
     */
    public PersistenceQueue(final String filename, final int filesize) throws IOException {
        if (filesize > ENTRIES_PER_FILE) {
            entriesPerFile = filesize;
        }
        initQueue(filename);
    }
    
    private void initQueue(final String filename) throws IOException {
        this.baseName = filename;
        this.controlFileName = filename + CONTROL_EXTENSION;
        this.tmpFileName = filename + TMP_EXTENSION;
        File control = new File(this.controlFileName);
        this.controlClass = new Control();
        // first try to recover old data files
        if (control.exists()) {
            recoverControlFile();
            int counter = this.controlClass.getReadPosition() - 1;
            /* if your last read position was in the middle of the file
             * it is necessary to re read the file until the former
             * read position will be reached
             */
            while (counter > 0) {
                this.readObject();
                counter--;
            }

        } else {
            // create a new file infrastructure
            createNewControlFile();
        }
    }

    /**
     * Clears the entire queue and forces the underlying file to be rewritten.
     * 
     * @throws java.io.IOException
     *             if an I/O error occurs
     */
    public synchronized void clear() throws IOException {
        File file = new File(this.controlFileName);
        file = new File(file.getParent());
        
        // release the streams to ensure that delete will work
        release();

        // delete all files, with basename at the beginning
        File[] fileList = file.listFiles(new FilterClass((new File(this.baseName + ".")).getName()));

        for (File deleteFile : fileList) {
            deleteFile.delete();
        }
        createNewControlFile();
    }

    /**
     * Returns true if the queue contains no elements.
     * 
     * @return true if the queue contains no elements.
     */
    public synchronized boolean isEmpty() {
        boolean result = false;
        if (size() == 0) {
            result = true;
        }
        return result;
    }

    /**
     * Returns the number of elements in this queue.
     * 
     * @return the number of elements in this queue
     */
    public synchronized int size() {
        return controlClass.getQueueSize();
    }

    /**
     * Retrieves, but does not remove, the head of this queue, returning
     * <code>null</code> if this queue is empty.
     * 
     * @return the head of this queue, or null if this queue is empty.
     */
    @SuppressWarnings("unchecked")
    public synchronized E peek() {
        Serializable object = null;
        E result = null;
        // if last element were removed, then read new element from stream
        if (this.markRemoved) {
            try {
                object = (Serializable) readObject();
            }
            catch (IOException e) {
                return null;
            }
            try {
                /* Now check if foot entry were received. Foot entry marks last element
                 * in file and contains name of next file 
                 */
                if (object instanceof FootEntry) {
                    FootEntry foot = (FootEntry) object;
                    
                    // delete the last file, because all entries are received from it
                    deleteLastFile();
                    this.controlClass.setReadPosition(0);
                    this.controlClass.setReadFile(foot.getNextFile()); 
                    // read again
                    object = (Serializable) readObject();
                    if (object instanceof FootEntry) {
                        return null;
                    }
                }
            }
            catch (IOException e) {
                return null;
            }
            this.controlClass.setReadPosition(this.controlClass.getReadPosition() + 1);
            result = (E) object;
            this.lastElement = result;
            try {
                this.writeControlFile();
            }
            catch (IOException ex) {
                // TODO should throw Exception insted of returning null
                result = null;
            }
            // reset remove marker
            this.markRemoved = false;
        } else {
            // if last element were not removed then this element is in cache
            result = this.lastElement;
        }
        return result;
    }

    /**
     * Removes and returns the head element of the persistent queue.
     * 
     * @return head element of this queue, or <code>null</code> if queue is
     *         empty.
     * @throws java.io.IOException
     *             if an I/O error occurs
     */
    public synchronized E remove() throws IOException {
        E result = null;

        
        if (this.markRemoved) {
            result = peek();
        } else {
            // return last read element from cache
            result = this.lastElement;
        }
        this.markRemoved = true;
        if ((result != null) && (this.controlClass.getQueueSize() > 0)) {
            this.controlClass.setQueueSize(this.controlClass.getQueueSize() - 1);
            writeControlFile();
        }
        return (E) result;
    }

    /**
     * Adds an element to the tail of the queue.
     * 
     * @param element
     *            the element to add
     * @throws java.io.IOException
     *             if an I/O error occurs
     */
    public synchronized void add(final E element) throws IOException {
        // check if maximum length were exceeded
        // if not
        if (this.controlClass.getWritePosition() < entriesPerFile) {
            this.controlClass.setWritePosition(this.controlClass.getWritePosition() + 1);
        } else {
            // if true new file has to be created

            //DateFormat form = DateFormat.getInstance();
            //String dateString = form.format(new Date());
            String dateString = ActualTimestamp.getTimeStamp();

            String outputFileName = this.baseName + "." + dateString;
            File outputFile = new File(outputFileName);
            outputFile.createNewFile();

            // write foot element
            FootEntry foot = new FootEntry();
            foot.setNextFile(outputFileName);

            writeObject(foot);

            this.controlClass.setWriteFile(outputFileName);

            this.controlClass.setWritePosition(0);
        }

        writeObject(element);

        this.controlClass.setQueueSize(this.controlClass.getQueueSize() + 1);
        writeControlFile();
    }

    /**
     * Releases all open File[Input|Output]Streams. Thats necessary to be able to remove the
     * files
     * @throws java.io.IOException if unable to realese streams
     */
    public synchronized void release() throws IOException {
        if (this.readStream != null) {
            this.readStream.close();
            this.readStream = null;
        }
        if (this.writeStream != null) {
            this.writeStream.close();
            this.writeStream = null;
        }
    }

    private void createNewControlFile() throws IOException {
        this.controlClass = new Control();
        File file = new File(this.controlFileName);
        file.createNewFile();

        //DateFormat form = DateFormat.getInstance();
        //String dateString = form.format(new Date());
        String dateString = ActualTimestamp.getTimeStamp();

        /* if new empty control file is written also the data 
         * be recreated
         * 
         */ 
        String ioFileName = this.baseName + "." + dateString;
        File ioFile = new File(ioFileName);
        ioFile.createNewFile();

        this.controlClass.setReadFile(ioFileName);

        this.controlClass.setWriteFile(ioFileName);

        writeControlFile();
    }

    private void recoverControlFile() throws IOException {
        FileInputStream fis = new FileInputStream(this.controlFileName);
        ObjectInputStream ois = new ObjectInputStream(fis);
        try {
            Serializable oc = (Serializable) ois.readObject();
            if (oc instanceof Control) {
                this.controlClass = (Control) oc;
            } else {
                throw new IOException("incompatible file structure for control " + "file: " + this.controlFileName);
            }
        }
        catch (ClassNotFoundException ex) {
            throw new IOException("unable to load control information " + "from: " + this.controlFileName);
        }
        fis.close();
    }

    private void writeControlFile() throws IOException {
        File tmpFile = new File(this.tmpFileName);
        tmpFile.createNewFile();
        FileOutputStream tmpControl = new FileOutputStream(tmpFile);
        ObjectOutputStream oos = new ObjectOutputStream(tmpControl);
        oos.writeObject(this.controlClass);
        oos.flush();
        oos.close();
        File controlFile = new File(this.controlFileName);
        controlFile.delete();
        if (!tmpFile.renameTo(controlFile)) {
            throw new IOException("Unable to rename temporary control file: " + this.tmpFileName);
        }
    }

    private void writeObject(final Serializable s) throws IOException {
        try {
            if (!this.lastOutputFile.equals(this.controlClass.getWriteFile())) {
                if (this.writeStream != null) {
                    this.writeStream.flush();
                    this.writeStream.close();
                }
                FileOutputStream fos = new FileOutputStream(this.controlClass.getWriteFile(), true);
                this.writeStream = fos;

                this.lastOutputFile = this.controlClass.getWriteFile();
            }
            ObjectOutputStream oos = new ObjectOutputStream(this.writeStream);
            oos.writeObject(s);
            //  oos.flush();
        }
        catch (FileNotFoundException e) {
            throw new IOException("File " + this.controlClass.getWriteFile()
                + " not found");

        }
    }

    private Serializable readObject() throws IOException {
        Serializable result = null;

        if (!this.lastInputFile.equals(this.controlClass.getReadFile())) {
            if (this.readStream != null) {
                this.readStream.close();
            }
            FileInputStream fis = new FileInputStream(this.controlClass.getReadFile());
            this.readStream = fis;

            this.lastInputFile = this.controlClass.getReadFile();
        }

        try {
            if (this.readStream.available() > 0) {
                ObjectInputStream ois = new ObjectInputStream(this.readStream);
                result = (Serializable) ois.readObject();
            }
        }
        catch (ClassNotFoundException e) {
            throw new IOException("Class not found to read in");
        }
        if (result == null) {
            throw new IOException("Zero element received while reading object");
        }
        return result;
    }

    private void deleteLastFile() throws IOException {
        if (this.readStream != null) {
            this.readStream.close();
            this.readStream = null;
        }
        File file = new File(this.controlClass.getReadFile());
        if (!file.delete()) {
            file.deleteOnExit();
        }
    }
}
