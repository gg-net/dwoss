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
package eu.ggnet.dwoss.receipt.ui.tryout.fx;

import java.util.*;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.se.SeContainer;
import javax.enterprise.inject.se.SeContainerInitializer;
import javax.persistence.LockModeType;

import javafx.application.Application;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.stage.Stage;

import eu.ggnet.dwoss.core.common.values.ProductGroup;
import eu.ggnet.dwoss.core.common.values.tradename.TradeName;
import eu.ggnet.dwoss.core.widget.Dl;
import eu.ggnet.dwoss.core.widget.auth.*;
import eu.ggnet.dwoss.core.widget.cdi.WidgetProducers;
import eu.ggnet.dwoss.core.widget.dl.LocalDl;
import eu.ggnet.dwoss.core.widget.dl.RemoteDl;
import eu.ggnet.dwoss.mandator.api.Mandators;
import eu.ggnet.dwoss.mandator.api.value.*;
import eu.ggnet.dwoss.mandator.spi.CachedMandators;
import eu.ggnet.dwoss.receipt.ee.UnitProcessor;
import eu.ggnet.dwoss.receipt.ee.UnitSupporter;
import eu.ggnet.dwoss.receipt.ui.StockController;
import eu.ggnet.dwoss.receipt.ui.cap.EditUnitMenuItem;
import eu.ggnet.dwoss.receipt.ui.tryout.stub.*;
import eu.ggnet.dwoss.rights.api.AtomicRight;
import eu.ggnet.dwoss.spec.ee.SpecAgent;
import eu.ggnet.dwoss.spec.ee.entity.ProductSpec;
import eu.ggnet.dwoss.stock.api.PicoStock;
import eu.ggnet.dwoss.stock.ee.StockAgent;
import eu.ggnet.dwoss.stock.ee.entity.*;
import eu.ggnet.dwoss.stock.spi.ActiveStock;
import eu.ggnet.dwoss.uniqueunit.ee.assist.gen.UniqueUnitGenerator;
import eu.ggnet.dwoss.uniqueunit.ee.entity.Product;
import eu.ggnet.dwoss.uniqueunit.ee.entity.UniqueUnit;
import eu.ggnet.dwoss.uniqueunit.ee.entity.UniqueUnit.Identifier;
import eu.ggnet.saft.core.*;
import eu.ggnet.saft.core.impl.Fx;

/**
 *
 * @author mirko.schulze
 */
public class EditUnitTryout {

    public static class EditUnitApplication extends Application {

        @Override
        public void start(Stage stage) throws Exception {
            SeContainerInitializer ci = SeContainerInitializer.newInstance();
            ci.addPackages(WidgetProducers.class);
            ci.addPackages(EditUnitTryout.class);
            ci.addPackages(true, StockController.class);
            ci.disableDiscovery();
            SeContainer container = ci.initialize();
            Instance<Object> instance = container.getBeanManager().createInstance();

            Saft saft = instance.select(Saft.class).get();
            UiCore.initGlobal(saft);
            saft.addOnShutdown(() -> container.close());

            Random randy = new Random();

            UniqueUnitGenerator uug = new UniqueUnitGenerator();
            List<UniqueUnit> uniqueUnits = new ArrayList<>();
            List<StockUnit> stockUnits = new ArrayList<>();
            List<Stock> stocks = Arrays.asList(new Stock(0, "Lager 1"), new Stock(1, "Lager 2"), new Stock(2, "Lager 3"));

            Product p = new Product(ProductGroup.PHONE, TradeName.ACER, "PartNo", "Name");

            for (int j = 0; j < 10; j++) {
                UniqueUnit uu = uug.makeUniqueUnit(TradeName.ACER, p);
                uu.setProduct(p);
                uniqueUnits.add(uu);
                if ( randy.nextInt(10) <= 7 ) {
                    StockUnit su = new StockUnit(uu.getRefurbishId(), uu.getProduct().getName(), uu.getId());
                    su.setStock(stocks.get(randy.nextInt(stocks.size() - 1)));
                    stockUnits.add(su);
                }
            }
            System.out.println("refurbishedIds:");
            uniqueUnits.forEach(u -> System.out.println(u.getIdentifier(Identifier.REFURBISHED_ID)));

            LocalDl local = instance.select(LocalDl.class).get();

            local.add(Guardian.class, new Guardian() {

                @Override
                public String getUsername() {
                    return "Demo";
                }

                //<editor-fold defaultstate="collapsed" desc="unused methods">
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

            local.add(ActiveStock.class, new ActiveStock() {

                @Override
                public PicoStock getActiveStock() {
                    return stocks.get(2).toPicoStock();
                }

                //<editor-fold defaultstate="collapsed" desc="unused methods">
                @Override
                public void setActiveStock(PicoStock stock) {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }
                //</editor-fold>
            });

            local.add(CachedMandators.class, new CachedMandators() {
                Set<TradeName> contractors = new HashSet<>(Arrays.asList(TradeName.ACER));

                @Override
                public Contractors loadContractors() {
                    return new Contractors(contractors, contractors);
                }

                @Override
                public ReceiptCustomers loadReceiptCustomers() {
                    return new ReceiptCustomers(new HashMap<>());
                }

                //<editor-fold defaultstate="collapsed" desc="unused methods">
                @Override
                public Mandator loadMandator() {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }

                @Override
                public DefaultCustomerSalesdata loadSalesdata() {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }

                @Override
                public SpecialSystemCustomers loadSystemCustomers() {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }

                @Override
                public PostLedger loadPostLedger() {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }
                //</editor-fold>
            });

            RemoteDl remote = instance.select(RemoteDl.class).get();

            remote.add(StockAgent.class, new StockAgent() {
                @Override
                @SuppressWarnings("unchecked")
                public <T> List<T> findAll(Class<T> entityClass) {
                    return (List<T>)stocks;
                }

                //<editor-fold defaultstate="collapsed" desc="unused methods">
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

            remote.add(UnitProcessor.class, new UnitProcessorStub(uniqueUnits, stockUnits));

            remote.add(UnitSupporter.class, new UnitSupporterStub(uniqueUnits));

            remote.add(SpecAgent.class, new SpecAgent() {

                //<editor-fold defaultstate="collapsed" desc="unused methods">
                @Override
                public ProductSpec findProductSpecByPartNoEager(String partNo) {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }

                @Override
                public <T> long count(Class<T> entityClass) {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }

                @Override
                public <T> List<T> findAll(Class<T> entityClass) {
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

            UiUtil.startup(stage, () -> {
                Menu m = new Menu("Lager/Logistik");
                m.getItems().addAll(instance.select(EditUnitMenuItem.class).get());
                return new MenuBar(m);
            });

            saft.core(Fx.class).initMain(stage);
        }
    }

    public static void main(String[] args) {
        Application.launch(EditUnitApplication.class, args);
    }

}
