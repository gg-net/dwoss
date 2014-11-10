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
