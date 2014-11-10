package eu.ggnet.dwoss.customer.assist;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;

import javax.inject.Qualifier;

/**
 * Customer Persistence Unit Injection Point
 */
@Qualifier
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({FIELD, METHOD, PARAMETER, TYPE})
public @interface Customers {
}
