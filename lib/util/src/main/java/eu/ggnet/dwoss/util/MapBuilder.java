package eu.ggnet.dwoss.util;

import java.util.*;

/**
 *
 * @author oliver.guenther
 */
public class MapBuilder<K, V> {

    private final HashMap<K, V> result = new HashMap<>();

    public MapBuilder<K, V> put(K k, V v) {
        result.put(k, v);
        return this;
    }

    public TreeMap toTreeMap() {
        return new TreeMap(result);
    }

    public HashMap toHashMap() {
        return result;
    }
}
