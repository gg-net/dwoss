package eu.ggnet.dwoss.util.persistence.entity;

public class EntityUtil {

    public static boolean equals(Identifiable one, Object two) {
        if ( one == null ) throw new NullPointerException("This Util expects the first element to be not null");
        if ( two == null ) return false;
        if ( one.getClass() != two.getClass() ) return false;
        final Identifiable other = (Identifiable)two;
        if ( one.getId() == 0 && other.getId() == 0 ) return one == other;
        return one.getId() == other.getId();
    }

    public static int hashCode(Identifiable one,int seed) {
        if ( one == null ) throw new NullPointerException("This Util expects the first element to be not null");
        if ( one.getId() == 0 ) throw new IllegalArgumentException("If the Identifieable is not persisted, it must call super.hashCode by himself");
        return seed * 7 + (int)(one.getId() ^ (one.getId() >>> 32));
    }
}
