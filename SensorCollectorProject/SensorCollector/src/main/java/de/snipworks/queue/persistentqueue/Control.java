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

import java.io.Serializable;

/**
 * This is the structure of the Control file.
 * @author Mario Hahnemann
 */
public class Control implements Serializable {

    private static final long serialVersionUID = 1L;

    // name of the actual read file
    private String readFile = null;
    // name of the actual write file
    private String writeFile = null;
    // actual position in read file
    private int readPosition = 0;
    // actual posistion in write file
    private int writePosition = 0;
    // actual size of queue calculated over all queue files
    private int queueSize = 0;
    
    public int getQueueSize() {
        return queueSize;
    }
    public void setQueueSize(final int queueSize) {
        this.queueSize = queueSize;
    }
    public String getReadFile() {
        return readFile;
    }
    public void setReadFile(final String readFile) {
        this.readFile = readFile;
    }
    public int getReadPosition() {
        return readPosition;
    }
    public void setReadPosition(final int readPosition) {
        this.readPosition = readPosition;
    }
    public String getWriteFile() {
        return writeFile;
    }
    public void setWriteFile(final String writeFile) {
        this.writeFile = writeFile;
    }
    public int getWritePosition() {
        return writePosition;
    }
    public void setWritePosition(final int writePosition) {
        this.writePosition = writePosition;
    }
}
