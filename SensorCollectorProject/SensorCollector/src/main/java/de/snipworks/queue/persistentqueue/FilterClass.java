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
import java.io.FilenameFilter;

/**
 *
 * @author Mario Hahnemann
 * Filter class to sort aut only files for specific queue
 */
public class FilterClass implements FilenameFilter {
    private String base = null;

    public FilterClass(final String baseName) {
        base = baseName;
    }

    public boolean accept(final File dir, final String name) {

        return name.toLowerCase().startsWith(base);
    }
}