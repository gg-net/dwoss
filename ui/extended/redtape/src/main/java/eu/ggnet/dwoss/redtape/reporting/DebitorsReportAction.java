package eu.ggnet.dwoss.redtape.reporting;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import eu.ggnet.saft.core.Workspace;

import eu.ggnet.dwoss.redtape.reporting.DebitorsReporter;

import eu.ggnet.saft.core.authorisation.AccessableAction;

import eu.ggnet.dwoss.util.FileJacket;

import eu.ggnet.dwoss.util.DateRangeChooserDialog;
import eu.ggnet.dwoss.common.ExceptionUtil;

import static eu.ggnet.saft.core.Client.lookup;
import static eu.ggnet.dwoss.rights.api.AtomicRight.CREATE_DEBITOR_REPORT;

/**
 *
 * @author pascal.perau
 */
public class DebitorsReportAction extends AccessableAction {

    public DebitorsReportAction() {
        super(CREATE_DEBITOR_REPORT);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final DateRangeChooserDialog question = new DateRangeChooserDialog(lookup(Workspace.class).getMainFrame());
        question.setTitle("Reportzeitraum für Debitoren");
        question.setVisible(true);
        if ( !question.isOk() ) return;
        new SwingWorker<Object, Object>() {
            @Override
            protected Object doInBackground() throws Exception {
                FileJacket jacket = lookup(DebitorsReporter.class).toXls(question.getStart(), question.getEnd());
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
