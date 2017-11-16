/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tryout;

import eu.ggnet.dwoss.uniqueunit.UniqueUnitAgent;
import eu.ggnet.dwoss.uniqueunit.entity.Product;
import eu.ggnet.dwoss.uniqueunit.entity.UniqueUnit;

import java.util.Collections;
import java.util.List;

import eu.ggnet.dwoss.uniqueunit.assist.gen.ProductGenerator;

/**
 *
 * @author lucas.huelsen
 */
public class UniqueUnitAgentStub implements UniqueUnitAgent {

    private final int AMOUNT = 200;

    private final int SLOW = 20;

    private final ProductGenerator GEN = new ProductGenerator();

    @Override
    public <T> long count(Class<T> entityClass) {
        if ( entityClass.equals(Product.class) ) {
            return AMOUNT;
        }
        throw new UnsupportedOperationException(entityClass + " not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <T> List<T> findAll(Class<T> entityClass) {
        if ( !entityClass.equals(Product.class) ) {
            throw new UnsupportedOperationException(entityClass + " not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        try {
            Thread.sleep(SLOW * AMOUNT);
            return (List<T>)(GEN.generateProduct(AMOUNT));
        } catch (InterruptedException ex) {
            return Collections.emptyList();
        }
    }

    @Override
    public <T> List<T> findAll(Class<T> entityClass, int start, int limit) {
        if ( !entityClass.equals(Product.class) ) {
            throw new UnsupportedOperationException(entityClass + " not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        if ( start + limit > AMOUNT ) {
            return Collections.emptyList();
        }
        try {
            Thread.sleep(SLOW * limit);
            return (List<T>)(GEN.generateProduct(limit));
        } catch (InterruptedException ex) {
            return Collections.emptyList();
        }
    }
    //<editor-fold defaultstate="collapsed" desc="Unused Methods">

    @Override
    public Product findProductByPartNo(String partNo) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public UniqueUnit findUnitByIdentifierEager(UniqueUnit.Identifier type, String identifier) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Product findProductByPartNoEager(String partNo) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <T> List<T> findAllEager(Class<T> entityClass) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <T> List<T> findAllEager(Class<T> entityClass, int start, int amount) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <T> T findById(Class<T> entityClass, Object id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <T> T findById(Class<T> entityClass, Object id, javax.persistence.LockModeType lockModeType) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <T> T findByIdEager(Class<T> entityClass, Object id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <T> T findByIdEager(Class<T> entityClass, Object id, javax.persistence.LockModeType lockModeType) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    //</editor-fold>

}
