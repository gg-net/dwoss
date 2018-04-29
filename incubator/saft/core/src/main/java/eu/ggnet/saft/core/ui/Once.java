package eu.ggnet.saft.core.ui;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotate Ui Class to indicate, that only one window of this class must be open at any time.
 *
 * @author oliver.guenther
 */
@Documented
@Retention(RUNTIME)
@Target(TYPE)
public @interface Once {

    /**
     * Returns and sets the once mode
     *
     * @return the once mode
     */
    boolean value() default true;
}
