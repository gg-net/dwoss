package tryout;

import eu.ggnet.dwoss.stock.ee.entity.Stock;
import eu.ggnet.dwoss.stock.ee.entity.StockTransactionType;
import eu.ggnet.dwoss.stock.ee.entity.StockTransaction;
import eu.ggnet.dwoss.stock.ee.entity.StockUnit;
import eu.ggnet.dwoss.stock.ee.entity.StockTransactionStatusType;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.persistence.LockModeType;
import javax.swing.*;

import org.junit.Test;

import eu.ggnet.dwoss.misc.ee.SalesChannelHandlerOperation.LastCharsRefurbishIdSorter;
import eu.ggnet.dwoss.misc.saleschannel.SalesChannelManagerDialog;
import eu.ggnet.dwoss.misc.saleschannel.SalesChannelTableModel;
import eu.ggnet.dwoss.rules.SalesChannel;
import eu.ggnet.dwoss.stock.ee.StockAgent;
import eu.ggnet.dwoss.stock.ee.model.SalesChannelLine;
import eu.ggnet.saft.Dl;

import static eu.ggnet.dwoss.rules.SalesChannel.CUSTOMER;
import static eu.ggnet.dwoss.rules.SalesChannel.RETAILER;

/**
 *
 * @author oliver.guenther
 */
public class SalesChannelManager {

    @Test
    public void testSalesChannelManager() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException ex) {
        }
        final Stock laden = new Stock(0, "Laden");
        final Stock lager = new Stock(1, "Lager");

        Dl.remote().add(StockAgent.class, new StockAgent() {
            @Override
            public <T> List<T> findAll(Class<T> entityClass) {
                if ( entityClass.equals(Stock.class) ) return (List<T>)Arrays.asList(laden, lager);
                return null;
            }

            //<editor-fold defaultstate="collapsed" desc="properties">
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
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public <T> T merge(T t) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public <T> void delete(T t) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public StockTransaction findOrCreateRollInTransaction(int stockId, String userName, String comment) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public <T> long count(Class<T> entityClass) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public <T> List<T> findAll(Class<T> entityClass, int start, int amount) {
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
            //</editor-fold>
        });

        JFrame f = new JFrame();
        SalesChannelManagerDialog dialog = new SalesChannelManagerDialog(f);
        List<SalesChannelLine> lines = new ArrayList<>();
        lines.add(new SalesChannelLine(0, "22231", "Acer Aspire 3222-üäö", "gebraucht", 10, 10, "Lager", SalesChannel.UNKNOWN, lager.getId()));
        lines.add(new SalesChannelLine(1, "23212", "Acer Aspire 5102WLMi-€", "gebraucht", 10, 10, "Lager", SalesChannel.RETAILER, lager.getId()));
        lines.add(new SalesChannelLine(2, "43521", "Acer Aspire X3200", "gebraucht", 10, 10, "Lager", SalesChannel.RETAILER, lager.getId()));
        lines.add(new SalesChannelLine(4, "58247", "Acer Aspire One A150X blau", "gebraucht", 10, 10, "Lager", SalesChannel.CUSTOMER, laden.getId()));
        lines.add(new SalesChannelLine(5, "82235", "Acer Aspire 8930G-583G32Bn", "gebraucht", 10, 10, "Lager", SalesChannel.CUSTOMER, laden.getId()));
        lines.add(new SalesChannelLine(6, "19262", "Acer Aspire 8920G-834G32Bn", "gebraucht", 10, 10, "Lager", SalesChannel.CUSTOMER, lager.getId()));
        lines.add(new SalesChannelLine(7, "17239", "Acer Aspire 7330-572G16Mn", "Originalkarton, nahezu neuwertig", 10, 10, "Lager", SalesChannel.UNKNOWN, lager.getId()));

        Map<SalesChannel, Stock> stockToChannel = new EnumMap<>(SalesChannel.class);
        stockToChannel.put(CUSTOMER, laden);
        stockToChannel.put(RETAILER, lager);

        dialog.setModel(new SalesChannelTableModel(lines, stockToChannel));
        Dimension dim = dialog.getToolkit().getScreenSize();
        Rectangle abounds = dialog.getBounds();
        dialog.setLocation((int)((dim.width - abounds.width) / 3),
                (dim.height - abounds.height) / 2);
        dialog.setVisible(true);
        System.out.println("OK = " + dialog.isOk());
        Map<Stock, List<String>> destinationsWithStockUnitIds = lines
                .stream()
                .filter(l -> l.getDestination() != null) // No Destination change
                .sorted(new LastCharsRefurbishIdSorter()) // Sort
                .collect(Collectors.groupingBy(SalesChannelLine::getDestination,
                        Collectors.mapping(SalesChannelLine::getRefurbishedId, Collectors.toList())));

        for (Entry<Stock, List<String>> entry : destinationsWithStockUnitIds.entrySet()) {
            System.out.println(entry.getKey().getName() + ",refurbishIds=" + entry.getValue());
        }

        f.dispose();

    }

}
