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

import javax.swing.AbstractAction;

import eu.ggnet.dwoss.rules.TradeName;
import eu.ggnet.saft.Ui;

import static eu.ggnet.saft.core.Client.lookup;

/**
 *
 * @author oliver.guenther
 */
public class ContractorExportAction extends AbstractAction {

    private final TradeName contractor;

    private final boolean missing;

    public ContractorExportAction(TradeName contractor, boolean missing) {
        super("Export " + (missing ? "fehlende " : "alle ") + contractor.getName() + " Daten (Lieferant" + (contractor.isManufacturer() ? "+Hersteller" : "") + ")");
        this.contractor = contractor;
        this.missing = missing;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Ui.exec(() -> {
            Ui.osOpen(Ui.progress().title("Export " + (missing ? "Fehlende" : "Alle") + " Daten")
                    .call(() -> (missing
                                 ? lookup(ContractorPricePartNoExporter.class).toContractorMissingXls(contractor)
                                 : lookup(ContractorPricePartNoExporter.class).toContractorXls(contractor)).toTemporaryFile()));
        });
    }
}
