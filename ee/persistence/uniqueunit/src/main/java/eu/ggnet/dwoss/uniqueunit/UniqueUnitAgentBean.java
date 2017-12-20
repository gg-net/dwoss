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
package eu.ggnet.dwoss.uniqueunit;

import java.util.*;
import java.util.Map.Entry;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.uniqueunit.api.PicoProduct;
import eu.ggnet.dwoss.uniqueunit.assist.CategoryProductDto;
import eu.ggnet.dwoss.uniqueunit.assist.UniqueUnits;
import eu.ggnet.dwoss.uniqueunit.eao.ProductEao;
import eu.ggnet.dwoss.uniqueunit.eao.UniqueUnitEao;
import eu.ggnet.dwoss.uniqueunit.entity.*;

import eu.ggnet.dwoss.util.persistence.AbstractAgentBean;
import eu.ggnet.saft.api.Reply;

/**
 * The Implementation of the UniqueUnitAgent
 * <p/>
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

    @Override
    public CategoryProduct createOrUpdate(CategoryProductDto dto, String username) {
        Objects.requireNonNull(dto, "DTO is null, not allowed");
        L.info("Trying to store category product from DTO: {}", dto);
        CategoryProduct cp;
        if ( dto.getId() == 0 ) {
            cp = new CategoryProduct();
            L.info("Creating new CategoryProduct");
        } else {
            cp = findById(CategoryProduct.class, dto.getId());
            L.info("updating existing CategoryProduct");
        }
        cp.setName(dto.getName());
        cp.setDescription(dto.getDescription());
        if ( dto.getSalesChannel() != null ) cp.setSalesChannel(dto.getSalesChannel());

        for (Product p : new ArrayList<>(cp.getProducts())) {
            p.setCategoryProduct(null);
            cp.remove(p);
        }

        for (PicoProduct pp : dto.getProducts()) {
            cp.add(findById(Product.class, pp.getId()));
            L.info("added Product: {} to CategoryProduct: {}", findById(Product.class, pp.getId()), cp);
        }
        for (Entry<PriceType, Double> price : dto.getPrices().entrySet()) {
            cp.setPrice(price.getKey(), price.getValue(), "Price changed by " + username);
        }
        if ( dto.getId() == 0 ) em.persist(cp);
        return cp;
    }

    @Override
    public Reply<Void> deleteCategoryProduct(long id) {
        CategoryProduct cp = em.find(CategoryProduct.class, id);
        if ( cp == null ) return Reply.failure("No Instance of CategoryProduct with id " + id + " found");
        for (Product p : new ArrayList<>(cp.getProducts())) {
            cp.remove(p);
        }
        em.remove(cp);
        return Reply.success(null);
    }
}
