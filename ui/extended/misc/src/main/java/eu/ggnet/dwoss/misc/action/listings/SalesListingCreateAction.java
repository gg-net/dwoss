package eu.ggnet.dwoss.misc.action.listings;

import java.awt.event.ActionEvent;
import java.util.List;
import java.util.concurrent.*;

import javax.swing.AbstractAction;
import javax.swing.SwingWorker;

import eu.ggnet.dwoss.configuration.GlobalConfig;
import eu.ggnet.saft.core.Workspace;

import eu.ggnet.dwoss.mandator.api.service.ListingActionConfiguration;

import eu.ggnet.dwoss.misc.op.listings.SalesListingProducer;

import eu.ggnet.dwoss.util.FileJacket;

import eu.ggnet.dwoss.common.ExceptionUtil;

import static eu.ggnet.saft.core.Client.lookup;

/**
 *
 * @author oliver.guenther
 */
public class SalesListingCreateAction extends AbstractAction {

    ListingActionConfiguration config;

    public SalesListingCreateAction(ListingActionConfiguration config) {
        super(config.getName());
        this.config = config;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        new SwingWorker<List<FileJacket>, Object>() {
            @Override
            protected List<FileJacket> doInBackground() throws Exception {
                return lookup(SalesListingProducer.class).generateListings(config);
            }

            @Override
            protected void done() {
                try {
                    List<FileJacket> files = get();
                    if ( files != null ) {
                        for (FileJacket file : files) {
                            file.toFile(GlobalConfig.APPLICATION_PATH_OUTPUT);
                        }
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    ExceptionUtil.show(lookup(Workspace.class).getMainFrame(), ex);
                }
            }
        }.execute();
    }
}
