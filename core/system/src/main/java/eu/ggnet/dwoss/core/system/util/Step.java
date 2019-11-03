/*
 * Copyright (C) 2014 GG-Net GmbH
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.ggnet.dwoss.core.system.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;

/**
 * Represents a Stepsize, normally for a Allgorithm of Dates.
 */
public enum Step {
    DAY {
        @Override
        public Date truncate(Date date) {
            return DateUtils.truncate(date, Calendar.DATE);
        }

        @Override
        public Date prepareEnd(Date date) {
            return DateUtils.addDays(DateUtils.truncate(date, Calendar.DATE), 1);
        }

        @Override
        public Date incement(Date date) {
            return DateUtils.addDays(date, 1);
        }

        @Override
        public String format(Date date) {
            return new SimpleDateFormat("yyyy-MM-dd").format(date);
        }
    }, WEEK {
        @Override
        public Date truncate(Date date) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            while (cal.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
                cal.add(Calendar.DATE, -1);
            }
            return DateUtils.truncate(cal.getTime(), Calendar.DATE);
        }

        @Override
        public Date prepareEnd(Date date) {
            return DateUtils.addDays(truncate(date), 1);
        }

        @Override
        public Date incement(Date date) {
            return DateUtils.addWeeks(date, 1);
        }

        @Override
        public String format(Date date) {
            return new SimpleDateFormat("yyyy'KW'ww").format(date);
        }
    }, MONTH {
        @Override
        public Date truncate(Date date) {
            return DateUtils.truncate(date, Calendar.MONTH);
        }

        @Override
        public Date prepareEnd(Date date) {
            return DateUtils.addDays(DateUtils.truncate(date, Calendar.MONTH), 1);
        }

        @Override
        public Date incement(Date actual) {
            return DateUtils.addMonths(actual, 1);
        }

        @Override
        public String format(Date date) {
            return new SimpleDateFormat("MMMM").format(date);
        }
    };

    /**
     * Prepares the start by truncating it to the selected Level.
     * <p>
     * @param start the start to be prepared.
     * @return the prepared start.
     */
    public abstract Date truncate(Date start);

    /**
     * Prepares the end by creating a slightly past element.
     * This allows the usage of something.before(preparedEnd) to inclued the end.
     * <p>
     * @param end the end to be prepared
     * @return the prepared end
     */
    public abstract Date prepareEnd(Date end);

    /**
     * Incements the date by the selected type.
     * <p>
     * @param date the date to be incremented
     * @return the incremented date
     */
    public abstract Date incement(Date date);

    /**
     * Format the date in a readable version of the step size.
     * e.g. 2014-01-01, step = Month, something like January make sence. step = Week, something like 2014KW01
     * <p>
     * @param date the date to formate
     * @return a useful string representation.
     */
    public abstract String format(Date date);

}
