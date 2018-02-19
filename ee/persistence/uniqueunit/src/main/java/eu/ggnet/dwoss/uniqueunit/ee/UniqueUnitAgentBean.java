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

import java.util.Map.Entry;
import java.util.Objects;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.uniqueunit.api.PicoUnit;
import eu.ggnet.dwoss.uniqueunit.ee.assist.UniqueUnits;
import eu.ggnet.dwoss.uniqueunit.ee.eao.ProductEao;
import eu.ggnet.dwoss.uniqueunit.ee.eao.UniqueUnitEao;
import eu.ggnet.dwoss.uniqueunit.ee.entity.*;
import eu.ggnet.dwoss.uniqueunit.ee.entity.dto.*;
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

    @Inject
    private CategoryProductMapper categoryProductMapper;

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
        CategoryProduct cp = dto.getId() == 0 ? new CategoryProduct() : findById(CategoryProduct.class, dto.getId());
        Objects.requireNonNull(cp, "No CategoryProduct found for id=" + dto.getId());
        categoryProductMapper.update(cp, dto);
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
        cp.getProducts().clear();
        em.remove(cp);
        return Reply.success(null);
    }

    @Override
    public Reply<Void> addToUnitCollection(PicoUnit punit, long unitCollectionId) {
        if ( punit == null ) return Reply.failure("PicoUnit is null");
        UniqueUnit uu = em.find(UniqueUnit.class, punit.getUniqueUnitId());
        if ( uu == null ) return Reply.failure("No UniqueUnit found for id " + punit.id());
        UnitCollection uc = em.find(UnitCollection.class, unitCollectionId);
        if ( uc == null ) return Reply.failure("No UnitCollection found for id " + unitCollectionId);
        uc.getUnits().add(uu);
        return Reply.success(null);
    }

    @Override
    public Reply<Void> unsetUnitCollection(PicoUnit punit) {
        if ( punit == null ) return Reply.failure("PicoUnit is null");
        UniqueUnit uu = em.find(UniqueUnit.class, punit.getUniqueUnitId());
        if ( uu == null ) return Reply.failure("No UniqueUnit found for id " + punit.id());
        uu.setUnitCollection(null);
        return Reply.success(null);
    }

    @Override
    public Reply<UnitCollection> createOnProduct(long productId, UnitCollectionDto dto, String username) {
        if ( dto == null ) return Reply.failure("UnitCollectionDto is null");
        Product product = em.find(Product.class, productId);
        if ( product == null ) return Reply.failure("Product of id " + productId + " does not exist");
        UnitCollection unitCollection = new UnitCollection();
        UnitCollectionMapper.INSTANCE.update(unitCollection, dto);
        for (Entry<PriceType, Double> price : dto.getPrices().entrySet()) {
            unitCollection.setPrice(price.getKey(), price.getValue(), "Price changed by " + username);
        }
        unitCollection.setProduct(product);
        em.persist(unitCollection);
        return Reply.success(unitCollection);
    }

    @Override
    public Reply<UnitCollection> update(UnitCollectionDto dto, String username) {
        if ( dto == null ) return Reply.failure("UnitCollectionDto is null");
        UnitCollection unitCollection = em.find(UnitCollection.class, dto.getId());
        if ( unitCollection == null ) return Reply.failure("UnitCollection of id " + dto.getId() + " does not exist");
        UnitCollectionMapper.INSTANCE.update(unitCollection, dto);
        for (Entry<PriceType, Double> price : dto.getPrices().entrySet()) {
            unitCollection.setPrice(price.getKey(), price.getValue(), "Price changed by " + username);
        }
        return Reply.success(unitCollection);
    }

    @Override
    public Reply<Void> delete(UnitCollection dto) {
        if ( dto == null ) return Reply.failure("UnitCollectionDto is null");
        UnitCollection unitCollection = em.find(UnitCollection.class, dto.getId());
        if ( unitCollection == null ) return Reply.failure("UnitCollection of id " + dto.getId() + " does not exist");
        unitCollection.setProduct(null);
        unitCollection.getUnits().clear();
        em.remove(unitCollection);
        return Reply.success(null);
    }

}
