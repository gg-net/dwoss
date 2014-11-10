package eu.ggnet.dwoss.receipt.product;

import java.util.Comparator;

import eu.ggnet.dwoss.spec.entity.INamed;

/**
 *
 * @author oliver.guenther
 */
public class NamedComparator implements Comparator<INamed> {

    @Override
    public int compare(INamed o1, INamed o2) {
        if ( o1 == o2 ) return 0;
        if ( o1 == null ) return -1;
        if ( o2 == null ) return +1;
        if ( o1.getName() == o2.getName() ) return 0;
        if ( o1.getName() == null ) return -1;
        return o1.getName().compareTo(o2.getName());
    }

}
