package eu.ggnet.dwoss.spec.eao;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import eu.ggnet.dwoss.util.persistence.eao.AbstractEao;
import eu.ggnet.dwoss.spec.entity.piece.Gpu;

/**
 * Entity Access Object for the CPU.
 *
 * @author oliver.guenther
 */
public class GpuEao extends AbstractEao<Gpu> {

    private EntityManager em;

    public GpuEao(EntityManager em) {
        super(Gpu.class);
        this.em = em;
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public Gpu find(Gpu.Series series, String model) {
        try {
            return em.createNamedQuery("Gpu.bySeriesModel", Gpu.class).setParameter(1, series).setParameter(2, model).getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }
}
