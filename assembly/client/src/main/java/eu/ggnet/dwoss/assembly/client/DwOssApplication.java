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
package eu.ggnet.dwoss.assembly.client;

import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.input.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.assembly.client.support.*;
import eu.ggnet.dwoss.assembly.client.support.exception.*;
import eu.ggnet.dwoss.assembly.client.support.executor.ExecutorManager;
import eu.ggnet.dwoss.assembly.client.support.login.*;
import eu.ggnet.dwoss.assembly.client.support.monitor.MonitorManager;
import eu.ggnet.dwoss.assembly.remote.cdi.FxmlLoaderInitializer;
import eu.ggnet.dwoss.core.common.UserInfoException;
import eu.ggnet.dwoss.core.widget.Dl;
import eu.ggnet.dwoss.core.widget.auth.Guardian;
import eu.ggnet.dwoss.core.widget.dl.RemoteLookup;
import eu.ggnet.dwoss.mandator.spi.CachedMandators;
import eu.ggnet.saft.core.Ui;
import eu.ggnet.saft.core.UiCore;
import eu.ggnet.saft.core.ui.*;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

/**
 * The DwOssApplication.
 * The Application is designed to allow user interaction asap. To reach this goal, the login dialog is displayed and active befor any remote connections, the
 * client cdi server, the main ui or the global keys are initialized and activated.
 * <p>
 * For the detailed startup see the documentation of {@link #start(javafx.stage.Stage) }.
 * </p>
 *
 * @author oliver.guenther
 */
public class DwOssApplication extends Application {

    private SeContainer container;

    private Instance<Object> instance;

    private final static Logger L = LoggerFactory.getLogger(DwOssApplication.class);

    /**
     * Starting the Deutsche Warenwirtschaft Open Source application.
     * The startup happens through the following steps:
     * <ol>
     * <li>Parameter validation via <a href="https://jcommander.org/">JCommander</a>. The required and optional parametes are defined here
     * {@link ConnectionParameter}.</li>
     * <li>Setting the LAF for Swing components via {@link LafMenuManager#loadAndSetUserLaf() }</li>
     * <li>Displaying a placeholder main pane and the blocking login screen.
     * <ul>
     * <li>The login screen has no authentification system on visibility but the user can already type in his username and password.</li>
     * <li>The guardian is set later and only then an authentification will happen, even if the user has pressed return before.</li>
     * <li>A red/green circle in the ui displays the status of the guardian (red meaning not yet set).</li>
     * </ul>
     * </li>
     * <li>Initialize the client backend, remote connections, saft core, and preparing the main view by wrapping the javafx pane in a swing frame.
     * <ul>
     * <li>{@link #initSeContainer() }</li>
     * <li>{@link #initGlobalExceptionHandling() }</li>
     * <li>{@link #initRemoteConnection(eu.ggnet.dwoss.assembly.client.support.ConnectionParameter) }</li>
     * <li>{@link #initMainPane() }</li>
     * <li>{@link #startSaftInitMainFrameAndWrapMainPane(javax.swing.JFrame, javafx.scene.layout.Pane, eu.ggnet.dwoss.assembly.client.support.ConnectionParameter)
     * }</li>
     * </ul>
     * </li>
     * <li>Activating the authentification in the login screen by calling {@link LoginScreenController#setAndActivateGuardian(eu.ggnet.saft.experimental.auth.Guardian)
     * with the remote guardian</li>
     * <li>Start the polling of server progress {@link MonitorManager#startPolling() }</li>
     * <li>Initialize the session timeout and global keys for manual logout and relocation of windows. Ctrl+Shift+L for manual logout and Ctrl+Shift+R for
     * relocation of all windows</li>
     * </ol>
     *
     * @param primaryStage primaryStage of application.
     * @throws Exception all possible exeptions.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            // Parameter discovery is done here, cause a error will be shown via Ui.
            ConnectionParameter cp = new ConnectionParameter();
            JCommander.newBuilder().addObject(cp).programName(DwOssMain.class.getName()).build().parse(getParameters().getRaw().toArray(new String[]{}));
            L.debug("start() parameters discovered: {}", cp);

            LafMenuManager.loadAndSetUserLaf();
            createAndShowMainPane(primaryStage);

            // Creating the Swing MainFrame as long as we are in the Swing mode.
            JFrame mainFrame = new JFrame();
            LoginScreenController firstLoginScreen = createAndShowFirstLoginScreen(primaryStage, mainFrame);

            CompletableFuture
                    .runAsync(() -> {
                        container = initSeContainer();
                        instance = container.getBeanManager().createInstance();
                    })
                    .thenRun(() -> initGlobalExceptionHandling())
                    .thenRun(() -> initRemoteConnection(cp))
                    .thenApply(v -> initMainPane())
                    .thenAcceptAsync(mainView -> startSaftInitMainFrameAndWrapMainPane(mainFrame, mainView, cp), java.awt.EventQueue::invokeLater)
                    .thenRunAsync(() -> firstLoginScreen.setAndActivateGuardian(Dl.local().lookup(Guardian.class)))
                    .thenRun(() -> instance.select(MonitorManager.class).get().startPolling())
                    .thenRun(() -> initSessionTimeoutAndManualLogoutKeys())
                    .thenRun(() -> initRelocationKeys())
                    .handle((v, ex) -> {
                        new DwFinalExceptionConsumer(null).accept(ex);
                        return null;
                    });
        } catch (ParameterException e) {
            // Sout is here correct.
            System.out.println(e.getMessage());
            System.out.println();
            e.getJCommander().usage();

            Alert a = new Alert(javafx.scene.control.Alert.AlertType.ERROR);
            a.setTitle("Fehler beim Start");
            a.setHeaderText("Fehler bei Start.");
            a.setContentText("Es ist ein Fehler beim Start aufgetreten. Bitte Console und/oder Logs prüfen.");
            a.show();
        }
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
     * Creates the main pane, containing nothing but an info text and displaying it on the supplied stage.
     * Used as placeholder, which can be filled with content in the post phase.
     * It will become more usefull in the jpro version.
     *
     * @param primaryStage the stage to use and show
     * @return the create visible pane.
     */
    private Pane createAndShowMainPane(Stage primaryStage) {
        L.debug("createAndShowMainPane() called");
        Label info = new Label("Starting the Main Application");
        StackPane mainPane = new StackPane(info);
        mainPane.setPrefSize(800, 600);

        Scene s = new Scene(mainPane);
        primaryStage.setScene(s);
        primaryStage.show();
        return mainPane;
    }

