package eu.ggnet.dwoss.spec.emo;

import javax.persistence.EntityManager;

import eu.ggnet.dwoss.util.persistence.eao.AbstractEao;
import eu.ggnet.dwoss.spec.eao.DisplayEao;
import eu.ggnet.dwoss.spec.entity.piece.Display;

/**
 * Entity Access Object for the CPU.
 *
 * @author oliver.guenther
 */
public class DisplayEmo extends AbstractEao<Display> {

    private EntityManager em;

    public DisplayEmo(EntityManager em) {
        super(Display.class);
        this.em = em;
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    /**
     * Returns a Display by its components, or an unpersisted entity.
     *
     * @param size       the size
     * @param resolution the resolution
     * @param type       the type
     * @param ration     the ration
     * @return a Display by its components, or an unpersisted entity.
     */
    public Display weakRequest(Display.Size size, Display.Resolution resolution, Display.Type type, Display.Ration ration) {
        Display display = new DisplayEao(em).find(size, resolution, type, ration);
        if (display == null) {
            display = new Display(size, resolution, type, ration);
        }
        return display;
    }
}
