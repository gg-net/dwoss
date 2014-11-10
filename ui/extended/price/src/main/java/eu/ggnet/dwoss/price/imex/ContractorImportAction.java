package eu.ggnet.dwoss.price.imex;

import eu.ggnet.saft.core.authorisation.AccessableAction;
import eu.ggnet.saft.core.authorisation.Guardian;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.concurrent.ExecutionException;

import javax.swing.*;

import eu.ggnet.saft.core.Workspace;

import eu.ggnet.dwoss.price.imex.ContractorPricePartNoImporter;

import eu.ggnet.dwoss.rules.TradeName;

import eu.ggnet.dwoss.util.FileJacket;
import eu.ggnet.dwoss.common.DetailDialog;
import eu.ggnet.dwoss.common.ExceptionUtil;

import static eu.ggnet.saft.core.Client.lookup;
import static eu.ggnet.dwoss.rights.api.AtomicRight.IMPORT_MISSING_CONTRACTOR_PRICES_DATA;
import static javax.swing.JOptionPane.*;

/**
 *
 * @author oliver.guenther
 */
public class ContractorImportAction extends AccessableAction {

    private final TradeName contractor;

    public ContractorImportAction(TradeName contractor) {
        super(IMPORT_MISSING_CONTRACTOR_PRICES_DATA);
        putValue(NAME, "Import fehlende " + contractor.getName() + " Daten (Lieferant" + (contractor.isManufacturer() ? "+Hersteller" : "") + ")");
        this.contractor = contractor;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO: Make the importer generic.
        JFileChooser dialog = new JFileChooser();
        dialog.setFileHidingEnabled(true);
        if ( JFileChooser.APPROVE_OPTION != dialog.showOpenDialog(lookup(Workspace.class).getMainFrame()) ) return;
        final File inFile = dialog.getSelectedFile();
        if ( YES_OPTION != showConfirmDialog(lookup(Workspace.class).getMainFrame(),
                "Fehlende " + contractor.getName() + " Daten aus der Datei:" + inFile.getPath() + " importieren ?",
                "Fehlende Liferanten Daten importieren",
                YES_NO_OPTION, QUESTION_MESSAGE) ) return;

        new SwingWorker<ContractorPricePartNoImporter.ImportResult, Void>() {
            @Override
            protected ContractorPricePartNoImporter.ImportResult doInBackground() throws Exception {
                FileJacket in = new FileJacket("in", ".xls", inFile);
                String user = lookup(Guardian.class).getUsername();
                if ( contractor.isManufacturer() ) return lookup(ContractorPricePartNoImporter.class).fromManufacturerXls(contractor, in, user);
                return lookup(ContractorPricePartNoImporter.class).fromContractorXls(contractor, in, user);
            }

            @Override
            protected void done() {
                try {
                    ContractorPricePartNoImporter.ImportResult result = get();
                    new DetailDialog(lookup(Workspace.class).getMainFrame())
                            .head(contractor.getName() + " Import")
                            .message("Import " + contractor.getName() + " Daten (Lieferant" + (contractor.isManufacturer() ? "+Hersteller" : "") + ") abgeschlossen")
                            .overview(result.getOverview())
                            .details(result.getErrors())
                            .showDialog();
                } catch (InterruptedException | ExecutionException ex) {
                    ExceptionUtil.show(lookup(Workspace.class).getMainFrame(), ex);
                }
            }
        }.execute();
    }
}
