package eu.ggnet.dwoss.redtape.reporting;

import java.awt.event.ActionEvent;
import java.util.concurrent.ExecutionException;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import org.openide.util.Lookup;

import eu.ggnet.saft.core.Workspace;
import eu.ggnet.saft.core.authorisation.Guardian;

import eu.ggnet.dwoss.redtape.reporting.RedTapeCloser;

import eu.ggnet.saft.core.authorisation.AccessableAction;

import eu.ggnet.dwoss.common.ExceptionUtil;

import static eu.ggnet.saft.core.Client.lookup;
import static eu.ggnet.dwoss.rights.api.AtomicRight.EXECUTE_MANUAL_CLOSING;

/**
 * Closes the last Week.
 *
 * @author oliver.guenther
 */
public class LastWeekCloseAction extends AccessableAction {

    public LastWeekCloseAction() {
        super(EXECUTE_MANUAL_CLOSING);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final RedTapeCloser closer = lookup(RedTapeCloser.class);
        if ( JOptionPane.YES_OPTION != JOptionPane.showConfirmDialog(lookup(Workspace.class).getMainFrame(), "Möchten Sie den manuellen Wochen/Tagesabschluss durchführen ?",
                "Wochen-/Tagesabschluss", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) ) return;
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                closer.executeManual(Lookup.getDefault().lookup(Guardian.class).getUsername());
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                } catch (InterruptedException | ExecutionException e) {
                    ExceptionUtil.show(lookup(Workspace.class).getMainFrame(), e);
                }
            }
        }.execute();

    }
}
