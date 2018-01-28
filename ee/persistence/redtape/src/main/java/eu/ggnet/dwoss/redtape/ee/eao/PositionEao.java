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
package eu.ggnet.dwoss.redtape.ee.eao;

import java.util.List;

import javax.persistence.EntityManager;

import eu.ggnet.dwoss.redtape.ee.entity.Position;
import eu.ggnet.dwoss.util.persistence.eao.AbstractEao;

/**
 *
 * @author oliver.guenther
 */
public class PositionEao extends AbstractEao<Position> {

    private final EntityManager em;

    public PositionEao(EntityManager em) {
        super(Position.class);
        this.em = em;
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public List<Position> findByDocumentId(long documentId) {
        return em.createNamedQuery("Position.findByDocumentId", Position.class).setParameter(1, documentId).getResultList();
    }

    public long countByDocumentId(long documentId) {
        return em.createNamedQuery("Position.countByDocumentId", Long.class).setParameter(1, documentId).getSingleResult();
    }

    public List<Position> findByUniqueUnitId(int unitId) {
        return em.createNamedQuery("Position.findByUniqueUnitId", Position.class).setParameter(1, unitId).getResultList();
    }
}
