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
package eu.ggnet.dwoss.price.imex;

import java.awt.event.ActionEvent;
import java.io.File;

import javafx.scene.control.Alert;

import eu.ggnet.dwoss.common.DetailDialog;
import eu.ggnet.dwoss.common.api.values.TradeName;
import eu.ggnet.dwoss.util.FileJacket;
import eu.ggnet.dwoss.util.TikaUtil;
import eu.ggnet.saft.*;
import eu.ggnet.saft.api.Reply;
import eu.ggnet.saft.core.auth.AccessableAction;
import eu.ggnet.saft.core.auth.Guardian;

import static eu.ggnet.dwoss.rights.api.AtomicRight.IMPORT_MISSING_CONTRACTOR_PRICES_DATA;
import static javafx.scene.control.Alert.AlertType.CONFIRMATION;
import static javafx.scene.control.ButtonType.OK;

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
        Ui.exec(() -> {
            Ui.fileChooser()
                    .open()
                    .opt().ifPresent(r -> {
                        Ui.build().dialog().eval(() -> new Alert(CONFIRMATION, "Fehlende " + contractor.getName() + " Daten aus der Datei:" + r.getPath() + " importieren ?"))
                                .opt()
                                .filter(b -> b == OK)
                                .map(b -> TikaUtil.isExcel(r))
                                .filter(Ui.failure()::handle)
                                .map(Reply::getPayload)
                                .map((File f) -> {
                                    if ( contractor.isManufacturer() ) {
                                        return Ui.progress().call(()
                                                -> Dl.remote().lookup(ContractorPricePartNoImporter.class).fromManufacturerXls(contractor, new FileJacket("in", ".xls", f), Dl.local().lookup(Guardian.class).getUsername()));
                                    }
                                    return Ui.progress().call(()
                                            -> Dl.remote().lookup(ContractorPricePartNoImporter.class).fromContractorXls(contractor, new FileJacket("in", ".xls", f), Dl.local().lookup(Guardian.class).getUsername()));
                                })
                                .ifPresent(re -> DetailDialog.show(UiCore.getMainFrame(), re.hasSucceded() ? "Import erfolgreich" : "Import fehlerhaft",
                        "Import " + contractor.getName() + " Daten (Lieferant" + (contractor.isManufacturer() ? "+Hersteller" : "") + ")" + (re.hasSucceded() ? " " : " fehlerhaft ") + "abgeschlossen", re.getSummary(), re.getDetailDescription()));
                    });
        });
    }
}
