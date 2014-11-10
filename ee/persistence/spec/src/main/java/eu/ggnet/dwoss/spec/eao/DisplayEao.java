package eu.ggnet.dwoss.spec.eao;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import eu.ggnet.dwoss.util.persistence.eao.AbstractEao;
import eu.ggnet.dwoss.spec.entity.piece.Display;

/**
 * Entity Access Object for the CPU.
 *
 * @author oliver.guenther
 */
public class DisplayEao extends AbstractEao<Display> {

    private EntityManager em;

    public DisplayEao(EntityManager em) {
        super(Display.class);
        this.em = em;
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    /**
     * Returns a Display by its components.
     * If no Display is found, null is returned
     *
     * @param size       the size
     * @param resolution the resolution
     * @param type       the type
     * @param ration     the ration
     * @return a Display by its components, or null if not existent
     */
    public Display find(Display.Size size, Display.Resolution resolution, Display.Type type, Display.Ration ration) {
        try {
            return em.createNamedQuery("Display.bySizeResolutionTypeRation", Display.class)
                    .setParameter(1, size).setParameter(2, resolution).setParameter(3, type).setParameter(4, ration).getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }
}
