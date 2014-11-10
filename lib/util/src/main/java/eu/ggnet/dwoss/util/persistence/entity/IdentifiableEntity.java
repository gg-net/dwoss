package eu.ggnet.dwoss.util.persistence.entity;

public abstract class IdentifiableEntity implements Identifiable {

    @Override
    public final boolean equals(Object obj) {
        return EntityUtil.equals(this, obj);
    }

    @Override
    public final int hashCode() {
        if (getId() == 0) return super.hashCode();
        return EntityUtil.hashCode(this, this.getClass().hashCode());
    }

}
