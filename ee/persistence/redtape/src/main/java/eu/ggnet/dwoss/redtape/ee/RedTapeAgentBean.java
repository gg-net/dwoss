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
package eu.ggnet.dwoss.redtape.ee;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import eu.ggnet.dwoss.redtape.ee.assist.RedTapes;
import eu.ggnet.dwoss.redtape.ee.eao.DossierEao;
import eu.ggnet.dwoss.redtape.ee.eao.SalesProductEao;
import eu.ggnet.dwoss.redtape.ee.entity.Dossier;
import eu.ggnet.dwoss.redtape.ee.entity.SalesProduct;

import eu.ggnet.dwoss.core.system.persistence.AbstractAgentBean;

/**
 * The RedTapeAgent implementation.
 * 
 * @author oliver.guenther
 */
@Stateless
public class RedTapeAgentBean extends AbstractAgentBean implements RedTapeAgent {
    
    private final static Logger L = LoggerFactory.getLogger(RedTapeAgentBean.class);

    @Inject
    @RedTapes
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public List<Dossier> findAllEagerDescending(int start, int end) {
        return optionalFetchEager(new DossierEao(em).findAllDescending(start, end));
    }

    /**
     * Get the dossiers where the customerId and the directive matches.
     * This method will Fetch/Eager every entity.
     * <p/>
     * @param customerId the cusotmer id
     * @param start      limites the returned list, this is the start
     * @param amount     limites the returned list, this is the amount of elements to show
     * @return the dossiers where the customerId and the directive matches
     */
    @Override
    public List<Dossier> findDossiersClosedByCustomerIdEager(long customerId, int start, int amount) {
        return optionalFetchEager(new DossierEao(em).findClosedByCustomerId(customerId, start, amount));
    }

    /**
     * Get the dossiers where the customerId matches without a specific directive
     * This method will Fetch/Eager every entity.
     * <p/>
     * @param customerId the customer id
     * @return the dossiers where the customerId matches and the directive does not match
     */
    @Override
    public List<Dossier> findDossiersOpenByCustomerIdEager(long customerId) {
        List<Dossier> dossiers = optionalFetchEager(new DossierEao(em).findOpenByCustomerId(customerId));
        L.info("found dossiers.size={} for customer.id={}", dossiers.size(), customerId);
        return dossiers;
    }

    /**
     * Removes the instance from the Database.
     * <p/>
     * @param salesProduct the instance to be removed.
     */
    @Override
    public void remove(SalesProduct salesProduct) {
        SalesProduct findById = new SalesProductEao(getEntityManager()).findById(salesProduct.getPartNo());
        getEntityManager().remove(findById);
    }

    /**
     * Update (Merge) a supplied instance.
     * <p/>
     * @param salesProduct the instance to merge.
     * @return the merged instance.
     */
    @Override
    public SalesProduct merge(SalesProduct salesProduct) {
        return getEntityManager().merge(salesProduct);
    }

    @Override
    public SalesProduct persist(SalesProduct salesProduct) {
        getEntityManager().persist(salesProduct);
        return salesProduct;
    }
}
