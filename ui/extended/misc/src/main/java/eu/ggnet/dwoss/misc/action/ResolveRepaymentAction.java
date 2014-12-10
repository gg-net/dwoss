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
package eu.ggnet.dwoss.misc.action;

import java.awt.event.ActionEvent;
import java.util.ArrayList;

import eu.ggnet.dwoss.mandator.MandatorSupporter;
import eu.ggnet.dwoss.misc.repayment.ContractorSelectorPane;
import eu.ggnet.dwoss.misc.repayment.ResolveRepaymentController;
import eu.ggnet.saft.core.Ui;
import eu.ggnet.saft.core.authorisation.AccessableAction;

import static eu.ggnet.dwoss.rights.api.AtomicRight.RESOLVE_REPAYMENT;
import static eu.ggnet.saft.core.Client.lookup;

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
        Ui.call(() -> new ArrayList<>(lookup(MandatorSupporter.class).loadContractors().all()))
                .choiceFx(ContractorSelectorPane.class)
                .onOk(x -> x.selectedContactor())
                .openFxml(ResolveRepaymentController.class)
                .exec();
    }

}
