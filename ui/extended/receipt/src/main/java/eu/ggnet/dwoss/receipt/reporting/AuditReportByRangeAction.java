package eu.ggnet.dwoss.receipt.reporting;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import javax.swing.AbstractAction;
import javax.swing.SwingWorker;

import eu.ggnet.saft.core.Workspace;

import eu.ggnet.dwoss.receipt.reporting.AuditReporter;

import eu.ggnet.dwoss.util.FileJacket;

import eu.ggnet.dwoss.util.DateRangeChooserDialog;
import eu.ggnet.dwoss.common.ExceptionUtil;

import static eu.ggnet.saft.core.Client.lookup;

/**
 *
 * @author oliver.guenther
 */
public class AuditReportByRangeAction extends AbstractAction {

    public AuditReportByRangeAction() {
        super("Audit Report nach Datum erzeugen");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final DateRangeChooserDialog dialog = new DateRangeChooserDialog(lookup(Workspace.class).getMainFrame());
        dialog.setVisible(true);
        if ( !dialog.isOk() ) return;
        new SwingWorker<FileJacket, Object>() {
            @Override
            protected FileJacket doInBackground() throws Exception {
                return lookup(AuditReporter.class).byRange(dialog.getStart(), dialog.getEnd());
            }

            @Override
            protected void done() {
                try {
                    Desktop.getDesktop().open(get().toTemporaryFile());
                } catch (InterruptedException | ExecutionException | IOException ex) {
                    ExceptionUtil.show(lookup(Workspace.class).getMainFrame(), ex);
                }
            }
        }.execute();
    }
}
