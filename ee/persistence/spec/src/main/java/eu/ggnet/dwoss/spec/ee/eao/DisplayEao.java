/* 
 * Copyright (C) 2014 GG-Net GmbH - Oliver Günther
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.ggnet.dwoss.spec.ee.eao;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import eu.ggnet.dwoss.core.system.persistence.AbstractEao;
import eu.ggnet.dwoss.spec.ee.entity.piece.Display;

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
