/*
 * Copyright (C) 2014 GG-Net GmbH
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
package tryout;

import eu.ggnet.saft.core.Ui;
import eu.ggnet.saft.core.Dl;
import eu.ggnet.saft.core.UiCore;
import eu.ggnet.dwoss.stock.ee.entity.StockTransactionStatusType;
import eu.ggnet.dwoss.stock.ee.entity.StockTransaction;
import eu.ggnet.dwoss.stock.ee.entity.Stock;
import eu.ggnet.dwoss.stock.ee.entity.StockTransactionType;
import eu.ggnet.dwoss.stock.ee.entity.StockUnit;

import java.awt.Dimension;
import java.util.*;

import javax.persistence.LockModeType;
import javax.swing.JButton;
import javax.swing.JPanel;

import eu.ggnet.dwoss.stock.ee.StockAgent;
import eu.ggnet.dwoss.stock.ee.StockTransactionProcessor;
import eu.ggnet.dwoss.stock.ui.cap.CreateSimpleAction;
import eu.ggnet.dwoss.core.common.UserInfoException;
import eu.ggnet.dwoss.core.widget.auth.Guardian;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

/**
 *
 * @author oliver.guenther
 */
public class CreateSimpleActionTryout {

    public static void main(String[] args) {
        JPanel p = new JPanel();
        JButton b = new JButton("Press to close");
        b.setPreferredSize(new Dimension(200, 50));
        b.addActionListener(e -> {
            Ui.closeWindowOf(b);
        });

        p.add(new JButton(new CreateSimpleAction()));
        p.add(b);

        Dl.remote().add(StockAgent.class, new StockAgent() {

            @Override
            public <T> List<T> findAll(Class<T> entityClass) {
                if ( entityClass.equals(Stock.class) ) return (List<T>)Arrays.asList(new Stock(0, "Rotes Lager"), new Stock(1, "Blaues Lager"));
                return null;
            }

            @Override
            public List<StockUnit> findStockUnitsByRefurbishIdEager(List<String> refurbishIds) {
                if ( refurbishIds.contains("1") ) {
                    Stock s = new Stock(0, "Lager1");
                    StockUnit s1 = new StockUnit("1", "Gerät Eins", 1);
                    s1.setStock(s);
                    return Arrays.asList(s1);
                }
                return Collections.EMPTY_LIST;
            }

            // <editor-fold defaultstate="collapsed" desc="Unused Methodes">
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
            // </editor-fold>
        });
        Dl.remote().add(StockTransactionProcessor.class, new StockTransactionProcessor() {
            @Override
            public SortedMap<Integer, String> perpareTransfer(List<StockUnit> stockUnits, int destinationStockId, String arranger, String comment) throws UserInfoException {
                SortedMap<Integer, String> r = new TreeMap<>();
                r.put(1, "1");
                return r;
            }
            // <editor-fold defaultstate="collapsed" desc="Unused Methodes">

            @Override
            public List<Integer> rollIn(List<StockTransaction> detachtedTransactions, String arranger) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void cancel(StockTransaction transaction, String arranger, String comment) throws UserInfoException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void commission(List<StockTransaction> transactions, String picker, String deliverer) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void receive(List<StockTransaction> transactions, String deliverer, String reciever) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void removeFromPreparedTransaction(String refurbishId, String arranger, String comment) throws UserInfoException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
            // </editor-fold>

        });
        Guardian guardianMock = mock(Guardian.class);
        given(guardianMock.getUsername()).willAnswer(i -> "Testuser");
        Dl.local().add(Guardian.class, guardianMock);

        UiCore.startSwing(() -> p);
    }

}
