/*
 * Copyright (C) 2021 GG-Net GmbH
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
package eu.ggnet.dwoss.receipt.ui.tryout.stub;

import java.util.List;

import jakarta.persistence.LockModeType;

import eu.ggnet.dwoss.uniqueunit.ee.UniqueUnitAgent;
import eu.ggnet.dwoss.uniqueunit.ee.entity.Product;
import eu.ggnet.dwoss.uniqueunit.ee.entity.UniqueUnit;
import eu.ggnet.dwoss.uniqueunit.ee.entity.UniqueUnit.Identifier;

/**
 *
 * @author oliver.guenther
 */
public class UniqueUnitAgentStub implements UniqueUnitAgent {

    private final List<UniqueUnit> uniqueUnits;

    private final List<Product> products;

    public UniqueUnitAgentStub(List<UniqueUnit> uniqueUnits, List<Product> products) {
        this.uniqueUnits = uniqueUnits;
        this.products = products;
    }

    @Override
    public Product findProductByPartNo(String partNo) {
        if ( partNo == null ) return null;
        return products.stream().filter(p -> partNo.equals(p.getPartNo())).findAny().orElse(null);
    }

    @Override
    public UniqueUnit findUnitByIdentifierEager(Identifier type, String identifier) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Product findProductByPartNoEager(String partNo) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <T> long count(Class<T> entityClass) {
        if ( entityClass.equals(UniqueUnit.class) ) return uniqueUnits.size();
        if ( entityClass.equals(Product.class) ) return products.size();
        throw new UnsupportedOperationException("Not implemented: " + entityClass);
    }

    @Override
    public <T> List<T> findAll(Class<T> entityClass) {
        if ( entityClass.equals(UniqueUnit.class) ) return (List<T>)uniqueUnits;
        if ( entityClass.equals(Product.class) ) return (List<T>)products;
        throw new UnsupportedOperationException("Not implemented: " + entityClass);
    }

    @Override
    public <T> List<T> findAll(Class<T> entityClass, int start, int amount) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <T> List<T> findAllEager(Class<T> entityClass) {
        return findAll(entityClass);
    }

    @Override
    public <T> List<T> findAllEager(Class<T> entityClass, int start, int amount) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <T> T findById(Class<T> entityClass, Object id) {
        // Allways return the first
        if ( entityClass.equals(UniqueUnit.class) ) return (T)uniqueUnits.get(0);
        if ( entityClass.equals(Product.class) ) return (T)products.get(0);
        throw new UnsupportedOperationException("UniqueUnitAgentStub.findById(" + entityClass + ") not implemented");
    }

    @Override
    public <T> T findById(Class<T> entityClass, Object id, LockModeType lockModeType) {
        return findById(entityClass, id);
    }

    @Override
    public <T> T findByIdEager(Class<T> entityClass, Object id) {
        return findById(entityClass, id);
    }

    @Override
    public <T> T findByIdEager(Class<T> entityClass, Object id, LockModeType lockModeType) {
        return findById(entityClass, id);
    }

}
