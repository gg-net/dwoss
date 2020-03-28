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
package eu.ggnet.dwoss.stock.ui.cap;

import java.awt.event.ActionEvent;

import eu.ggnet.dwoss.stock.ee.StockTransactionProcessor;
import eu.ggnet.dwoss.stock.ui.transactions.RemoveQuestionView;
import eu.ggnet.saft.core.Dl;
import eu.ggnet.saft.core.Ui;
import eu.ggnet.dwoss.core.widget.AccessableAction;
import eu.ggnet.dwoss.core.widget.auth.Guardian;
import eu.ggnet.saft.core.ui.AlertType;

import static eu.ggnet.dwoss.rights.api.AtomicRight.REMOVE_SINGE_UNIT_FROM_TRANSACTION;

/**
 * Removes a unit from a Transaction.
 * <p/>
 * @author oliver.guenther
 */
public class RemoveUnitFromTransactionAction extends AccessableAction {

    public RemoveUnitFromTransactionAction() {
        super(REMOVE_SINGE_UNIT_FROM_TRANSACTION);
    }

    @Override
    @SuppressWarnings("UseSpecificCatch")
    public void actionPerformed(ActionEvent e) {
        Ui.exec(() -> {
            Ui.build().fx().eval(() -> new RemoveQuestionView()).opt().ifPresent(v -> Ui.progress().call(() -> {
                Dl.remote().lookup(StockTransactionProcessor.class).removeFromPreparedTransaction(v.refurbishId(), Dl.local().lookup(Guardian.class).getUsername(), v.comment());
                Ui.build().alert().message("SopoNr: " + v.refurbishId() + " aus Transaktion entfernt").show(AlertType.INFO);
                return null;
            }));
        });
    }
}
