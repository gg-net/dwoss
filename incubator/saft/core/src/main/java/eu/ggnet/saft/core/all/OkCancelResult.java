package eu.ggnet.saft.core.all;

/**
 *
 * @author oliver.guenther
 */
public class OkCancelResult<V> {

    public final V value;
    public final boolean ok;

    public OkCancelResult(V value, boolean ok) {
        this.value = value;
        this.ok = ok;
    }

}
