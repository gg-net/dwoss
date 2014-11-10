package eu.ggnet.dwoss.spec.eao;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import eu.ggnet.dwoss.util.persistence.eao.AbstractEao;
import eu.ggnet.dwoss.spec.entity.piece.Cpu;

/**
 * Entity Access Object for the CPU.
 *
 * @author oliver.guenther
 */
public class CpuEao extends AbstractEao<Cpu> {

    private EntityManager em;

    public CpuEao(EntityManager em) {
        super(Cpu.class);
        this.em = em;
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public Cpu find(Cpu.Series series, String model) {
        try {
            return em.createNamedQuery("Cpu.bySeriesModel", Cpu.class).setParameter(1, series).setParameter(2, model).getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }
}
