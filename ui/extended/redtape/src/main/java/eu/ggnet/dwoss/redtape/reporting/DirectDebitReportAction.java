package eu.ggnet.dwoss.redtape.reporting;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import javax.swing.AbstractAction;
import javax.swing.SwingWorker;

import eu.ggnet.saft.core.Client;
import eu.ggnet.saft.core.Workspace;

import eu.ggnet.dwoss.redtape.reporting.DirectDebitReporter;

import eu.ggnet.dwoss.common.ExceptionUtil;

import static eu.ggnet.saft.core.Client.lookup;

/**
 *
 * @author pascal.perau
 */
public class DirectDebitReportAction extends AbstractAction {

    public DirectDebitReportAction() {
        super("Lastschriftenreport");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        new SwingWorker<File, Object>() {
            @Override
            protected File doInBackground() throws Exception {
                return lookup(DirectDebitReporter.class).toXls().toTemporaryFile();
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
}
