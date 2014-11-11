/* 
 * Copyright (C) 2014 pascal.perau
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
package eu.ggnet.dwoss.util;

/**
 * Utility class for math operations
 *
 * @author oliver.guenther
 */
public class MathUtil {

    /**
     * Applies the value with percent and rounds the result if its in space of the delta.
     * <p>The value and percent are applied as follows: value * (1+percent).
     * If abs(round(result) - result) &lt; delta return round(result) else result
     * </p>
     * <p/>
     * @param value   the value to apply everything
     * @param percent the percentage to be applied
     * @param delta   the delta for the last rounding process
     * @return the correct or rounded result.
     */
    public static double roundedApply(double value, double percent, double delta) {
        double correct = Math.round(value * (1 + percent) * 100.0) / 100.0;
        double rounded = Math.round(correct);
        if ( Math.abs(rounded - correct) < delta ) return rounded;
        return correct;
    }

    /**
     * Returns true if two doubles are considered equal. Tests if the absolute
     * difference between two doubles has a difference less then .0000001. This
     * should be fine when comparing prices, because prices have a precision of
     * .001.
     *
     * @param d1 double to compare.
     * @param d2 double to compare.
     * @return true true if two doubles are considered equal.
     */
    public static boolean equals(Double d1, Double d2) {
        if ( d1 == d2 ) return true;
        if ( d1 == null || d2 == null ) return false;
        return Math.abs(d1 - d2) < 0.0000001;
    }
}
