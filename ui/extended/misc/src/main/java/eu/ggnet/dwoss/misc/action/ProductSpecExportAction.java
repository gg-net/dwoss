package eu.ggnet.dwoss.misc.action;

import java.awt.event.ActionEvent;
import java.util.concurrent.ExecutionException;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import eu.ggnet.dwoss.configuration.GlobalConfig;
import eu.ggnet.saft.core.Workspace;
import eu.ggnet.saft.core.authorisation.AccessableAction;

import eu.ggnet.dwoss.spec.SpecExporter;

import eu.ggnet.dwoss.util.FileJacket;

import eu.ggnet.dwoss.common.ExceptionUtil;

import static eu.ggnet.saft.core.Client.lookup;
import static eu.ggnet.dwoss.rights.api.AtomicRight.READ_PRODUCT_SPEC_FOR_XML_EXPORT;

/**
 * Action to Export the ProductSpecs to XML.
 * <p/>
 * @author oliver.guenther
 */
public class ProductSpecExportAction extends AccessableAction {

    public ProductSpecExportAction() {
        super(READ_PRODUCT_SPEC_FOR_XML_EXPORT);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String input = JOptionPane.showInputDialog(lookup(Workspace.class).getMainFrame(), "Grenze der zu exportierenden ProductSpecs eingeben", 100);
        if ( input == null ) return;
        try {
            final int amount = Integer.parseInt(input);

            new SwingWorker<FileJacket, Object>() {
                @Override
                protected FileJacket doInBackground() throws Exception {
                    return lookup(SpecExporter.class).toXml(amount);
                }

                @Override
                protected void done() {
                    try {
                        FileJacket fj = get();
                        fj.toFile(GlobalConfig.APPLICATION_PATH_OUTPUT);
                    } catch (InterruptedException | ExecutionException ex) {
                        ExceptionUtil.show(lookup(Workspace.class).getMainFrame(), ex);
                    }
                }
            }.execute();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(lookup(Workspace.class).getMainFrame(), "Die Eingabe '" + input + "' ist keine Zahl");
        }
    }
}
