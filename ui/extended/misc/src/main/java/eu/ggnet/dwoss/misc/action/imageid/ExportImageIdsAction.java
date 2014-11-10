package eu.ggnet.dwoss.misc.action.imageid;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import javax.swing.AbstractAction;
import javax.swing.SwingWorker;

import eu.ggnet.saft.core.Workspace;

import eu.ggnet.dwoss.misc.op.ImageIdHandler;

import eu.ggnet.dwoss.rules.SalesChannel;

import eu.ggnet.dwoss.util.FileJacket;
import eu.ggnet.dwoss.common.ExceptionUtil;

import static eu.ggnet.saft.core.Client.lookup;

/**
 *
 * @author oliver.guenther
 */
public class ExportImageIdsAction extends AbstractAction {

    private final SalesChannel saleschannel;

    public ExportImageIdsAction(SalesChannel saleschannel) {
        super("Bilder Ids" + (saleschannel == null ? "" : " für " + saleschannel.getName()) + " exportieren");
        this.saleschannel = saleschannel;
    }

    public ExportImageIdsAction() {
        this(null);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        new SwingWorker<FileJacket, Object>() {
            @Override
            protected FileJacket doInBackground() throws Exception {
                return lookup(ImageIdHandler.class).exportMissing(saleschannel);
            }

            @Override
            protected void done() {
                try {
                    Desktop.getDesktop().open(get().toTemporaryFile());
                } catch (InterruptedException | ExecutionException | IOException ex) {
                    ExceptionUtil.show(lookup(Workspace.class).getMainFrame(), ex);
                }
            }
        }.execute();
    }
}
