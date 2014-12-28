/*
 * Copyright (C) 2014 GG-Net GmbH - Oliver Günther
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
package eu.ggnet.dwoss.assembly.sample;

import java.awt.EventQueue;
import java.awt.Toolkit;
import java.net.*;
import java.util.Objects;

import javax.validation.ConstraintViolationException;

import javafx.application.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import org.openide.util.Lookup;

import eu.ggnet.dwoss.common.exception.*;
import eu.ggnet.dwoss.mandator.MandatorSupporter;
import eu.ggnet.dwoss.mandator.api.value.Mandator;
import eu.ggnet.dwoss.report.entity.ReportLine;
import eu.ggnet.dwoss.util.MetawidgetConfig;
import eu.ggnet.dwoss.util.UserInfoException;
import eu.ggnet.saft.core.*;
import eu.ggnet.saft.runtime.SwingClient;

import javassist.NotFoundException;

import static eu.ggnet.saft.core.Client.lookup;

/**
 * JavaFx entry Point.
 * <p>
 * import static javafx.geometry.HPos.CENTER;
 * <p>
 * import static javafx.geometry.Pos.CENTER;
 * <p/>
 * @author oliver.guenther
 */
public class RunClientFx extends Application {

    private final static String H = "Sample Client: ";

    private SwingClient swingClient;

    private static boolean main = false;

    static URL loadAppImage() {
        return RunClientFx.class.getResource("projectavatar.png");
    }

    public static void main(String[] args) throws NotFoundException, InterruptedException {
        System.out.println(H + "main()");
        main = true;
        launch(args);
    }

    @Override
    public void init() throws Exception {
        System.out.println(H + "init()" + (main ? " through main()" : " trought other lifecycle"));
        Platform.setImplicitExit(false);
        Toolkit.getDefaultToolkit().getSystemEventQueue().push(new UnhandledExceptionCatcher());
        MetawidgetConfig.enhancedMetawidget(ReportLine.class, Mandator.class);
        Client.enableCache(MandatorSupporter.class);
        Lookup.getDefault().lookup(Server.class).initialise(); // Do this here and the logger is up and running on start.

        swingClient = new SwingClient() {
            @Override
            protected void close() {
                Lookup.getDefault().lookup(Server.class).shutdown();
                Platform.exit();
                System.exit(0);
            }
        };
        EventQueue.invokeLater(() -> {
            swingClient.init();
        });
        UiCore.overwriteFinalExceptionConsumer(new DwFinalExceptionConsumer());
        UiCore.registerExceptionConsumer(UserInfoException.class, new UserInfoExceptionConsumer());
        UiCore.registerExceptionConsumer(ConstraintViolationException.class, new ConstraintViolationConsumer());
    }

    @Override
    public void stop() throws Exception {
        System.out.println(H + "stop()");
        // can't use that now cause of the different lifecycles.
    }

    @Override
    public void start(Stage stage) throws Exception {
        System.out.println(H + "start()");
//        showSuscribtion();
        FXMLLoader loader = new FXMLLoader(SubscribtionController.loadFxml());
        Pane root = (Pane)loader.load();
        SubscribtionController controller = Objects.requireNonNull(loader.getController());
        Stage newStage = new Stage();
        newStage.setTitle("Willkommen in der Testversion der DWOSS");
        newStage.setScene(new Scene(root));
        newStage.setResizable(false);
        controller.setStage(newStage);
        newStage.showAndWait();
        EventQueue.invokeLater(() -> {
            swingClient.show("(Sample) - Mandant:" + lookup(MandatorSupporter.class).loadMandator().getCompany().getName(), getParameters());
        });
    }
}
