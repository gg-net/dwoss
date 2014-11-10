package eu.ggnet.dwoss.common.log;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.interceptor.InterceptorBinding;

import static java.lang.annotation.ElementType.*;

/**
 * Enables the Logger of enter and exit of a method.
 */
@InterceptorBinding
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({METHOD, TYPE})
public @interface AutoLogger {
}
