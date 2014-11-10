package eu.ggnet.dwoss.redtape.reporting;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import eu.ggnet.saft.core.Client;
import eu.ggnet.saft.core.Workspace;

import eu.ggnet.dwoss.redtape.DocumentSupporter;

import eu.ggnet.saft.core.authorisation.AccessableAction;

import eu.ggnet.dwoss.util.FileJacket;

import eu.ggnet.dwoss.common.ExceptionUtil;

import eu.ggnet.dwoss.util.dialog.Alert;

import static eu.ggnet.saft.core.Client.lookup;
import static eu.ggnet.dwoss.rights.api.AtomicRight.EXPORT_DOSSIER_TO_XLS;

/**
 *
 * @author oliver.guenther
 */
public class ExportDossierToXlsAction extends AccessableAction {

    public ExportDossierToXlsAction() {
        super(EXPORT_DOSSIER_TO_XLS);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final String dossierId = JOptionPane.showInputDialog(lookup(Workspace.class).getMainFrame(), "Bitte DossierId eingeben:").trim();
        new SwingWorker<FileJacket, Object>() {
            @Override
            protected FileJacket doInBackground() throws Exception {
                return lookup(DocumentSupporter.class).toXls(dossierId);
            }

            @Override
            protected void done() {
                try {
                    FileJacket fj = get();
                    if ( fj == null ) Alert.builder().title("Fehler").body("Keine Rückgabewerte").build().show();
                    else Desktop.getDesktop().open(fj.toTemporaryFile());
                } catch (InterruptedException | ExecutionException | IOException ex) {
                    ExceptionUtil.show(Client.lookup(Workspace.class).getMainFrame(), ex);
                }
            }
        }.execute();
    }
}
