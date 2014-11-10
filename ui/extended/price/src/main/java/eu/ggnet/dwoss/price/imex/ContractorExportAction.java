package eu.ggnet.dwoss.price.imex;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import javax.swing.AbstractAction;
import javax.swing.SwingWorker;

import eu.ggnet.saft.core.Workspace;

import eu.ggnet.dwoss.price.imex.ContractorPricePartNoExporter;

import eu.ggnet.dwoss.rules.TradeName;

import eu.ggnet.dwoss.util.FileJacket;
import eu.ggnet.dwoss.common.ExceptionUtil;

import static eu.ggnet.saft.core.Client.lookup;

/**
 *
 * @author oliver.guenther
 */
public class ContractorExportAction extends AbstractAction {

    private final TradeName contractor;

    public ContractorExportAction(TradeName contractor) {
        super("Export fehlende " + contractor.getName() + " Daten (Lieferant" + (contractor.isManufacturer() ? "+Hersteller" : "") + ")");
        this.contractor = contractor;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        new SwingWorker<FileJacket, Object>() {
            @Override
            protected FileJacket doInBackground() throws Exception {
                if ( contractor.isManufacturer() ) return lookup(ContractorPricePartNoExporter.class).toManufacturerXls(contractor);
                return lookup(ContractorPricePartNoExporter.class).toContractorXls(contractor);
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
