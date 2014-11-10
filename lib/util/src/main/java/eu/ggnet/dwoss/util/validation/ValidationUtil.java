package eu.ggnet.dwoss.util.validation;

import java.util.HashSet;
import java.util.Set;

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
        throw new ValidationException(ConstraintViolationFormater.toMultiLine(new HashSet<>(violations), true));
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
     * @return true if valid or null, else false.
     */
    public static boolean isValidOrShow(java.awt.Window parent, String title, Object candiate, boolean showRoot) {
        if ( candiate == null ) return true;
        Set<ConstraintViolation<?>> violations = new HashSet<ConstraintViolation<?>>(validator.validate(candiate));
        if ( violations.isEmpty() ) return true;
        JOptionPane.showMessageDialog(parent,
                ConstraintViolationFormater.toMultiLine(violations, showRoot),
                title == null ? "Fehler gefunden" : title, JOptionPane.ERROR_MESSAGE);
        return false;
    }
}
