package eu.ggnet.dwoss.assembly.local;

import java.awt.EventQueue;
import java.awt.Toolkit;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

import org.openide.util.Lookup;

import eu.ggnet.dwoss.mandator.MandatorSupporter;
import eu.ggnet.dwoss.mandator.api.value.Mandator;
import eu.ggnet.dwoss.report.entity.ReportLine;
import eu.ggnet.dwoss.report.returns.Summary;

import eu.ggnet.dwoss.util.MetawidgetConfig;

import eu.ggnet.dwoss.common.UnhandledExceptionCatcher;
import eu.ggnet.saft.core.Client;
import eu.ggnet.saft.core.Server;
import eu.ggnet.saft.runtime.SwingClient;

import static eu.ggnet.saft.core.Client.lookup;

/**
 * JavaFx entry Point.
 * <p/>
 * @author oliver.guenther
 */
public class RunClientFx extends Application {

    private SwingClient swingClient;

    public static void main(String[] args) {
        System.out.println("Starting JavaFx Local Client");
        launch(args);
    }

    @Override
    public void init() throws Exception {
        Platform.setImplicitExit(false);
        Toolkit.getDefaultToolkit().getSystemEventQueue().push(new UnhandledExceptionCatcher());
        MetawidgetConfig.enhancedMetawidget(ReportLine.class, Mandator.class, Summary.class);
        Client.enableCache(MandatorSupporter.class);
        swingClient = new SwingClient() {
            @Override
            protected void close() {
                Lookup.getDefault().lookup(Server.class).shutdown();
            }
        };
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                swingClient.init();
            }
        });
        Lookup.getDefault().lookup(Server.class).initialise();
    }

    @Override
    public void stop() throws Exception {
        // can't use that now cause of the different lifecycles.
    }

    @Override
    public void start(Stage stage) throws Exception {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                swingClient.show("(Local) - Mandant:" + lookup(MandatorSupporter.class).loadMandator().getCompany().getName(), getParameters());
            }
        });
    }
}
