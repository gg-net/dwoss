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
package eu.ggnet.dwoss.price.ui.cap.build;

import javafx.scene.control.MenuItem;

import eu.ggnet.dwoss.core.common.values.tradename.TradeName;
import eu.ggnet.dwoss.core.widget.*;
import eu.ggnet.dwoss.price.ee.imex.ContractorPricePartNoExporter;
import eu.ggnet.saft.core.Ui;

/**
 *
 * @author oliver.guenther
 */
public class ContractorExportMenuItem extends MenuItem {

    public ContractorExportMenuItem init(TradeName contractor, boolean missing) {
        setText("Export " + (missing ? "fehlende " : "alle ") + contractor.getName() + " Daten (Lieferant" + (contractor.isManufacturer() ? "+Hersteller" : "") + ")");
        setOnAction(e -> {
            Ui.exec(() -> {
                FileUtil.osOpen(Progressor.global().run("Export " + (missing ? "Fehlende" : "Alle") + " Daten",
                        () -> (missing
                               ? Dl.remote().lookup(ContractorPricePartNoExporter.class).toContractorMissingXls(contractor)
                               : Dl.remote().lookup(ContractorPricePartNoExporter.class).toContractorXls(contractor)).toTemporaryFile()));
            });
        });
        return this;
    }
}
