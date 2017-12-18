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
package eu.ggnet.dwoss.assembly.remote;

import eu.ggnet.saft.UiAlert;
import eu.ggnet.saft.Client;
import eu.ggnet.saft.UiCore;
import eu.ggnet.dwoss.util.EjbConnectionConfiguration;

import java.awt.EventQueue;
import java.awt.Toolkit;
import java.util.Map;

import javax.validation.ConstraintViolationException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.assembly.remote.lookup.*;
import eu.ggnet.dwoss.common.exception.*;
import eu.ggnet.dwoss.mandator.MandatorSupporter;
import eu.ggnet.dwoss.mandator.api.value.Mandator;
import eu.ggnet.dwoss.report.entity.ReportLine;
import eu.ggnet.dwoss.report.ui.returns.Summary;
import eu.ggnet.dwoss.util.MetawidgetConfig;
import eu.ggnet.dwoss.util.UserInfoException;
import eu.ggnet.saft.runtime.SwingClient;

import static eu.ggnet.saft.Client.lookup;
import static eu.ggnet.saft.core.ui.UiAlertBuilder.Type.ERROR;

/**
 * JavaFx entry Point.
 * <p/>
 * @import static eu.ggnet.saft.core.UiAlert.Type.ERROR;
 * author oliver.guenther
 */
public class RunClientFx extends Application {

    private SwingClient swingClient;

    /**
     * Optional errorstring from init, to be inspecte in start.
     */
    private String error = null;

    private EjbConnectionConfiguration lookupConfig;

    public static void main(String[] args) {
        System.out.println("Java main");
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        LoggerFactory.getLogger(RunClientFx.class).info("JavaFx start");
        System.out.println("JavaFx start");

        if ( error != null ) {
            UiAlert.title("Fehler im Init")
                    .nl("Fehler in der Initialisierung oder Verbindung")
                    .nl("Später noch mal probieren oder Technik kontaktieren")
                    .nl()
                    .nl(error)
                    .show(ERROR);
            return;
        }

        /*
        The SwingClient is a little bit ugly.
        The result is, that we must init it on the EventQueue and we can not use the supplied fx stage.
         */
        Platform.setImplicitExit(false);
        EventQueue.invokeLater(() -> {
            swingClient.show(
                    "(Remote," + lookupConfig.getHost() + ":" + lookupConfig.getPort() + ") - Mandant:"
                    + lookup(MandatorSupporter.class).loadMandator().getCompany().getName(), getParameters());
        });

    }

    @Override
    public void init() throws Exception {
        LoggerFactory.getLogger(RunClientFx.class).info("JavaFx init");
        System.out.println("JavaFx init");

        String key = discoverConfigParameters();
        if ( !Configurations.containsConfig(key) ) usage(key); // Go to default, but inform on console abaout usage.
        lookupConfig = Configurations.getConfigOrDefault(key);
        Client.setRemoteLookup(new WildflyLookup(lookupConfig));

        Toolkit.getDefaultToolkit().getSystemEventQueue().push(new UnhandledExceptionCatcher());

        MetawidgetConfig.enhancedMetawidget(ReportLine.class, Mandator.class, Summary.class);

        UiCore.overwriteFinalExceptionConsumer(new DwFinalExceptionConsumer());
        UiCore.registerExceptionConsumer(UserInfoException.class, new UserInfoExceptionConsumer());
        UiCore.registerExceptionConsumer(ConstraintViolationException.class, new ConstraintViolationConsumer());

        Client.enableCache(MandatorSupporter.class);

        verifyRemoteConnection();

        EventQueue.invokeAndWait(() -> {
            swingClient = new SwingClient() {
                @Override
                protected void close() {
                    System.out.println("Calling Close");
                    Platform.exit();
                    System.exit(0); // Again, not perfect, but otherwise the application dosen't close and I don't know why.
                }
            };
            swingClient.init();
        });
    }

    @Override
    public void stop() throws Exception {
        System.out.println("JavaFx stop");
    }

    /**
     * Discovers the config from parameters including some convertions of old configs.
     *
     * @return the config key or inf none given null;
     */
    private String discoverConfigParameters() {
        LoggerFactory.getLogger(RunClientFx.class).debug("JavaFx parameters {}", getParameters().getRaw());
        Map<String, String> p = getParameters().getNamed();
        String key = p.get("config"); // may return null
        // Optional: Look for old Parameters
        if ( key == null ) key = "http://retrax.ahrensburg.gg-net.de:9080/tomee/ejb".equals(p.get("--url")) ? "elus" : null; // old direkt tomee way
        if ( key == null ) key = p.get("mandator"); // old key
        return key;
    }

    private void usage(String givenKey) {
        String usage = "Called without parameter or wrong parameter, using default config.\n"
                + "Usage: --config=key\n"
                + "Values: " + Configurations.toInfo() + "\n"
                + "Used key was: " + givenKey;
        System.out.println(usage);
    }

    @SuppressWarnings({"CallToPrintStackTrace", "UseSpecificCatch"})
    private void verifyRemoteConnection() {
        try {
            // Try to load the Mandator.
            Client.lookup(MandatorSupporter.class).loadMandator();
        } catch (Exception e) {
            error = e.getMessage() + " thrown by " + e.getClass().getSimpleName();
            LoggerFactory.getLogger(RunClientFx.class).error("Exception on remote connection test.", e);
            e.printStackTrace();
        }
    }
}
