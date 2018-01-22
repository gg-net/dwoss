/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tryout.stub;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.uniqueunit.UniqueUnitAgent;
import eu.ggnet.dwoss.uniqueunit.api.PicoProduct;
import eu.ggnet.dwoss.uniqueunit.api.PicoUnit;
import eu.ggnet.dwoss.uniqueunit.entity.dto.CategoryProductDto;
import eu.ggnet.dwoss.uniqueunit.entity.*;
import eu.ggnet.saft.api.Reply;

/**
 *
 * @author lucas.huelsen
 */
public class UniqueUnitAgentStub implements UniqueUnitAgent {

    private final int AMOUNT = 200;

    private final int SLOW = 40;

    private final Logger L = LoggerFactory.getLogger(UniqueUnitAgentStub.class);

    private final CategoryProductGenerator CPGEN = new CategoryProductGenerator();

    private final ProductGenerator PGEN = new ProductGenerator();

    @Override
    public <T> long count(Class<T> entityClass) {
        if ( entityClass.equals(CategoryProduct.class) ) {
            return AMOUNT;
        } else if ( entityClass.equals(Product.class) ) {
            return AMOUNT;
        }
        throw new UnsupportedOperationException(entityClass + " not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <T> List<T> findAll(Class<T> entityClass) {

        if ( entityClass.equals(CategoryProduct.class) ) {
            try {
                Thread.sleep(SLOW * AMOUNT);
                return (List<T>)(CPGEN.generateCategoryProduct(AMOUNT));
            } catch (InterruptedException ex) {
                return Collections.emptyList();
            }
        } else if ( entityClass.equals(Product.class) ) {
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

        if ( entityClass.equals(CategoryProduct.class) ) {
            if ( start + limit > AMOUNT ) {
                return Collections.emptyList();
            }
            try {
                Thread.sleep(SLOW * limit);
                return (List<T>)(CPGEN.generateCategoryProduct(limit));
            } catch (InterruptedException ex) {
                return Collections.emptyList();
            }
        }

        if ( entityClass.equals(Product.class) ) {
            if ( start + limit > AMOUNT ) {
                return Collections.emptyList();
            }
            try {
                Thread.sleep(SLOW * limit);
                L.info("Collecting Products..");
                return (List<T>)(PGEN.generateProduct(limit));
            } catch (InterruptedException ex) {
                return Collections.emptyList();
            }
        }
        throw new UnsupportedOperationException(entityClass + " not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public CategoryProduct createOrUpdate(CategoryProductDto dto, String username) throws NullPointerException {
        CategoryProduct cp = new CategoryProduct();
        cp.setName(dto.getName());
        cp.setDescription(dto.getDescription());
        cp.setSalesChannel(dto.getSalesChannel());
        cp.getProducts().forEach(p -> p.setCategoryProduct(null));

        for (PicoProduct pp : dto.getProducts()) {
            Product p = new Product();
            p.setName(pp.getShortDescription());
            cp.getProducts().add(p);
        }
        for (Map.Entry<PriceType, Double> price : dto.getPrices().entrySet()) {
            cp.setPrice(price.getKey(), price.getValue(), "Price changed by " + username);

        }
        System.out.println("Storing " + cp);
        return cp;
    }

    @Override
    public Reply<Void> addToUnitCollection(PicoUnit unit, long unitCollectionId) {
        return Reply.success(null);
    }

    @Override
    public Reply<Void> unsetUnitCollection(PicoUnit unit) {
        return Reply.success(null);
    }

    @Override
    public <T> T findByIdEager(Class<T> entityClass, Object id) {
        if ( entityClass == Product.class ) {
            return (T)PGEN.generateProduct(1).get(0);
        } else if ( entityClass == UnitCollection.class ) {
            return (T)PGEN.generateProduct(1).get(0).getUnitCollections().get(0);
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
    public <T> T findById(Class<T> entityClass, Object id, javax.persistence.LockModeType lockModeType) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <T> T findById(Class<T> entityClass, Object id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <T> T findByIdEager(Class<T> entityClass, Object id, javax.persistence.LockModeType lockModeType) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Product findProductByPartNo(String partNo) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Reply<Void> deleteCategoryProduct(long id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    //</editor-fold>

}
