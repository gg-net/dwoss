package eu.ggnet.dwoss.core.widget.ops;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotate a dependent Action, it will indicated as the default one.
 *
 * @author oliver.guenther
 */
@Documented
@Retention(RUNTIME)
@Target(TYPE)
public @interface DefaultAction {

}
