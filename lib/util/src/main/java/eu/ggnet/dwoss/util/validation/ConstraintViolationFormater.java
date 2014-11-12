/* 
 * Copyright (C) 2014 GG-Net GmbH - Oliver Günther
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
package eu.ggnet.dwoss.util.validation;

import java.util.Collection;
import java.util.Iterator;

import javax.validation.ConstraintViolation;

/**
 * Some default formats for ConstraintViolations.
 * <p/>
 * @author oliver.guenther
 */
public class ConstraintViolationFormater {

    /**
     * Return a single line string of all ConstraintViolations.
     * <p/>
     * @param violations the violations.
     * @return a single line string of all ConstraintViolations.
     */
    public static <T> String toSingleLine(final Collection<ConstraintViolation<T>> violations) {
        StringBuilder sb = new StringBuilder("ConstraintViolations[");
        for (Iterator<ConstraintViolation<T>> it = violations.iterator(); it.hasNext();) {
            ConstraintViolation cv = it.next();
            sb.append("{message=").append(cv.getMessage()).append(", root=").append(cv.getRootBeanClass()).append(", property=").append(cv.getPropertyPath());
            sb.append(", invalidValue=").append(cv.getInvalidValue()).append("}");
            if ( it.hasNext() ) sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * Returns a Multiline Format.
     * <p/>
     * @param violations the violations to format.
     * @param showRoot   if true the root will be added.
     * @return a Multiline Format.
     */
    public static String toMultiLine(final Collection<ConstraintViolation<?>> violations, boolean showRoot) {
        StringBuilder sb = new StringBuilder("ConstraintViolations:");
        for (Iterator<ConstraintViolation<?>> it = violations.iterator(); it.hasNext();) {
            ConstraintViolation cv = it.next();
            sb.append("\nViolation")
                    .append("\n -Message: ").append(cv.getMessage())
                    .append("\n -Invalid Value: ").append(cv.getInvalidValue())
                    .append("\n -Class: ").append(cv.getRootBeanClass())
                    .append("\n -Property: ").append(cv.getPropertyPath());
            if ( showRoot ) sb.append("\n -").append(cv.getRootBean() == null ? "null" : cv.getRootBean().toString());
        }
        return sb.toString();
    }
}
