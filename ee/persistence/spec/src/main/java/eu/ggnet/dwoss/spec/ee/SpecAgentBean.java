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
package eu.ggnet.dwoss.spec.ee;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import eu.ggnet.dwoss.spec.ee.assist.Specs;
import eu.ggnet.dwoss.spec.ee.eao.ProductSpecEao;
import eu.ggnet.dwoss.spec.ee.entity.ProductSpec;
import eu.ggnet.dwoss.util.persistence.AbstractAgentBean;

/**
 * The Implementation of the SpecAgent
 * <p>
 * @author oliver.guenther
 */
@Stateless
public class SpecAgentBean extends AbstractAgentBean implements SpecAgent {

    @Inject
    @Specs
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    /**
     * Finds a ProductSpec by part no and fetches the object tree eager.
     * <p>
     * @param partNo the part no to search for
     * @return a ProductSpec or null if non found.
     */
    @Override
    public ProductSpec findProductSpecByPartNoEager(String partNo) {
        if ( partNo == null ) return null;
        return optionalFetchEager(new ProductSpecEao(em).findByPartNo(partNo));
    }
}
