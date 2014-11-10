package eu.ggnet.dwoss.util.persistence.eao;

import javax.persistence.EntityManager;

public class DefaultEao<T> extends AbstractEao<T> {

    private EntityManager em;

    public DefaultEao(Class<T> entityClass, EntityManager em) {
        super(entityClass);
        this.em = em;
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }
}
