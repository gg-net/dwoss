package eu.ggnet.dwoss.receipt.reporting;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.concurrent.ExecutionException;

import javax.swing.AbstractAction;
import javax.swing.SwingWorker;

import eu.ggnet.saft.core.Workspace;

import eu.ggnet.dwoss.receipt.reporting.RefurbishmentReporter;

import eu.ggnet.dwoss.util.FileJacket;

import eu.ggnet.dwoss.common.ExceptionUtil;

import static eu.ggnet.saft.core.Client.lookup;

/**
 *
 * @author pascal.perau
 */
public class ReportRefurbishmentAction extends AbstractAction {

    public ReportRefurbishmentAction() {
        super("Refurbishmentreport");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final ReportRefurbishmentDialog dialog = new ReportRefurbishmentDialog(lookup(Workspace.class).getMainFrame());
        dialog.setVisible(true);
        if ( !dialog.isOk() ) return;
        new SwingWorker<Object, Object>() {
            @Override
            protected Object doInBackground() throws Exception {
                FileJacket jacket = lookup(RefurbishmentReporter.class).toXls(dialog.getContractor(), dialog.getStart(), dialog.getEnd());
                File f = jacket.toTemporaryFile();
                Desktop.getDesktop().open(f);
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                } catch (InterruptedException | ExecutionException ex) {
                    ExceptionUtil.show(lookup(Workspace.class).getMainFrame(), ex);
                }
            }
        }.execute();
    }
}
