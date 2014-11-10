package eu.ggnet.dwoss.util.table;

/**
 *
 * @author oliver.guenther
 */
public interface PojoFilter<T> {

    boolean filter(T t);

}
