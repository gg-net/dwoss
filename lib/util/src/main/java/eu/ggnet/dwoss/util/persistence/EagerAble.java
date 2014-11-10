package eu.ggnet.dwoss.util.persistence;

/**
 * By implementing the interface an entity supplies the contract to load the object tree eager on demand.
 *
 * @author oliver.guenther
 */
public interface EagerAble {

    /**
     * Should be called by any Agent if an Eager Method is used.
     */
    void fetchEager();
}
