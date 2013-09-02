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

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;


/**
 * Helper class to generate timestamp
 * @author Mario Hahnemann
*/
public final class ActualTimestamp {

    /**
     * Imposible to generate Instance from that
     */
    private ActualTimestamp() {
    }

    /**
     * Returns timestamp for actual time as yyyymmddhh24missmmm.
     * @return the formatted time
     */
    public static String getTimeStamp() {
            long current = System.currentTimeMillis();
            final GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTimeInMillis(current);
            final StringBuffer output = new StringBuffer(100);
            final DecimalFormat dfZeroZero = new DecimalFormat("00");
            final DecimalFormat dfZeroZeroZero = new DecimalFormat("000");
            output.append(dfZeroZero.format(calendar.get(Calendar.YEAR)));
            output.append(dfZeroZero.format(calendar.get(Calendar.MONTH) + 1));
            output.append(dfZeroZero.format(calendar.get(Calendar.DAY_OF_MONTH)));
            output.append(dfZeroZero.format(calendar.get(Calendar.HOUR_OF_DAY)));
            output.append(dfZeroZero.format(calendar.get(Calendar.MINUTE)));
            output.append(dfZeroZero.format(calendar.get(Calendar.SECOND)));
            output.append(dfZeroZeroZero.format(calendar.get(Calendar.MILLISECOND)));
            //TODO for fast processors we'll need more precision
            return output.toString();
        }
}
