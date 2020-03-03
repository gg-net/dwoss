/*
 * Copyright (C) 2020 GG-Net GmbH
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
package eu.ggnet.dwoss.assembly.client.support;

import java.awt.Toolkit;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.se.SeContainer;
import javax.enterprise.inject.se.SeContainerInitializer;
import javax.persistence.LockModeType;
import javax.validation.ConstraintViolationException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.*;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import eu.ggnet.dwoss.assembly.client.Main;
import eu.ggnet.dwoss.assembly.client.support.login.LoggedInTimeout;
import eu.ggnet.dwoss.assembly.remote.MainCdi;
import eu.ggnet.dwoss.assembly.remote.cdi.FxmlLoaderInitializer;
import eu.ggnet.dwoss.assembly.remote.exception.*;
import eu.ggnet.dwoss.core.common.UserInfoException;
import eu.ggnet.dwoss.core.system.GlobalConfig;
import eu.ggnet.dwoss.core.system.autolog.LoggerProducer;
import eu.ggnet.dwoss.core.widget.AbstractGuardian;
import eu.ggnet.dwoss.customer.ui.CustomerTaskService;
import eu.ggnet.dwoss.mail.ui.cap.SendResellerListToSubscribedCustomersMenuItem;
import eu.ggnet.dwoss.mandator.api.Mandators;
import eu.ggnet.dwoss.mandator.api.value.*;
import eu.ggnet.dwoss.misc.ui.AboutController;
import eu.ggnet.dwoss.price.ui.PriceBlockerViewCask;
import eu.ggnet.dwoss.receipt.ui.UiUnitSupport;
import eu.ggnet.dwoss.redtapext.ui.ReactivePicoUnitDetailViewCask;
import eu.ggnet.dwoss.report.ui.RawReportView;
import eu.ggnet.dwoss.rights.api.AtomicRight;
import eu.ggnet.dwoss.rights.api.Operator;
import eu.ggnet.dwoss.rights.ui.UiPersona;
import eu.ggnet.dwoss.search.ui.SearchCask;
import eu.ggnet.dwoss.stock.api.PicoStock;
import eu.ggnet.dwoss.stock.api.StockApi;
import eu.ggnet.dwoss.stock.ee.StockAgent;
import eu.ggnet.dwoss.stock.ee.entity.*;
import eu.ggnet.dwoss.stock.ui.StockUpiImpl;
import eu.ggnet.dwoss.uniqueunit.ui.ProductTask;
import eu.ggnet.saft.core.Dl;
import eu.ggnet.saft.core.UiCore;
import eu.ggnet.saft.core.dl.RemoteLookup;
import eu.ggnet.saft.experimental.auth.AuthenticationException;
import eu.ggnet.saft.experimental.auth.Guardian;

import static eu.ggnet.dwoss.core.common.values.tradename.TradeName.*;
import static javafx.event.EventType.ROOT;

/**
 *
 * @author oliver.guenther
 */
public class ClientApplication extends Application {

    private SeContainer container;

    private Label info;

    private Parent mainView;

    private Instance<Object> instance;

