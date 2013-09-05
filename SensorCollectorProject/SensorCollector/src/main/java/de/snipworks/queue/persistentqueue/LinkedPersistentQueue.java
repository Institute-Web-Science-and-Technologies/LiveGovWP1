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

import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;


/**
 * Wrapper class to encapsulate persistence queue to standard inerface of queue
 * @author Mario Hahnemann
 *
 * @param <QueueType> the type of elements stored in queue
 */
public class LinkedPersistentQueue < QueueType extends Serializable >
        implements Queue < QueueType > {
    
    // the persience queue to wrap
    private PersistenceQueue < QueueType > queue;

    
    /**
     * Constrcutor for the class, the defragmentation intervall is 50 by default
     * @param fileName the name of file where elements there stored
     * @throws java.io.IOException if file not writeable
     */
    LinkedPersistentQueue(final String fileName) throws IOException {
        queue = new PersistenceQueue < QueueType > (fileName);
    }

    /**
     * Constrcutor for the class
     * @param fileName the name of file where elements there stored
     * @param filesize number of entries per queue file
     * @throws java.io.IOException if file not writeable
     */
    LinkedPersistentQueue(final String fileName, final int filesize)
            throws IOException {
        queue = new PersistenceQueue < QueueType > (fileName, filesize);
    }

    /**
     * {@inheritDoc}
     **/
    public QueueType element() {
        QueueType element = this.peek();
        if (element == null) {
            throw new NoSuchElementException();
        }
        return element;
    }

    /**
     * {@inheritDoc}
     **/
    public boolean offer(final QueueType o) {
        Boolean result = true;
        try {
            queue.add(o);
        }
        catch (IOException ex) {
            result = false;
        }
        return result;
    }
    
    /**
     * {@inheritDoc}
     **/
    public QueueType peek() {
        return queue.peek();
    }
    
    /**
     * {@inheritDoc}
     **/
    public QueueType poll() {
        QueueType element = null;
        try {
            element = queue.remove();
        }
        catch (IOException e) {
            element = null;
        }
        return element;
    }
    
    /**
     * {@inheritDoc}
     **/
    public QueueType remove() {
        QueueType element = this.poll();
        if (element == null) {
            throw new NoSuchElementException();
        }
        return element;

    }
    
    /**
     * This method is not supported
     * {@inheritDoc}
     **/
    public boolean add(final QueueType o) {
        throw new UnsupportedOperationException();
    }

    /**
     * This method is not supported
     * {@inheritDoc}
     **/
    public boolean addAll(final Collection < ? extends QueueType > c) {
        //TODO do not know if it would better support the method, check recommented 
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     **/
    public void clear() {
        try {
            queue.clear();
        }
        catch (IOException e) {
            // Do nothing because of interface definition 
        }
    }

    /**
     * This method is not supported
     * {@inheritDoc}
     **/
    public boolean contains(final Object o) {
        throw new UnsupportedOperationException();
    }

    /**
     * This method is not supported
     * {@inheritDoc}
     **/
    public boolean containsAll(final Collection < ? > c) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     **/
    public boolean isEmpty() {
        return queue.isEmpty();
    }

    /**
     * This method is not supported
     * {@inheritDoc}
     **/
    public Iterator < QueueType > iterator() {
        throw new UnsupportedOperationException();
    }

    /**
     * This method is not supported
     * {@inheritDoc}
     **/
    public boolean remove(final Object o) {
        throw new UnsupportedOperationException();
    }

    /**
     * This method is not supported
     * {@inheritDoc}
     **/
    public boolean removeAll(final Collection < ? > c) {
        throw new UnsupportedOperationException();
    }

    /**
     * This method is not supported
     * {@inheritDoc}
     **/
    public boolean retainAll(final Collection < ? > c) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     **/
    public int size() {
        return queue.size();
    }

    /**
     * This method is not supported
     * {@inheritDoc}
     **/
    public Object[] toArray() {
        throw new UnsupportedOperationException();
    }
    
    /**
     * This method is not supported
     * {@inheritDoc}
     **/
    public < T > T[] toArray(final T[] a) {
        throw new UnsupportedOperationException();
    }
    
    
    
    
    
    
    
}
