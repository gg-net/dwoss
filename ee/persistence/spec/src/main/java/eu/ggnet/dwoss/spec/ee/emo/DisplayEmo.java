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
package eu.ggnet.dwoss.spec.ee.emo;

import jakarta.persistence.EntityManager;

import eu.ggnet.dwoss.core.system.persistence.AbstractEao;
import eu.ggnet.dwoss.spec.ee.eao.DisplayEao;
import eu.ggnet.dwoss.spec.ee.entity.piece.Display;

import static java.util.Objects.requireNonNull;

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
     * @param size       the size must not be null
     * @param resolution the resolution must not be null
     * @param type       the type must not be null
     * @param ration     the ration must not be null
     * @return a Display by its components, or an unpersisted entity.
     * @throws NullPointerException if any parameter is null.
     */
    public Display weakRequest(Display.Size size, Display.Resolution resolution, Display.Type type, Display.Ration ration) throws NullPointerException {       
        Display display = new DisplayEao(em).find(
                requireNonNull(size, "size must not be null"), 
                requireNonNull(resolution, "resolution must not be null"),
                requireNonNull(type,"type must not be null"),
                requireNonNull(ration, "ration must not be null"));
        if (display == null) {
            display = new Display(size, resolution, type, ration);
        }
        return display;
    }
}
