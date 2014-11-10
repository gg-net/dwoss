package eu.ggnet.dwoss.price;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.concurrent.ExecutionException;

import javax.swing.JFileChooser;
import javax.swing.SwingWorker;

import eu.ggnet.saft.core.Workspace;

import eu.ggnet.dwoss.price.Exporter;

import eu.ggnet.saft.core.authorisation.AccessableAction;

import eu.ggnet.dwoss.util.FileJacket;

import eu.ggnet.dwoss.common.ExceptionUtil;

import static eu.ggnet.saft.core.Client.lookup;
import static eu.ggnet.dwoss.rights.api.AtomicRight.IMPORT_PRICE_BY_XLS;
import static javax.swing.JOptionPane.*;

/**
 * Action to create an XLS Report of possible sales, initiated by an XLS File containing only partNo.
 * <p/>
 * @author pascal.perau
 */
public class PriceByInputFileAction extends AccessableAction {

    public PriceByInputFileAction() {
        super(IMPORT_PRICE_BY_XLS);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JFileChooser dialog = new JFileChooser();
        dialog.setFileHidingEnabled(true);
        if ( JFileChooser.APPROVE_OPTION != dialog.showOpenDialog(lookup(Workspace.class).getMainFrame()) ) {
            return;
        }
        final String fileName = dialog.getSelectedFile().getPath();
        if ( YES_OPTION == showConfirmDialog(lookup(Workspace.class).getMainFrame(),
                "Xls Datei " + fileName + " als Eingabequelle verwenden ? (erste Zeile = Überschrift, erste Spalte enthält Artikelnummern)",
                "Preise erzeugen nach Referencedaten", YES_NO_OPTION, QUESTION_MESSAGE) ) {
            new SwingWorker<Object, Object>() {
                @Override
                protected Object doInBackground() throws Exception {
                    FileJacket inFile = new FileJacket("in", "xls", new File(fileName));
                    FileJacket outFile = lookup(Exporter.class).toXlsByXls(inFile);
                    File f = outFile.toTemporaryFile();
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

}
