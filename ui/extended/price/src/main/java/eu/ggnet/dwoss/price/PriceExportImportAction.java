package eu.ggnet.dwoss.price;

import eu.ggnet.saft.core.authorisation.Guardian;

import java.awt.event.ActionEvent;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import eu.ggnet.saft.core.Workspace;

import eu.ggnet.dwoss.price.Importer;

import eu.ggnet.saft.core.authorisation.AccessableAction;

import eu.ggnet.dwoss.common.ExceptionUtil;

import static eu.ggnet.saft.core.Client.lookup;
import static eu.ggnet.dwoss.rights.api.AtomicRight.EXPORT_AND_IMPORT_PRICEMANAGMENT;

/**
 *
 * @author pascal.perau
 */
public class PriceExportImportAction extends AccessableAction {

    public PriceExportImportAction() {
        super(EXPORT_AND_IMPORT_PRICEMANAGMENT);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        new SwingWorker<Object, Object>() {
            @Override
            protected Object doInBackground() throws Exception {
                lookup(Importer.class).direct(lookup(Guardian.class).getUsername());
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
