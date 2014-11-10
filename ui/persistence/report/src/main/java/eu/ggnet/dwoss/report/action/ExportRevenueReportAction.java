package eu.ggnet.dwoss.report.action;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import eu.ggnet.saft.core.Client;
import eu.ggnet.saft.core.Workspace;
import eu.ggnet.saft.core.authorisation.AccessableAction;

import eu.ggnet.dwoss.report.op.RevenueReporter;

import eu.ggnet.dwoss.report.RevenueReportSelectorPane;
import eu.ggnet.dwoss.common.ExceptionUtil;

import eu.ggnet.dwoss.util.OkCancelStage;

import javafx.application.Platform;

import static eu.ggnet.saft.core.Client.lookup;
import static eu.ggnet.dwoss.rights.api.AtomicRight.EXPORT_REVENUE_REPORT;

/**
 *
 * @author pascal.perau
 */
public class ExportRevenueReportAction extends AccessableAction {

    public ExportRevenueReportAction() {
        super(EXPORT_REVENUE_REPORT);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                final RevenueReportSelectorPane selector = new RevenueReportSelectorPane();
                OkCancelStage<RevenueReportSelectorPane> stage = new OkCancelStage<>("Auswählen", selector);
                stage.showAndWait();
                if ( stage.isCancel() ) return;

                new SwingWorker<File, Object>() {
                    @Override
                    protected File doInBackground() throws Exception {
                        return lookup(RevenueReporter.class).toXls(selector.getStart(), selector.getEnd(), selector.getStep()).toTemporaryFile();
                    }

                    @Override
                    protected void done() {
                        try {
                            Desktop.getDesktop().open(get());
                        } catch (InterruptedException | ExecutionException | IOException ex) {
                            ExceptionUtil.show(Client.lookup(Workspace.class).getMainFrame(), ex);
                        }
                    }
                }.execute();
            }
        });

    }
}
