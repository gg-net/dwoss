/*
 * Copyright (C) 2014 GG-Net GmbH
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
package eu.ggnet.dwoss.misc.ui.cap;

import java.awt.event.ActionEvent;

import javafx.scene.control.ChoiceDialog;

import eu.ggnet.dwoss.mandator.api.value.Contractors;
import eu.ggnet.dwoss.mandator.spi.CachedMandators;
import eu.ggnet.dwoss.misc.ui.ResolveRepaymentController;
import eu.ggnet.dwoss.core.common.values.tradename.TradeName;
import eu.ggnet.dwoss.core.widget.Dl;
import eu.ggnet.saft.core.Ui;
import eu.ggnet.dwoss.core.widget.AccessableAction;

import static eu.ggnet.dwoss.rights.api.AtomicRight.RESOLVE_REPAYMENT;

/**
 *
 * @author bastian.venz
 */
public class ResolveRepaymentAction extends AccessableAction {

    public ResolveRepaymentAction() {
        super(RESOLVE_REPAYMENT);
    }

    private boolean cancel = false;

    @Override
    public void actionPerformed(ActionEvent e) {
        Ui.exec(() -> {
            Contractors contractors = Ui.progress().call(() -> Dl.local().lookup(CachedMandators.class).loadContractors());
            Ui.exec(() -> {
                Ui.build().dialog().eval(() -> {
                    ChoiceDialog<TradeName> dialog = new ChoiceDialog<>(contractors.all().iterator().next(), contractors.all());
                    dialog.setTitle("Gutschriften");
                    dialog.setHeaderText(RESOLVE_REPAYMENT.toName());
                    dialog.setContentText("Lieferant auswählen:");
                    return dialog;
                }).opt().ifPresent(c -> Ui.build().fxml().show(() -> c, ResolveRepaymentController.class));
            });
        });
    }

}
