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

import java.util.*;

import javax.persistence.LockModeType;

import eu.ggnet.dwoss.spec.ee.SpecAgent;
import eu.ggnet.dwoss.spec.ee.entity.ProductSeries;
import eu.ggnet.dwoss.spec.ee.entity.ProductSpec;
import eu.ggnet.dwoss.spec.ee.entity.piece.Cpu;
import eu.ggnet.dwoss.spec.ee.entity.piece.Gpu;

/**
 *
 * @author oliver.guenther
 */
public class SpecAgentStub implements SpecAgent {

    private final Set<ProductSeries> serieses;

    private final List<Gpu> gpus;

    private final List<Cpu> cpus;

    private final Map<String, ProductSpec> specs;

    public SpecAgentStub(Set<ProductSeries> serieses, List<Gpu> gpus, List<Cpu> cpus, Map<String, ProductSpec> specs) {
        this.serieses = serieses;
        this.gpus = gpus;
        this.cpus = cpus;
        this.specs = specs;
    }

    @Override
    public ProductSpec findProductSpecByPartNoEager(String partNo) {
        return specs.get(partNo);
    }

    @Override
    public <T> long count(Class<T> entityClass) {
        if ( entityClass.equals(Cpu.class) ) return cpus.size();
        if ( entityClass.equals(Gpu.class) ) return gpus.size();
        if ( entityClass.equals(ProductSeries.class) ) return serieses.size();
        if ( entityClass.equals(ProductSpec.class) ) return specs.values().size();
        return 0;
    }

    @Override
    public <T> List<T> findAll(Class<T> entityClass) {
        if ( entityClass.equals(Cpu.class) ) return (List<T>)cpus;
        if ( entityClass.equals(Gpu.class) ) return (List<T>)gpus;
        if ( entityClass.equals(ProductSeries.class) ) return (List<T>)new ArrayList<>(serieses);
        if ( entityClass.equals(ProductSpec.class) ) return (List<T>)new ArrayList(specs.values());
        return null;
    }

    @Override
    public <T> List<T> findAll(Class<T> entityClass, int start, int amount) {
        return findAll(entityClass);
    }

    @Override
    public <T> List<T> findAllEager(Class<T> entityClass) {
        return findAll(entityClass);
    }

    @Override
    public <T> List<T> findAllEager(Class<T> entityClass, int start, int amount) {
        return findAll(entityClass);
    }

    @Override
    public <T> T findById(Class<T> entityClass, Object id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <T> T findById(Class<T> entityClass, Object id, LockModeType lockModeType) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <T> T findByIdEager(Class<T> entityClass, Object id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <T> T findByIdEager(Class<T> entityClass, Object id, LockModeType lockModeType) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
};
