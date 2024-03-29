package eu.ggnet.dwoss.receipt.ui.tryout.stub;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.LockModeType;

import eu.ggnet.dwoss.stock.ee.StockAgent;
import eu.ggnet.dwoss.stock.ee.entity.*;

// TODO: Rename and move to subs
public class StockAgentStub implements StockAgent {

    private final List<Shipment> shipments;

    private final List<Stock> stocks;

    private final List<StockUnit> stockUnits;

    public StockAgentStub(List<Shipment> shipments, List<Stock> stocks, List<StockUnit> stockUnits) {
        this.shipments = shipments;
        this.stocks = stocks;
        this.stockUnits = stockUnits;
    }

    @Override
    public StockUnit findStockUnitByUniqueUnitIdEager(Integer uniqueUnitId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public StockUnit findStockUnitByRefurbishIdEager(String refurbishId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<StockTransaction> findStockTransactionEager(StockTransactionType type, StockTransactionStatusType statusType) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<StockTransaction> findStockTransactionEager(StockTransactionType type, StockTransactionStatusType statusType, int start, int amount) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<StockUnit> findStockUnitsByRefurbishIdEager(List<String> refurbishIds) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <T> T persist(T t) {
        System.out.println("StockAgentStub.persist(" + t + ")");
        if ( t instanceof Shipment )
            shipments.add((Shipment)t);
        return t;
    }

    @Override
    public <T> T merge(T t) {
        System.out.println("StockAgentStub.merge(" + t + ")");
        return t;
    }

    @Override
    public <T> void delete(T t) {
        System.out.println("StockAgentStub.delete(" + t + ")");
        if ( t instanceof Shipment )
            shipments.remove((Shipment)t);
    }

    @Override
    public <T> long count(Class<T> entityClass) {
        if ( entityClass == Shipment.class ) return shipments.size();
        throw new UnsupportedOperationException("count() - no implementation for " + entityClass);
    }

    @Override
    public <T> List<T> findAll(Class<T> entityClass) {
        if ( entityClass == Shipment.class ) return (List<T>)new ArrayList<>(shipments);
        if ( entityClass == Stock.class ) return (List<T>)stocks;
        return null;
    }

    @Override
    public <T> List<T> findAll(Class<T> entityClass, int start, int amount) {
        if ( entityClass == Shipment.class ) return (List<T>)new ArrayList<>(shipments);
        if ( entityClass == Stock.class ) return (List<T>)stocks;
        return null;
    }

    @Override
    public <T> List<T> findAllEager(Class<T> entityClass) {
        return findAll(entityClass);
    }

    @Override
    public <T> List<T> findAllEager(Class<T> entityClass, int start, int amount) {
        return findAll(entityClass, start, amount);
    }

    @Override
    public <T> T findById(Class<T> entityClass, Object id) {
        if ( entityClass == Shipment.class ) {
            Long sId = (Long)id;
            for (Shipment shipment : shipments) {
                if ( shipment.getId() == sId ) return (T)shipment;
            }
        }
        if ( entityClass == Stock.class ) {
            Integer sId = (Integer)id;
            for (Stock stock : stocks) {
                if ( stock.getId() == sId ) return (T)stock;
            }
        }
        return null;
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
