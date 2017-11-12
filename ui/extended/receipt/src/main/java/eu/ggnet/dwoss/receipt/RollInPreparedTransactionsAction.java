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
package eu.ggnet.dwoss.receipt;

import java.awt.event.ActionEvent;

import eu.ggnet.dwoss.stock.StockAgent;
import eu.ggnet.dwoss.stock.StockTransactionProcessor;
import eu.ggnet.saft.Ui;
import eu.ggnet.saft.core.authorisation.AccessableAction;
import eu.ggnet.saft.core.authorisation.Guardian;

import static eu.ggnet.dwoss.rights.api.AtomicRight.CREATE_ROLL_IN_OF_PREPARED_TRANSACTIONS;
import static eu.ggnet.dwoss.stock.entity.StockTransactionStatusType.PREPARED;
import static eu.ggnet.dwoss.stock.entity.StockTransactionType.ROLL_IN;
import static eu.ggnet.saft.core.Client.lookup;

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
        Ui.call(() -> lookup(StockAgent.class).findStockTransactionEager(ROLL_IN, PREPARED))
                .choiceSwing(RollInPreparedTransactionViewCask.class)
                .onOk(v -> lookup(StockTransactionProcessor.class).rollIn(v.getStockTransactions(), lookup(Guardian.class).getUsername()))
                .exec();
    }
}
