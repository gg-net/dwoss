package eu.ggnet.dwoss.redtape.assist;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

import static java.lang.annotation.ElementType.*;

/**
 * UniqueUnit Persistence Unit Injection Point
 */
@Qualifier
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({FIELD, METHOD,PARAMETER,TYPE})
public @interface RedTapes {}
