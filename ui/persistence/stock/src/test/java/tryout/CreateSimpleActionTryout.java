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

import java.awt.Dimension;
import java.util.*;
import java.util.concurrent.CountDownLatch;

import javax.persistence.LockModeType;
import javax.swing.JButton;
import javax.swing.JPanel;

import org.junit.Test;

import eu.ggnet.dwoss.stock.StockAgent;
import eu.ggnet.dwoss.stock.StockTransactionProcessor;
import eu.ggnet.dwoss.stock.entity.*;
import eu.ggnet.dwoss.stock.transactions.CreateSimpleAction;
import eu.ggnet.dwoss.util.UserInfoException;
import eu.ggnet.saft.UiCore;
import eu.ggnet.saft.api.*;
import eu.ggnet.saft.core.Client;
import eu.ggnet.saft.core.authorisation.Guardian;
import eu.ggnet.saft.core.authorisation.UserChangeListener;

/**
 *
 * @author oliver.guenther
 */
public class CreateSimpleActionTryout {

    @Test
    public void tryout() throws InterruptedException {

        JPanel p = new JPanel();
        JButton b = new JButton("Press to close");
        b.setPreferredSize(new Dimension(200, 50));
        CountDownLatch l = new CountDownLatch(1);
        b.addActionListener(e -> {
            l.countDown();
        });

        p.add(new JButton(new CreateSimpleAction()));
        p.add(b);

        Client.addSampleStub(StockAgent.class, new StockAgent() {

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
        Client.addSampleStub(StockTransactionProcessor.class, new StockTransactionProcessor() {
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

        Client.addSampleStub(Guardian.class, new Guardian() {

            @Override
            public String getUsername() {
                return "Testuser";
            }

            // <editor-fold defaultstate="collapsed" desc="Unused Methodes">
            @Override
            public void logout() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public Set<String> getOnceLoggedInUsernames() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void login(String user, char[] pass) throws AuthenticationException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public Set<Authorisation> getRights() {
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
            public void add(Object enableAble, Authorisation authorisation) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void remove(Accessable accessable) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public boolean hasRight(Authorisation authorisation) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
            // </editor-fold>

        });

        UiCore.startSwing(() -> p);
        l.await();
    }

}
