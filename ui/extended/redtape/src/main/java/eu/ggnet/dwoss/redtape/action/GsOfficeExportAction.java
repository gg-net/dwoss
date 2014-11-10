package eu.ggnet.dwoss.redtape.action;

import java.awt.event.ActionEvent;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import eu.ggnet.dwoss.configuration.GlobalConfig;
import eu.ggnet.saft.core.Workspace;

import eu.ggnet.dwoss.redtape.gsoffice.GsOfficeExporter;

import eu.ggnet.saft.core.authorisation.AccessableAction;

import eu.ggnet.dwoss.util.FileJacket;

import eu.ggnet.dwoss.util.DateRangeChooserDialog;
import eu.ggnet.dwoss.common.ExceptionUtil;

import static eu.ggnet.saft.core.Client.lookup;
import static eu.ggnet.dwoss.rights.api.AtomicRight.EXPORT_DOCUMENTS_FOR_GSOFFICE_IN_XML;

/**
 * Action to create the GsOfficeXml.
 *
 * @author pascal.perau
 */
public class GsOfficeExportAction extends AccessableAction {

    public GsOfficeExportAction() {
        super(EXPORT_DOCUMENTS_FOR_GSOFFICE_IN_XML);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final DateRangeChooserDialog dialog = new DateRangeChooserDialog(lookup(Workspace.class).getMainFrame());
        dialog.setVisible(true);
        if ( !dialog.isOk() ) return;
        new SwingWorker<Object, Object>() {
            @Override
            protected Object doInBackground() throws Exception {
                FileJacket jacket = lookup(GsOfficeExporter.class).toXml(dialog.getStart(), dialog.getEnd());
                jacket.toFile(GlobalConfig.APPLICATION_PATH_OUTPUT);
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
