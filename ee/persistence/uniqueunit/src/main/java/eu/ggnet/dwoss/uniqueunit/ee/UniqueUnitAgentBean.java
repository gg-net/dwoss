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
package eu.ggnet.dwoss.uniqueunit.ee;


import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.uniqueunit.ee.assist.UniqueUnits;
import eu.ggnet.dwoss.uniqueunit.ee.eao.ProductEao;
import eu.ggnet.dwoss.uniqueunit.ee.eao.UniqueUnitEao;
import eu.ggnet.dwoss.uniqueunit.ee.entity.Product;
import eu.ggnet.dwoss.uniqueunit.ee.entity.UniqueUnit;
import eu.ggnet.dwoss.core.system.persistence.AbstractAgentBean;

/**
 * The Implementation of the UniqueUnitAgent
 * <p>
 * @author oliver.guenther
 */
@Stateless
public class UniqueUnitAgentBean extends AbstractAgentBean implements UniqueUnitAgent {

    private final Logger L = LoggerFactory.getLogger(UniqueUnitAgentBean.class);

    @Inject
    @UniqueUnits
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    /**
     * Finds a Product with the partNo.
     * <p/>
     * @param partNo the partNo as search
     * @return the found product or null.
     */
    @Override
    public Product findProductByPartNo(String partNo) {
        return new ProductEao(em).findByPartNo(partNo);
    }

    /**
     * Finds a Product with the partNo, eager loading all resources.
     * <p/>
     * @param partNo the partNo as search
     * @return the found product or null.
     */
    @Override
    public Product findProductByPartNoEager(String partNo) {
        return optionalFetchEager(new ProductEao(em).findByPartNo(partNo));
    }

    /**
     * Finds a UniqueUnit by the Identifier.
     * <p/>
     * @param type       the identifierType
     * @param identifier the identifier
     * @return the uniqueUnit or null.
     */
    @Override
    public UniqueUnit findUnitByIdentifierEager(UniqueUnit.Identifier type, String identifier) {
        return optionalFetchEager(new UniqueUnitEao(em).findByIdentifier(type, identifier));
    }
    
}
