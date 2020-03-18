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

import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.se.SeContainer;
import javax.enterprise.inject.se.SeContainerInitializer;
import javax.persistence.LockModeType;
import javax.swing.JFrame;
import javax.validation.ConstraintViolationException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.assembly.client.Main;
import eu.ggnet.dwoss.assembly.client.support.login.*;
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
import eu.ggnet.saft.core.ui.SwingCore;
import eu.ggnet.saft.core.ui.UserPreferences;
import eu.ggnet.saft.experimental.auth.AuthenticationException;
import eu.ggnet.saft.experimental.auth.Guardian;

import static eu.ggnet.dwoss.core.common.values.tradename.TradeName.*;

/**
 *
 * @author oliver.guenther
 */
public class ClientApplication extends Application {

    private SeContainer container;

    private Label info;

    private Pane mainView;

    private JFrame mainFrame;

    private Instance<Object> instance;

    private final static Logger L = LoggerFactory.getLogger(ClientApplication.class);

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
        LoginScreenController firstLoginScreen = createAndShowFirstLoginScreen(primaryStage);

        mainFrame = new JFrame("Todo: Fillme with mandator and database connection");

        CompletableFuture
                .runAsync(this::postInit)
                .thenRunAsync(() -> {
                    JFXPanel p = new JFXPanel();
                    p.setScene(new Scene(mainView));
                    mainFrame.getContentPane().add(p);
                    // Todo: Store and Load location
                    mainFrame.setSize(800, 600);
                    mainFrame.setLocationByPlatform(true);

                    UiCore.continueSwing(mainFrame);
                    mainFrame.addWindowListener(new WindowAdapter() {
                        @Override
                        public void windowClosing(WindowEvent e) {
                            Dl.local().lookup(UserPreferences.class).storeLocation(ClientApplication.class, mainFrame);
                            UiCore.shutdown(); // Todo: Saft does that on closed, but closed is never called.
                        }

                    });
                    Dl.local().lookup(UserPreferences.class).loadLocation(ClientApplication.class, mainFrame);

                    /*
                    // If we switch to saft javafx, use this. And uns Platform::runLater
                    mainPane.getChildren().clear(); // remove everything
                    mainPane.getChildren().add(mainView);
                     */
                }, java.awt.EventQueue::invokeLater)
                .thenRunAsync(() -> {

                    // Init complete, setting the guardian.
                    firstLoginScreen.setAndActivateGuardian(Dl.local().lookup(Guardian.class));

                    initSessionTimeoutAndManualLogout();
                    initRelocationKeys();

                })
                .handle((Void t, Throwable u) -> {
                    // Manual call in init
                    new DwFinalExceptionConsumer(null).accept(u);
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
    private void postInit() {
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

        // Setting the Exception Handler
        java.awt.Toolkit.getDefaultToolkit().getSystemEventQueue().push(new UnhandledExceptionCatcher());
        // Todo: Later:bugmail =  Dl.local().lookup(CachedMandators.class).loadMandator().bugMail()
        UiCore.overwriteFinalExceptionConsumer(new DwFinalExceptionConsumer(null));
        UiCore.registerExceptionConsumer(UserInfoException.class, new UserInfoExceptionConsumer());
        UiCore.registerExceptionConsumer(ConstraintViolationException.class, new ConstraintViolationConsumer());

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

    private LoginScreenController createAndShowFirstLoginScreen(Stage owner) {
        FXMLLoader loader = new FXMLLoader(LoginScreenController.class.getResource("LoginScreenView.fxml"));
        Parent loginView;
        try {
            loginView = loader.load();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        LoginScreenController loginController = loader.getController();
        // Manual FX, will be different in manual swing.
        Stage loginStage = new Stage();
        loginStage.initModality(Modality.APPLICATION_MODAL);
        loginStage.initOwner(owner);
        loginStage.setScene(new Scene(loginView));
        loginStage.setOnCloseRequest(e -> loginController.closed());
        loginStage.show();

        loginController.accept(new LoginScreenConfiguration.Builder()
                .onSuccess(p -> {
                    java.awt.EventQueue.invokeLater(() -> mainFrame.setVisible(true));
                    Platform.runLater(() -> {
                        loginStage.close();
                        owner.close();
                    });

                    // If saft in JavaFx mode, this is enought
                    // Platform.runLater(() -> loginStage.close());
                    // Restart the timer. Instance will not be null at that time.
                    instance.select(LoggedInTimeout.class).get().startTime();
                })
                .onCancel(() -> UiCore.shutdown()).build()
        );

        return loginController;
    }

    private void initRelocationKeys() {

        // TODO: JavaFx mode fehlt. Und festelltung ob wir in JavaFX sind.
        java.awt.KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher((java.awt.event.KeyEvent e) -> {
            if ( e.getID() == java.awt.event.KeyEvent.KEY_PRESSED && e.isControlDown() && e.isShiftDown() && e.getKeyCode() == java.awt.event.KeyEvent.VK_R ) {
                L.info("KeyEvent[Ctrl+Shift+L] detected");
                int i = 20;

                Window m = UiCore.getMainFrame();
                L.debug("KeyEvent[Ctrl+Shift+L] relocating MainFrame {}", m);
                m.setSize(800, 600);
                m.setLocation(i, i);
                i = i + 20;

                for (Iterator<java.awt.Window> iterator = SwingCore.ACTIVE_WINDOWS.values().stream().map(w -> w.get()).filter(w -> w != null).iterator();
                        iterator.hasNext();) {
                    Window w = iterator.next();
                    L.debug("KeyEvent[Ctrl+Shift+L] relocating {}", w);
                    w.setSize(800, 600);
                    i = i + 20;
                }
                // Todo: implement a global clear.
                // Dl.local().lookup(UserPreferences.class).isReset();
            }
            return false;
        });

    }

    /**
     * Initializes and activates the session timout and the global Keyhandler.
     * The session timout and the if it's activate is discovered in the user storage.
     * The manual logout ist done via Ctrl + Shift + L and registered.
     */
    private void initSessionTimeoutAndManualLogout() {
        // Global login/logout handler
        LoggedInTimeout loggedInTimeout = instance.select(LoggedInTimeout.class).get();

        // Swing: Ctrl + Shift + L
        java.awt.KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher((java.awt.event.KeyEvent e) -> {
            if ( e.getID() == java.awt.event.KeyEvent.KEY_PRESSED && e.isControlDown() && e.isShiftDown() && e.getKeyCode() == java.awt.event.KeyEvent.VK_L ) {
                loggedInTimeout.manualTimeout();
            }
            return false;
        });

        // Swing: Session Timout activity detector
        java.awt.Toolkit.getDefaultToolkit().addAWTEventListener((java.awt.AWTEvent event) -> loggedInTimeout.resetTime(),
                java.awt.AWTEvent.MOUSE_MOTION_EVENT_MASK | java.awt.AWTEvent.MOUSE_EVENT_MASK | java.awt.AWTEvent.KEY_EVENT_MASK
        );

        // JavaFx: Ctrl + Shift + L global keylistener.
        KeyCombination keysCtrlShiftL = new KeyCodeCombination(KeyCode.L, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN);
        addEventFilter(KeyEvent.KEY_RELEASED, k -> {
            if ( keysCtrlShiftL.match(k) ) {
                loggedInTimeout.manualTimeout();
            }
        });
        // JavaFx: Session Timeout activity detector
        addEventHandler(EventType.ROOT, (e) -> loggedInTimeout.resetTime());

        // TODO: Load timeout and aktivation from storage. Dont start the timeout here. Change Method on the other side.
        /*
        loggedInTimeout.setTimeoutAndStartTime(null);
        loggedInTimeout.setTimeoutStore(t -> System.out.println("Todo: Store Timechange " + t));
         */
    }

    private <T extends javafx.event.Event> void addEventFilter(
            final EventType<T> eventType,
            final EventHandler<? super T> eventFilter) {
        // TODO: Merge into saft an propagate to every stage
        // Stage s ...;
        // s.addEventFilter(....
    }

    private <T extends javafx.event.Event> void addEventHandler(
            final EventType<T> eventType,
            final EventHandler<? super T> eventHandler) {
        // TODO: Merge into saft an propagate to every stage
        // Stage s ...;
        // s.addEventHandler(....

    }
}
