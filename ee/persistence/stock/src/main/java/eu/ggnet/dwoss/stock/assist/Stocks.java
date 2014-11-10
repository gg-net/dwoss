package eu.ggnet.dwoss.stock.assist;

import java.lang.annotation.*;

import javax.inject.Qualifier;

/**
 * Stock Persistence Unit Injection Point
 */
@Qualifier
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD,ElementType.PARAMETER,ElementType.TYPE})
public @interface Stocks {}
