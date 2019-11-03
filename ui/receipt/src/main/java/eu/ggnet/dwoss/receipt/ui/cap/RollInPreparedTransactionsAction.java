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
package eu.ggnet.dwoss.receipt.ui.cap;

import eu.ggnet.dwoss.receipt.ui.cap.support.RollInPreparedTransactionViewCask;

import java.awt.event.ActionEvent;

import eu.ggnet.dwoss.stock.ee.StockAgent;
import eu.ggnet.dwoss.stock.ee.StockTransactionProcessor;
import eu.ggnet.saft.core.Dl;
import eu.ggnet.saft.core.Ui;
import eu.ggnet.saft.api.Reply;
import eu.ggnet.dwoss.core.widget.AccessableAction;
import eu.ggnet.saft.experimental.auth.Guardian;
import eu.ggnet.dwoss.core.widget.saft.OkCancelWrap;

import static eu.ggnet.dwoss.rights.api.AtomicRight.CREATE_ROLL_IN_OF_PREPARED_TRANSACTIONS;
import static eu.ggnet.dwoss.stock.ee.entity.StockTransactionStatusType.PREPARED;
import static eu.ggnet.dwoss.stock.ee.entity.StockTransactionType.ROLL_IN;

/**
 *
 * @author oliver.guenther
 */
public class RollInPreparedTransactionsAction extends AccessableAction {

    public RollInPreparedTransactionsAction() {
        super(CREATE_ROLL_IN_OF_PREPARED_TRANSACTIONS);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Ui.exec(() -> {
            Ui.build().title("Stock Transactionen einrollen ?").swing()
                    .eval(() -> Dl.remote().lookup(StockAgent.class).findStockTransactionEager(ROLL_IN, PREPARED), () -> OkCancelWrap.consumerVetoResult(new RollInPreparedTransactionViewCask()))
                    .opt()
                    .filter(Reply::hasSucceded)
                    .map(Reply::getPayload)
                    .ifPresent(sts -> Ui.progress().call(() -> Dl.remote().lookup(StockTransactionProcessor.class).rollIn(sts, Dl.local().lookup(Guardian.class).getUsername())));
        });
    }
}
