package tryout;

import java.util.*;

import javax.persistence.LockModeType;
import javax.swing.JButton;
import javax.swing.JPanel;

import eu.ggnet.dwoss.core.common.UserInfoException;
import eu.ggnet.dwoss.core.common.values.SalesChannel;
import eu.ggnet.dwoss.core.widget.Dl;
import eu.ggnet.dwoss.core.widget.auth.*;
import eu.ggnet.dwoss.misc.ee.SalesChannelHandler;
import eu.ggnet.dwoss.misc.ui.cap.OpenSalesChannelManagerAction;
import eu.ggnet.dwoss.rights.api.AtomicRight;
import eu.ggnet.dwoss.stock.ee.StockAgent;
import eu.ggnet.dwoss.stock.ee.entity.*;
import eu.ggnet.dwoss.stock.ee.model.SalesChannelLine;
import eu.ggnet.saft.core.Ui;
import eu.ggnet.saft.core.UiCore;

import static eu.ggnet.dwoss.core.common.values.SalesChannel.CUSTOMER;
import static eu.ggnet.dwoss.core.common.values.SalesChannel.RETAILER;

/**
 *
 * @author oliver.guenther
 */
public class SalesChannelManagerTryout {

    public static void main(String... args) {

        final Stock laden = new Stock(0, "Laden");
        laden.setPrimaryChannel(RETAILER);
        final Stock lager = new Stock(1, "Lager");
        lager.setPrimaryChannel(CUSTOMER);

        Dl.remote().add(StockAgent.class, new StockAgent() {
            @Override
            public <T> List<T> findAll(Class<T> entityClass) {
                if ( entityClass.equals(Stock.class) ) return (List<T>)Arrays.asList(laden, lager);
                return null;
            }

            //<editor-fold defaultstate="collapsed" desc="unused">
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
        Dl.remote().add(SalesChannelHandler.class, new SalesChannelHandler() {
            @Override
            public List<SalesChannelLine> findAvailableUnits() {
                List<SalesChannelLine> lines = new ArrayList<>();
                lines.add(new SalesChannelLine(0, "22231", "Acer Aspire 3222-üäö", "gebraucht", 10, 10, "Lager", SalesChannel.UNKNOWN, lager.getId()));
                lines.add(new SalesChannelLine(1, "23212", "Acer Aspire 5102WLMi-€", "gebraucht", 10, 10, "Lager", SalesChannel.RETAILER, lager.getId()));
                lines.add(new SalesChannelLine(2, "43521", "Acer Aspire X3200", "gebraucht", 10, 10, "Lager", SalesChannel.RETAILER, lager.getId()));
                lines.add(new SalesChannelLine(4, "58247", "Acer Aspire One A150X blau", "gebraucht", 10, 10, "Lager", SalesChannel.CUSTOMER, laden.getId()));
                lines.add(new SalesChannelLine(5, "82235", "Acer Aspire 8930G-583G32Bn", "gebraucht", 10, 10, "Lager", SalesChannel.CUSTOMER, laden.getId()));
                lines.add(new SalesChannelLine(6, "19262", "Acer Aspire 8920G-834G32Bn", "gebraucht", 10, 10, "Lager", SalesChannel.CUSTOMER, lager.getId()));
                lines.add(new SalesChannelLine(7, "17239", "Acer Aspire 7330-572G16Mn", "Originalkarton, nahezu neuwertig", 10, 10, "Lager", SalesChannel.UNKNOWN, lager.getId()));
                return lines;
            }

            @Override
            public boolean update(List<SalesChannelLine> lines, String arranger, String transactionComment) throws UserInfoException {
                System.out.println("Update");
                System.out.println("Lines: " + lines);
                System.out.println("Arranger: " + arranger);
                System.out.println("Comment: " + transactionComment);
                return true;
            }
        });
        Dl.local().add(Guardian.class, new Guardian() {

            @Override
            public String getUsername() {
                return "Demo";
            }

            //<editor-fold defaultstate="collapsed" desc="unused">
            @Override
            public void logout() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public Set<String> getOnceLoggedInUsernames() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public Set<String> getAllUsernames() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void login(String user, char[] pass) throws AuthenticationException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public Set<AtomicRight> getRights() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public boolean quickAuthenticate(int userId) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void remove(Object instance) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void addUserChangeListener(UserChangeListener listener) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void removeUserChangeListener(UserChangeListener listener) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void add(Accessable accessable) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void add(Object enableAble, AtomicRight authorisation) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void remove(Accessable accessable) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public boolean hasRight(AtomicRight authorisation) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            //</editor-fold>
        });

        JButton close = new JButton("Schliessen");
        close.addActionListener(e -> Ui.closeWindowOf(close));

        JButton run = new JButton("Verkaufskanalmanager öffnen");
        run.setAction(new OpenSalesChannelManagerAction());

        JPanel p = new JPanel();
        p.add(run);
        p.add(close);

        UiCore.startSwing(() -> p);
    }

}
