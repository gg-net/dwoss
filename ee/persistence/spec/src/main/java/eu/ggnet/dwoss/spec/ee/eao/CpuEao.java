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

import eu.ggnet.dwoss.util.persistence.eao.AbstractEao;
import eu.ggnet.dwoss.spec.ee.entity.piece.Cpu;

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
