package eu.ggnet.dwoss.spec.assist;

import java.lang.annotation.*;

import javax.inject.Qualifier;

import static java.lang.annotation.ElementType.*;

/**
 * UniqueUnit Persistence Unit Injection Point
 */
@Qualifier
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({FIELD, METHOD,PARAMETER,TYPE})
public @interface Specs {}
