package eu.ggnet.dwoss.receipt.ui.tryout.stub;

import eu.ggnet.dwoss.stock.ee.entity.Shipment;
import eu.ggnet.dwoss.stock.ee.entity.StockTransaction;
import eu.ggnet.dwoss.stock.ee.entity.StockTransactionStatusType;
import eu.ggnet.dwoss.stock.ee.entity.StockTransactionType;
import eu.ggnet.dwoss.stock.ee.entity.StockUnit;

import java.util.*;

import javax.persistence.LockModeType;

import eu.ggnet.dwoss.core.common.values.tradename.TradeName;
import eu.ggnet.dwoss.stock.ee.StockAgent;

// TODO: Rename and move to subs
public class StockAgentStub implements StockAgent {

    private List<Shipment> shipments;

    private final Random R = new Random();

    public StockAgentStub() {
        shipments = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            shipments.add(new Shipment("Test-SH-" + i,
                    TradeName.values()[R.nextInt(TradeName.values().length)],
                    TradeName.getManufacturers().toArray(new TradeName[0])[R.nextInt(TradeName.getManufacturers().size())],
                    Shipment.Status.values()[R.nextInt(Shipment.Status.values().length)]));
        }
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
        System.out.println("ServiceCall: Create Shipment in Database!");
        if ( t instanceof Shipment )
            shipments.add((Shipment)t);
        return t;
    }

    @Override
    public <T> T merge(T t) {
        System.out.println("Updateting the t: " + t);
        return t;
    }

    @Override
    public <T> void delete(T t) {
        System.out.println("ServiceCall: Delete Shipment in Database!");
        if ( t instanceof Shipment )
            shipments.remove((Shipment)t);
    }

    @Override
    public <T> long count(Class<T> entityClass) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <T> List<T> findAll(Class<T> entityClass) {
        if ( entityClass == Shipment.class )
            return (List<T>)shipments;
        return null;
    }

    @Override
    public <T> List<T> findAll(Class<T> entityClass, int start, int amount) {
        if ( entityClass == Shipment.class )
            return (List<T>)shipments;
        return null;
    }

    @Override
    public <T> List<T> findAllEager(Class<T> entityClass) {
        if ( entityClass == Shipment.class )
            return (List<T>)shipments;
        return null;
    }

    @Override
    public <T> List<T> findAllEager(Class<T> entityClass, int start, int amount) {
        if ( entityClass == Shipment.class )
            return (List<T>)shipments;
        return null;
    }

    @Override
    public <T> T findById(Class<T> entityClass, Object id) {
        if ( entityClass == Shipment.class ) {
            Long sId = (Long)id;
            for (Shipment shipment : shipments) {
                if ( shipment.getId() == sId ) return (T)shipment;
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

    @Override
    public StockTransaction findOrCreateRollInTransaction(int stockId, String userName, String comment) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
