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
 *
 * @author Mario Hahnemann
 * The definition of the last entry in each queue file if it is full.
 */
class FootEntry implements Serializable {

    private static final long serialVersionUID = 1L;
    private String nextFile = null;
    public String getNextFile() {
        return nextFile;
    }
    public void setNextFile(final String nextFile) {
        this.nextFile = nextFile;
    }
}
