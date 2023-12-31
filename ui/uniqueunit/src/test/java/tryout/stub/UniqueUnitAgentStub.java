/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tryout.stub;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.uniqueunit.ee.UniqueUnitAgent;
import eu.ggnet.dwoss.uniqueunit.ee.entity.Product;
import eu.ggnet.dwoss.uniqueunit.ee.entity.UniqueUnit;

/**
 *
 * @author lucas.huelsen
 */
public class UniqueUnitAgentStub implements UniqueUnitAgent {

    private final int AMOUNT = 200;

    private final int SLOW = 40;

    private final Logger L = LoggerFactory.getLogger(UniqueUnitAgentStub.class);

    private final ProductGenerator PGEN = new ProductGenerator();

    @Override
    public <T> long count(Class<T> entityClass) {
        if ( entityClass.equals(Product.class) ) {
            return AMOUNT;
        }
        throw new UnsupportedOperationException(entityClass + " not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <T> List<T> findAll(Class<T> entityClass) {

        if ( entityClass.equals(Product.class) ) {
            try {
                Thread.sleep(SLOW * AMOUNT);
                return (List<T>)(PGEN.generateProduct(AMOUNT));
            } catch (InterruptedException ex) {
                return Collections.emptyList();
            }
        }

        throw new UnsupportedOperationException(entityClass + " not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <T> List<T> findAll(Class<T> entityClass, int start, int limit) {

        if ( entityClass.equals(Product.class) ) {
            try {
                if ( start + limit > AMOUNT ) {
                    return Collections.emptyList();
                }
                Thread.sleep(SLOW * limit);
                return (List<T>)(PGEN.generateProduct(limit));
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }

        }
        throw new UnsupportedOperationException(entityClass + " not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <T> T findByIdEager(Class<T> entityClass, Object id) {
        if ( entityClass == Product.class ) {
            return (T)PGEN.generateProduct(1).get(0);
        }
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

//<editor-fold defaultstate="collapsed" desc="Unused Methods">
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
    public <T> T findById(Class<T> entityClass, Object id, jakarta.persistence.LockModeType lockModeType) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <T> T findById(Class<T> entityClass, Object id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <T> T findByIdEager(Class<T> entityClass, Object id, jakarta.persistence.LockModeType lockModeType) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Product findProductByPartNo(String partNo) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    //</editor-fold>

}
