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
package eu.ggnet.dwoss.core.system;

import java.util.*;

import javax.swing.JOptionPane;
import javax.validation.*;

/**
 * This is a simple utility class, which helps with validation
 *
 * @author oliver.guenther
 */
public class ValidationUtil {

    private final static Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    /**
     * Validates a Object and throws a Exception if invalid.
     * <p>
     * @param candiate the candidate.
     * @throws ConstraintViolationException
     */
    public static void validate(Object candiate) throws ValidationException {
        if ( candiate == null ) return;
        Set<ConstraintViolation<Object>> violations = validator.validate(candiate);
        if ( violations.isEmpty() ) return;
        throw new ValidationException(formatToMultiLine(new HashSet<ConstraintViolation<?>>(violations), true));
    }

    /**
     * Validates the candiate and shows on violations a Dialog, returns true if valid, else false.
     * <p/>
     * @param parent   an optional parent
     * @param candiate the candidate to validate, may be null.
     * @return true if valid or null, else false.
     */
    public static boolean isValidOrShow(java.awt.Window parent, Object candiate) {
        return isValidOrShow(parent, null, candiate, false);
    }

    /**
     * Validates the candiate and shows on violations a Dialog, returns true if valid, else false.
     * <p/>
     * @param parent   an optional parent
     * @param candiate the candidate to validate, may be null.
     * @param showRoot show the root of the failure.
     * @return true if valid or null, else false.
     */
    public static boolean isValidOrShow(java.awt.Window parent, Object candiate, boolean showRoot) {
        return isValidOrShow(parent, null, candiate, showRoot);
    }

    /**
     * Validates the candiate and shows on violations a Dialog, returns true if valid, else false.
     * <p/>
     * @param parent   an optional parent
     * @param title    an optional title
     * @param candiate the candidate to validate, may be null.
     * @param showRoot
     * @return true if valid or null, else false.
     */
    public static boolean isValidOrShow(java.awt.Window parent, String title, Object candiate, boolean showRoot) {
        if ( candiate == null ) return true;
        Set<ConstraintViolation<?>> violations = new HashSet<ConstraintViolation<?>>(validator.validate(candiate));
        if ( violations.isEmpty() ) return true;
        JOptionPane.showMessageDialog(parent,
                formatToMultiLine(violations, showRoot),
                title == null ? "Fehler gefunden" : title, JOptionPane.ERROR_MESSAGE);
        return false;
    }

    /**
     * Return a single line string of all ConstraintViolations.
     * <p/>
     * @param violations the violations.
     * @return a single line string of all ConstraintViolations.
     */
    public static <T> String formatToSingleLine(final Collection<ConstraintViolation<T>> violations) {
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
    public static String formatToMultiLine(final Collection<ConstraintViolation<?>> violations, boolean showRoot) {
        StringBuilder sb = new StringBuilder("ConstraintViolations:");
        for (Iterator<ConstraintViolation<?>> it = violations.iterator(); it.hasNext();) {
            ConstraintViolation cv = it.next();
            sb.append("\nViolation").append("\n -Message: ").append(cv.getMessage()).append("\n -Invalid Value: ").append(cv.getInvalidValue()).append("\n -Class: ").append(cv.getRootBeanClass()).append("\n -Property: ").append(cv.getPropertyPath());
            if ( showRoot ) sb.append("\n -").append(cv.getRootBean() == null ? "null" : cv.getRootBean().toString());
        }
        return sb.toString();
    }
}
