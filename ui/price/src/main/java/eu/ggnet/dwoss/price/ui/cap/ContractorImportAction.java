/*
 * Copyright (C) 2014 GG-Net GmbH - Oliver Günther
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.ggnet.dwoss.price.ui.cap;

import eu.ggnet.dwoss.core.widget.ConfirmationDialog;

import java.awt.event.ActionEvent;

import eu.ggnet.dwoss.core.common.FileJacket;
import eu.ggnet.dwoss.core.common.values.tradename.TradeName;
import eu.ggnet.dwoss.core.widget.*;
import eu.ggnet.dwoss.core.widget.auth.Guardian;
import eu.ggnet.dwoss.price.ee.imex.ContractorPricePartNoImporter;
import eu.ggnet.saft.core.Ui;

import static eu.ggnet.dwoss.rights.api.AtomicRight.IMPORT_MISSING_CONTRACTOR_PRICES_DATA;

/**
 *
 * @author oliver.guenther
 */
public class ContractorImportAction extends AccessableAction {

    private final TradeName contractor;

    public ContractorImportAction(TradeName contractor) {
        super(IMPORT_MISSING_CONTRACTOR_PRICES_DATA);
        putValue(NAME, "Import fehlende " + contractor.getDescription() + " Daten (Lieferant" + (contractor.isManufacturer() ? "+Hersteller" : "") + ")");
        this.contractor = contractor;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        Ui.fileChooser().open().cf()
                .thenCompose(f -> Ui.build().dialog().eval(() -> new ConfirmationDialog<>("Import durchführen ?", "Fehlende " + contractor.getDescription() + " Daten aus der Datei:" + f.getPath() + " importieren ?", f)).cf())
                .thenApply(f -> TikaUtil.verifyExcel(f))
                .thenApply(f -> {
                    if ( contractor.isManufacturer() )
                        return Ui.progress().call(() -> importer().fromManufacturerXls(contractor, new FileJacket("in", ".xls", f), user()));
                    else
                        return Ui.progress().call(() -> importer().fromContractorXls(contractor, new FileJacket("in", ".xls", f), user()));
                })
                .thenAccept(ir -> Ui.build().fx().show(() -> ir, () -> new ImportResultView()))
                .handle(Ui.handler());

    }

    private ContractorPricePartNoImporter importer() {
        return Dl.remote().lookup(ContractorPricePartNoImporter.class);
    }

    private String user() {
        return Dl.local().lookup(Guardian.class).getUsername();
    }
}