    private FirstLoginController login(Stage owner) {
        FXMLLoader loader = new FXMLLoader(FirstLoginController.class.getResource("FirstLoginView.fxml"));
        Parent loginView;
        try {
            loginView = loader.load();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        FirstLoginController loginController = loader.getController();
        // Manual FX, will be different in manual swing.
        Stage loginStage = new Stage();
        loginStage.initModality(Modality.APPLICATION_MODAL);
        loginStage.initOwner(owner);
        loginStage.setScene(new Scene(loginView));
        loginStage.setOnCloseRequest(e -> loginController.closed());
        loginStage.show();
        loginController.setLoginListener(() -> {
            Platform.runLater(() -> loginStage.close());
            // Restart the timer.
            instance.select(LoggedInTimeout.class).get().startTime();
        });
        loginController.setCanceledListener(() -> Platform.exit());
        return loginController;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        LafMenuManager.loadAndSetUserLaf();

        info = new Label("Info here");
        StackPane mainPane = new StackPane(info);
        mainPane.setPrefSize(800, 600);

        Scene s = new Scene(mainPane);

        // Global KeyListener for Logout
        // TODO: Later we need some form of global registration, so that the KeyEvents are added to all future stages.
        primaryStage.setScene(s);

        primaryStage.show();

        // Non CDI mode, CDI stats in postInit.
        FirstLoginController loginController = login(primaryStage);

        CompletableFuture
                .runAsync(this::postInit)
                .thenRunAsync(() -> {
                    // Replace Mainview, later kick in Swing temporyry
                    // And remember to relocate
                    mainPane.getChildren().clear(); // remove everything
                    mainPane.getChildren().add(mainView);
                }, Platform::runLater)
                .thenRunAsync(() -> { // Phase one only classic lookups
                    loginController.setAndActivateGuardian(Dl.local().lookup(Guardian.class));

                    LoggedInTimeout loggedInTimeout = instance.select(LoggedInTimeout.class).get();

                    loggedInTimeout.setTimeoutAction(() -> {
                        Dl.local().lookup(Guardian.class).logout();
                        FirstLoginController controller = login(primaryStage);
                        controller.setAndActivateGuardian(Dl.local().lookup(Guardian.class));
                    });

                    // Global Eventlistener for activity tracking
                    s.addEventHandler(ROOT, (e) -> loggedInTimeout.resetTime());

                    // Ctrl + Shift + L global keylistener.
                    KeyCombination keysCtrlShiftL = new KeyCodeCombination(KeyCode.L, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN);
                    s.addEventFilter(KeyEvent.KEY_RELEASED, k -> {
                        if ( keysCtrlShiftL.match(k) ) {
                            loggedInTimeout.manualTimeout();
                        }
                    });
                    // TODO: Load timeout from storage
                    loggedInTimeout.setTimeoutAndStartTime(null);
                    loggedInTimeout.setTimeoutStore(t -> System.out.println("Todo: Store Timechange " + t));

                })
                .handle((Void t, Throwable u) -> {
                    u.printStackTrace();
                    return null;
                });
    }

    @Override
    public void stop() throws Exception {
        if ( container.isRunning() ) {
            // Shutdown the global executor.
            container.getBeanManager().createInstance().select(ExecutorManager.class).get().shutdown();
            container.close();
        }
        UiCore.shutdown();
    }

    /**
     * init after start
     */
    public void postInit() {
        // Setting the Exception Handler
        Toolkit.getDefaultToolkit().getSystemEventQueue().push(new UnhandledExceptionCatcher());
        UiCore.overwriteFinalExceptionConsumer(new DwFinalExceptionConsumer());
        UiCore.registerExceptionConsumer(UserInfoException.class, new UserInfoExceptionConsumer());
        UiCore.registerExceptionConsumer(ConstraintViolationException.class, new ConstraintViolationConsumer());

        /*
        // Global Key handler (Strg + Shift + L) für logout.
        // Todo: This needs to go to saft somehow. In Swing
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {
            @Override
            public boolean dispatchKeyEvent(KeyEvent e) {
                if ( e.isControlDown() && e.isShiftDown() && e.getKeyCode() == KeyEvent.VK_L ) {
                    System.out.println("Tastenkombination gefunden");
                }
                return false;
            }
        });
        Toolkit.getDefaultToolkit().addAWTEventListener((AWTEvent event) -> System.out.println("Aktivität"),
                MOUSE_MOTION_EVENT_MASK | MOUSE_EVENT_MASK | KEY_EVENT_MASK);
         */
        // TODO: remove later,
        Dl.local().add(RemoteLookup.class, new RemoteLookup() {
            @Override
            public <T> boolean contains(Class<T> clazz) {
                if ( StockAgent.class.equals(clazz) ) return true;
                return false;
            }

            //<editor-fold defaultstate="collapsed" desc="remoteAgent">
            @Override
            public <T> T lookup(Class<T> clazz) {
                if ( StockAgent.class.equals(clazz) ) return (T)new StockAgent() {
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
                        public <T> List<T> findAll(Class<T> entityClass) {
                            if ( entityClass.equals(Stock.class) ) {
                                return (List<T>)Arrays.asList(new Stock(1, "Hamburg"), new Stock(2, "Bremen"));
                            }
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
                    };
                if ( Mandators.class.equals(clazz) ) return (T)new Mandators() {
                        @Override
                        public Mandator loadMandator() {
                            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                        }

                        @Override
                        public DefaultCustomerSalesdata loadSalesdata() {
                            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                        }

                        @Override
                        public ReceiptCustomers loadReceiptCustomers() {
                            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                        }

                        @Override
                        public SpecialSystemCustomers loadSystemCustomers() {
                            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                        }

                        @Override
                        public Contractors loadContractors() { // used in price sub menu
                            return new Contractors(EnumSet.of(ACER, LENOVO), EnumSet.of(ACER, PACKARD_BELL, LENOVO));
                        }

                        @Override
                        public PostLedger loadPostLedger() {
                            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                        }
                    };
                if ( StockApi.class.equals(clazz) ) return (T)new StockApi() {
                        @Override
                        public List<PicoStock> findAllStocks() {
                            return Arrays.asList(new PicoStock(1, "Hamburg"), new PicoStock(2, "Bremen"));
                        }
                    };
                return null;
            }
            //</editor-fold>
        });

        Dl.local().add(Guardian.class, new AbstractGuardian() {
            @Override
            public void login(String user, char[] pass) throws AuthenticationException {
                if ( "test".equalsIgnoreCase(user) && "test".equals(String.valueOf(pass)) ) {
                    setRights(new Operator(user, 123, Collections.emptyList()));
                    return;
                } // success
                if ( "admin".equalsIgnoreCase(user) && "admin".equals(String.valueOf(pass)) ) {
                    setRights(new Operator(user, 666, Arrays.asList(AtomicRight.values())));
                    return;
                } // success
                throw new AuthenticationException("User or Pass wrong");
            }
        });

        // Initialize the Container
        SeContainerInitializer ci = SeContainerInitializer.newInstance();
        ci.disableDiscovery();
        ci.addPackages(true, MainCdi.class);
        ci.addPackages(true, Main.class);
        ci.addPackages(true, CustomerTaskService.class); // customer.ui
        ci.addPackages(true, SendResellerListToSubscribedCustomersMenuItem.class); // mail.ui
        ci.addPackages(true, PriceBlockerViewCask.class); // price.ui
        ci.addPackages(true, UiUnitSupport.class); // receipt.ui
        ci.addPackages(true, RawReportView.class); // report.ui
        ci.addPackages(true, UiPersona.class); // rights.ui
        ci.addPackages(true, StockUpiImpl.class); // stock.ui
        ci.addPackages(true, ProductTask.class); // uniqueunit.ui
        ci.addPackages(true, ReactivePicoUnitDetailViewCask.class); // redtapext.ui
        ci.addPackages(true, AboutController.class); // misc.ui
        ci.addPackages(true, SearchCask.class); // search.ui
        ci.addPackages(LoggerProducer.class); // core.system. autolog
        ci.addPackages(GlobalConfig.class); // Global Config produces.
        container = ci.initialize();
        // TODO: Remote connection and everything else.
        instance = container.getBeanManager().createInstance();

        // TODO: Here we will have Saft already.
        FXMLLoader mainLoader = instance.select(FxmlLoaderInitializer.class).get().createLoader(ClientMainController.class.getResource("ClientMainView.fxml"));
        try {
            mainLoader.load();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        mainView = mainLoader.getRoot();

        ClientMainController mainController = mainLoader.getController();
    }

}
