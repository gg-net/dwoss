package eu.ggnet.dwoss.misc.action.listings;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import javax.swing.AbstractAction;
import javax.swing.SwingWorker;

import eu.ggnet.dwoss.configuration.GlobalConfig;
import eu.ggnet.saft.core.Workspace;

import eu.ggnet.dwoss.misc.op.listings.SalesListingProducer;

import eu.ggnet.dwoss.util.FileJacket;

import eu.ggnet.dwoss.common.ExceptionUtil;

import static eu.ggnet.saft.core.Client.lookup;

/**
 *
 * @author oliver.guenther
 */
public class AllSalesListingAction extends AbstractAction {

    public AllSalesListingAction() {
        super("Listen über alle Geräte");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        new SwingWorker<FileJacket, Object>() {
            @Override
            protected FileJacket doInBackground() throws Exception {
                return lookup(SalesListingProducer.class).generateAllSalesListing();
            }

            @Override
            protected void done() {
                try {
                    File f = get().toFile(GlobalConfig.APPLICATION_PATH_OUTPUT);
                    Desktop.getDesktop().open(f);
                } catch (InterruptedException | ExecutionException | IOException ex) {
                    ExceptionUtil.show(lookup(Workspace.class).getMainFrame(), ex);
                }
            }
        }.execute();
    }
}