    /**
     * Creates and shows the LoginScreen in a very special first mode.
     * In this phase, no cdi or saft is available. So the login screen is
     * created with native fx calls. It is application modal, blocking the supplied
     * owner.
     * <ul>
     * <li>On a successful authentication, it will close the itself and the owner stage and show the supplied mainFrame.</li>
     * <li>On a cancel close, the hole application will be shutdown.</li>
     * </ul>
     * After the creation of the controller, no connection to any authentication system has happend.
     * A call to {@link LoginScreenController#setAndActivateGuardian(eu.ggnet.saft.experimental.auth.Guardian) } is needed later.
     *
     * @param owner     the owner of the dialog, normaly the main pane.
     * @param mainFrame a swing frame to be displayed.
     * @return the created and visible controller of the screen.
     */
    private LoginScreenController createAndShowFirstLoginScreen(Stage owner, JFrame mainFrame) {
        FXMLLoader loader = new FXMLLoader(LoginScreenController.class.getResource("LoginScreenView.fxml"));
        Parent loginView;
        try {
            loginView = loader.load();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        LoginScreenController loginController = loader.getController();

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
                    instance.select(LoggedInTimeoutManager.class).get().startTime();
                })
                // This is a very special case, because many things are happening in background, this is the only simple way out. UiCore.shutdown() does not work here.
                .onCancel(() -> System.exit(0)).build()
        );

        return loginController;
    }

    /**
     * Initializes the CDI SeContainer based on the global {@link ContainerConfiguration}.
     * The auto discovery is disables, because jars of the server side are visible for the container and
     * that would break everything.
     */
    private SeContainer initSeContainer() {
        ContainerConfiguration cc = ContainerConfiguration.instance();
        SeContainerInitializer ci = SeContainerInitializer.newInstance();
        ci.addPackages(cc.packages());
        ci.addPackages(true, cc.fullPackages());
        ci.disableDiscovery();
        return ci.initialize();
    }

    /**
     * Sets all the Exception consumers in the system and in saft.
     */
    private void initGlobalExceptionHandling() {
        java.awt.Toolkit.getDefaultToolkit().getSystemEventQueue().push(new UnhandledExceptionCatcher());
        UiCore.overwriteFinalExceptionConsumer(new DwFinalExceptionConsumer(() -> Dl.local().lookup(CachedMandators.class).loadMandator().bugMail()));
        UiCore.registerExceptionConsumer(UserInfoException.class, new UserInfoExceptionConsumer());
        UiCore.registerExceptionConsumer(ConstraintViolationException.class, new ConstraintViolationConsumer());
        Thread.setDefaultUncaughtExceptionHandler((Thread t, Throwable e) -> {
            L.warn("Exception occured on {}", t, e);
            Ui.handle(e);
        });
    }

    /**
     * Initializes the remote connection, but only if the parameter --diableRemote was not set.
     * See {@link ConnectionParameter} and {@link DwOssMain}.
     *
     * @param cp the connection parameter.
     */
    private void initRemoteConnection(ConnectionParameter cp) {
        if ( !cp.disableRemote() ) {
            L.debug("initRemoteConnection() with {}", cp);
            Dl.local().add(RemoteLookup.class, new WildflyLookup(cp));
        } else {
            L.warn("initRemoteConnection() disabled. ConnectionParameter --disableRemote was set.");
        }
    }

    /**
     * Initializes and creates the main view.
     *
     * @see DwOssClientController
     *
     * @return the created pane.
     */
    private Pane initMainPane() {
        // TODO: If Saft uses CDI, we can start unsing it here.
        FXMLLoader mainLoader = instance.select(FxmlLoaderInitializer.class).get().createLoader(DwOssClientController.class.getResource("DwOssClientView.fxml"));
        try {
            mainLoader.load();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        return mainLoader.getRoot();
    }

    /**
     * Uses the supplied swing frame to wrap the supplied pane and initializes it with title, icon, location and close listener.
     *
     * @param mainFrame the outer frame
     * @param mainPane  the pane to wrap
     * @param cp        parameters for the title
     */
    private void startSaftInitMainFrameAndWrapMainPane(JFrame mainFrame, Pane mainPane, ConnectionParameter cp) {
        JFXPanel p = new JFXPanel();
        p.setScene(new Scene(mainPane));
        mainFrame.getContentPane().add(p);
        // Todo: Store and Load location
        mainFrame.setSize(800, 600);
        mainFrame.setLocationByPlatform(true);
        mainFrame.setTitle(Dl.local().lookup(CachedMandators.class).loadMandator().company().name()
                + " - Deutsche Warenwirtschaft - "
                + cp.toUrl());
        mainFrame.setIconImage(new ImageIcon(DwOssClientController.loadIcon()).getImage());

        Dl.local().add(UserPreferences.class, new UserPreferencesJdk()); // Hard added here.
        UiCore.continueSwing(mainFrame);
        mainFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Dl.local().lookup(UserPreferences.class).storeLocation(DwOssApplication.class, mainFrame);
                UiCore.shutdown(); // Todo: Saft does that on closed, but closed is never called.
            }

        });
        Dl.local().lookup(UserPreferences.class).loadLocation(DwOssApplication.class, mainFrame);

        /*
         // If we switch to saft javafx, use this. And uns Platform::runLater
         mainPane.getChildren().clear(); // remove everything
         mainPane.getChildren().add(mainView);
         */
        // Old Ops usage, RedTape Ui Contextmenu
        // Ops.registerActionFactory(new ConsumerFactoryOfStockTransactions());
    }

    /**
     * Initialize the relocation function for windows with the key combination Ctrl+Shift+R.
     */
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
     * Initializes and activates the session timout and the manual logout via Ctrl+Shift+L.
     * The session timout and the if it's activate is discovered in the user storage.
     * The manual logout ist done via Ctrl + Shift + L and registered.
     */
    private void initSessionTimeoutAndManualLogoutKeys() {
        // Global login/logout handler
        LoggedInTimeoutManager loggedInTimeout = instance.select(LoggedInTimeoutManager.class).get();

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
