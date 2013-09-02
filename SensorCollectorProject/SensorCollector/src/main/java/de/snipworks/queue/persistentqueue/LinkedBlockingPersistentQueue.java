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
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 
 * This is one queue Implementation with java.util.queue Interface
 * Please note that Collection and Iteration is not well supported.<br>
 * Implementation of BlockingQueue for {@link de.snipworks.queue.persistentqueue.LinkedPersistentQueue}
 * 
 * @author Mario Hahnemann
 * @param <QueueType> the type of elements stored in queue
 */
public class LinkedBlockingPersistentQueue<QueueType extends Serializable>
        extends LinkedPersistentQueue<QueueType> implements BlockingQueue<QueueType> {
    private ReentrantLock lock = new ReentrantLock();
    private Condition queueFilled = lock.newCondition();
    private Condition queueNotFull = lock.newCondition();

    /**
     * Constructor for class
     * @see de.snipworks.queue.persistentqueue.LinkedPersistentQueue#LinkedPersistentQueue(String)
     * @throws java.io.IOException in case of file access trouble
     */
    public LinkedBlockingPersistentQueue(final String fileName) throws IOException {
        super(fileName);
    }

    /**
     * Constructor for class
     * @see de.snipworks.queue.persistentqueue.LinkedPersistentQueue#LinkedPersistentQueue(String)
     * @throws java.io.IOException in case of file access trouble
     */
    public LinkedBlockingPersistentQueue(final String fileName,
            final int filesize) throws IOException {
        super(fileName, filesize);
    }

    /**
     * This method is not supported because of risk of out of memory exeption
     * {@inheritDoc}
     */
    public int drainTo(final Collection<? super QueueType> c) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public int drainTo(final Collection<? super QueueType> c, final int maxElements) {
        if (c == null) {
            throw new NullPointerException();
        }
        int elementCount = 0;

        lock.lock();
        try {
            while (elementCount < maxElements) {
                QueueType element = super.poll();
                if (element != null) {
                    c.add(element);
                    elementCount++;
                } else {
                    break;
                }
            }
            // give signal if elments removed. With that new free space is available
            if (elementCount > 0) {
                queueNotFull.signal();
            }
        } finally {
            lock.unlock();
        }
        return elementCount;
    }

    /**
     *  {@inheritDoc}
     */
    @Override
    public boolean offer(final QueueType o) {
        boolean result = true;
        lock.lock();
        try {
            result = super.offer(o);
            if (result) {
                // set signal because new element in queue
                queueFilled.signal();
            }
        } finally {
            lock.unlock();
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public boolean offer(final QueueType o, final long timeout, final TimeUnit unit) throws InterruptedException {
        boolean result = true;
        lock.lock();
        try {
            result = super.offer(o);
            // try it again with wait for new elements
            if (!result) {
                // wait for new space in queue
                queueNotFull.await(timeout, unit);
                /* don't check the result of await. Because it is possible to add elements
                 * without sending signals. Just try it again.
                 */
                result = super.offer(o);
            }
            // set signal because new element in queue
            if (result) {
                queueFilled.signal();
            }
        /** don't catch the InterruptedException this must be thrown by method
         *  how it is defined in interfac specification
         */
        } finally {
            lock.unlock();
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public QueueType poll(final long timeout, final TimeUnit unit) throws InterruptedException {
        QueueType result = null;
        lock.lock();
        try {
            result = super.poll();
            if (result == null) {
                // wait until new element comes into queue or timeout happens
                queueFilled.await(timeout, unit);
                /* don't check the result of await. Because it is possible to add elements
                 * without sending signals. Just try it again.
                 */
                result = super.poll();
            }
            if (result != null) {
                // got new element, new space in queue available
                queueNotFull.signal();
            }
        /* don't catch the InterruptedException this must be thrown by method
         *  how it is defined in interfac specification
         */

        } finally {
            lock.unlock();
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public void put(final QueueType o) throws InterruptedException {
        boolean result = true;
        lock.lock();
        try {
            result = super.offer(o);
            if (!result) {
                /* there are 2 ways to pass the wait
                 * <li> InterruptedException will be send to method caller </li>
                 * <li> signal were received, this will happen for one waiting thread only </li>
                 */
                queueNotFull.await();
                // try it again, but only once
                result = super.offer(o);
            }
            if (result) {
                // set signal because new element in queue
                queueFilled.signal();
            } else {
                // throws also this Exception if disk full 
                throw new InterruptedException();
            }

        } finally {
            lock.unlock();
        }
    }

    /**
     * This method is not supported because the queue size is limited by disk
     * free space
     * {@inheritDoc}
     */
    public int remainingCapacity() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public QueueType take() throws InterruptedException {
        QueueType result = null;
        lock.lock();
        try {
            result = super.poll();
            if (result == null) {
                /* wait until new element comes into queue or timeout happens
                 * there are 2 ways to pass the wait
                 * <li> InterruptedException will be send to method caller</li>
                 * <li> signal were received, this will happen for one waiting thread only</li>
                 */
                queueFilled.await();
                // try it again, but only once
                result = super.poll();
            }
            if (result != null) {
                queueNotFull.signal();
            }
        } finally {
            lock.unlock();
        }
        return result;
    }
}
