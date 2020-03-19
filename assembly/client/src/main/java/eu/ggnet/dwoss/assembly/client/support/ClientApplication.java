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
import java.net.URL;
import java.util.Iterator;
import java.util.concurrent.CompletableFuture;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.se.SeContainer;
import javax.enterprise.inject.se.SeContainerInitializer;
import javax.swing.ImageIcon;
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

import eu.ggnet.dwoss.assembly.client.DwOssMain;
import eu.ggnet.dwoss.assembly.client.support.login.*;
import eu.ggnet.dwoss.assembly.remote.MainCdi;
import eu.ggnet.dwoss.assembly.remote.cdi.FxmlLoaderInitializer;
import eu.ggnet.dwoss.assembly.remote.exception.*;
import eu.ggnet.dwoss.core.common.UserInfoException;
import eu.ggnet.dwoss.core.system.GlobalConfig;
import eu.ggnet.dwoss.core.system.autolog.LoggerProducer;
import eu.ggnet.dwoss.customer.ui.CustomerTaskService;
import eu.ggnet.dwoss.mail.ui.cap.SendResellerListToSubscribedCustomersMenuItem;
import eu.ggnet.dwoss.mandator.spi.CachedMandators;
import eu.ggnet.dwoss.misc.ui.AboutController;
import eu.ggnet.dwoss.price.ui.PriceBlockerViewCask;
import eu.ggnet.dwoss.receipt.ui.UiUnitSupport;
import eu.ggnet.dwoss.redtapext.ui.ReactivePicoUnitDetailViewCask;
import eu.ggnet.dwoss.report.ui.RawReportView;
import eu.ggnet.dwoss.rights.ui.UiPersona;
import eu.ggnet.dwoss.search.ui.SearchCask;
import eu.ggnet.dwoss.stock.ui.StockUpiImpl;
import eu.ggnet.dwoss.uniqueunit.ui.ProductTask;
import eu.ggnet.saft.core.Dl;
import eu.ggnet.saft.core.UiCore;
import eu.ggnet.saft.core.dl.RemoteLookup;
import eu.ggnet.saft.core.ui.*;
import eu.ggnet.saft.experimental.auth.Guardian;

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
                    mainFrame.setTitle(discoverTitle());
                    mainFrame.setIconImage(new ImageIcon(loadIcon()).getImage());

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
        ContainerConfiguration cc = ContainerConfiguration.instance();
        cc.addPackages(true, MainCdi.class);
        cc.addPackages(true, DwOssMain.class);
        cc.addPackages(true, CustomerTaskService.class); // customer.ui
        cc.addPackages(true, SendResellerListToSubscribedCustomersMenuItem.class); // mail.ui
        cc.addPackages(true, PriceBlockerViewCask.class); // price.ui
        cc.addPackages(true, UiUnitSupport.class); // receipt.ui
        cc.addPackages(true, RawReportView.class); // report.ui
        cc.addPackages(true, UiPersona.class); // rights.ui
        cc.addPackages(true, StockUpiImpl.class); // stock.ui
        cc.addPackages(true, ProductTask.class); // uniqueunit.ui
        cc.addPackages(true, ReactivePicoUnitDetailViewCask.class); // redtapext.ui
        cc.addPackages(true, AboutController.class); // misc.ui
        cc.addPackages(true, SearchCask.class); // search.ui
        cc.addPackages(LoggerProducer.class); // core.system. autolog
        cc.addPackages(GlobalConfig.class); // Global Config produces.

        // Initialize the Container
        SeContainerInitializer ci = SeContainerInitializer.newInstance();
        ci.addPackages(cc.packages());
        ci.addPackages(true, cc.fullPackages());
        ci.disableDiscovery();
        container = ci.initialize();
        // TODO: Remote connection and everything else.
        instance = container.getBeanManager().createInstance();

        // Setting the Exception Handler
        java.awt.Toolkit.getDefaultToolkit().getSystemEventQueue().push(new UnhandledExceptionCatcher());
        // Todo: Later:bugmail =  Dl.local().lookup(CachedMandators.class).loadMandator().bugMail()
        UiCore.overwriteFinalExceptionConsumer(new DwFinalExceptionConsumer(null));
        UiCore.registerExceptionConsumer(UserInfoException.class, new UserInfoExceptionConsumer());
        UiCore.registerExceptionConsumer(ConstraintViolationException.class, new ConstraintViolationConsumer());

        if ( !ApplicationConfiguration.instance().connectionParameter().disableRemote() ) // So the tryout can use everything of the global main.
            Dl.local().add(RemoteLookup.class, new WildflyLookup(ApplicationConfiguration.instance().connectionParameter()));

        // TODO: If Saft uses CDI, we can start unsing it here.
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

        // Swing: Ctrl + Shift + R
        java.awt.KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher((java.awt.event.KeyEvent e) -> {
            if ( e.getID() == java.awt.event.KeyEvent.KEY_PRESSED && e.isControlDown() && e.isShiftDown() && e.getKeyCode() == java.awt.event.KeyEvent.VK_R ) {
                L.info("KeyEvent[Ctrl+Shift+R] detected");
                if ( UiCore.isSwing() ) relocateWindowsInSwingMode();
                if ( UiCore.isFx() ) relocateWindowsInJavaFxMode();
            }
            return false;
        });

        // JavaFx: Ctrl + Shift + R
        KeyCombination keysCtrlShiftR = new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN);
        addEventFilter(KeyEvent.KEY_PRESSED, k -> {
            if ( keysCtrlShiftR.match(k) ) {
                L.info("KeyEvent[Ctrl+Shift+R] detected");
                if ( UiCore.isSwing() ) relocateWindowsInSwingMode();
                if ( UiCore.isFx() ) relocateWindowsInJavaFxMode();
            }
        });

    }

    /**
     * Discover the Title of the DW.
     *
     * @return the title
     */
    private String discoverTitle() {
        return Dl.local().lookup(CachedMandators.class).loadMandator().company().name()
                + " - Deutsche Warenwirtschaft - "
                + ApplicationConfiguration.instance().connectionParameter().toUrl();
    }

    private void relocateWindowsInSwingMode() {
        int i = 20;

        Window m = UiCore.getMainFrame();
        L.debug("relocateWindowsInSwingMode() relocating MainFrame {}", m);
        m.setSize(800, 600);
        m.setLocation(i, i);
        i = i + 20;

        for (Iterator<java.awt.Window> iterator = SwingCore.ACTIVE_WINDOWS.values().stream().map(w -> w.get()).filter(w -> w != null).iterator();
                iterator.hasNext();) {
            Window w = iterator.next();
            L.debug("relocateWindowsInSwingMode() relocating {}", w);
            w.setSize(800, 600);
            w.setLocation(i, i);
            i = i + 20;
        }
        // Todo: implement a global clear.
        // Dl.local().lookup(UserPreferences.class).isReset();

    }

    private void relocateWindowsInJavaFxMode() {
        // INFO: Untested.
        int i = 20;
        Stage m = UiCore.getMainStage();
        L.debug("relocateWindowsInJavaFxMode() relocating MainWindow {}", m);
        m.setX(i);
        m.setY(i);
        m.setWidth(800);
        m.setHeight(600);

        i = i + 20;

        for (Iterator<Stage> iterator = FxCore.ACTIVE_STAGES.values().stream().map(w -> w.get()).filter(w -> w != null).iterator();
                iterator.hasNext();) {
            Stage w = iterator.next();
            L.debug("relocateWindowsInJavaFxMode() relocating {}", w);
            w.setX(i);
            w.setY(i);
            w.setWidth(800);
            w.setHeight(600);
            i = i + 20;
        }
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
        addEventFilter(KeyEvent.KEY_PRESSED, k -> {
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

    static URL loadIcon() {
        return ClientApplication.class.getResource("app-icon3.png"); // NOI18N
    }

}
