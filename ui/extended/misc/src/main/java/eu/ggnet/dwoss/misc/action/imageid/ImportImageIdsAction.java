package eu.ggnet.dwoss.misc.action.imageid;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.concurrent.ExecutionException;

import javax.swing.JFileChooser;
import javax.swing.SwingWorker;

import eu.ggnet.saft.core.Workspace;

import eu.ggnet.dwoss.misc.op.ImageIdHandler;

import eu.ggnet.saft.core.authorisation.AccessableAction;

import eu.ggnet.dwoss.util.FileJacket;

import eu.ggnet.dwoss.common.ExceptionUtil;

import static eu.ggnet.saft.core.Client.lookup;
import static eu.ggnet.dwoss.rights.api.AtomicRight.IMPORT_IMAGE_IDS;
import static javax.swing.JOptionPane.*;

/**
 *
 * @author oliver.guenther
 */
public class ImportImageIdsAction extends AccessableAction {

    public ImportImageIdsAction() {
        super(IMPORT_IMAGE_IDS);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JFileChooser dialog = new JFileChooser();
        dialog.setFileHidingEnabled(true);
        if ( JFileChooser.APPROVE_OPTION != dialog.showOpenDialog(lookup(Workspace.class).getMainFrame()) ) return;
        final File inFile = dialog.getSelectedFile();
        if ( YES_OPTION != showConfirmDialog(lookup(Workspace.class).getMainFrame(),
                "ImageIds aus der Datei:" + inFile.getPath() + " importieren ?", "ImageIds importieren",
                YES_NO_OPTION, QUESTION_MESSAGE) ) return;

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                lookup(ImageIdHandler.class).importMissing(new FileJacket("in", ".xls", inFile));
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
