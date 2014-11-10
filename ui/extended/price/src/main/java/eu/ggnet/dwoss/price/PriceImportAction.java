package eu.ggnet.dwoss.price;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.concurrent.ExecutionException;

import javax.swing.JFileChooser;
import javax.swing.SwingWorker;

import eu.ggnet.saft.core.Workspace;
import eu.ggnet.saft.core.authorisation.Guardian;
import eu.ggnet.saft.core.authorisation.AccessableAction;

import eu.ggnet.dwoss.price.Importer;

import eu.ggnet.dwoss.util.FileJacket;

import eu.ggnet.dwoss.common.ExceptionUtil;

import static eu.ggnet.saft.core.Client.lookup;
import static eu.ggnet.dwoss.rights.api.AtomicRight.IMPORT_PRICEMANGMENT;
import static javax.swing.JOptionPane.*;

/**
 *
 * @author pascal.perau
 */
public class PriceImportAction extends AccessableAction {

    public PriceImportAction() {
        super(IMPORT_PRICEMANGMENT);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JFileChooser dialog = new JFileChooser();
        dialog.setFileHidingEnabled(true);
        if ( JFileChooser.APPROVE_OPTION != dialog.showOpenDialog(null) ) {
            return;
        }
        final String fileName = dialog.getSelectedFile().getPath();
        if ( YES_OPTION == showConfirmDialog(lookup(Workspace.class).getMainFrame(), "PriceManagment: " + fileName + " importieren ?", "Import des PriceManagments II", YES_NO_OPTION, QUESTION_MESSAGE) ) {
            new SwingWorker<Object, Object>() {
                @Override
                protected Object doInBackground() throws Exception {
                    FileJacket inFile = new FileJacket("in", "xls", new File(fileName));
                    lookup(Importer.class).fromXls(inFile, lookup(Guardian.class).getUsername());
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
