/*
 * Copyright (C) 2018 GG-Net GmbH
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
package eu.ggnet.dwoss.customer.ui.cap;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import eu.ggnet.dwoss.customer.ui.ViolationDialog;
import eu.ggnet.dwoss.customer.ui.neo.CustomerConnectorFascade;
import eu.ggnet.saft.core.Ui;

/**
 *
 * @author pascal.perau
 */
public class MergeCustomerAfterAddressLabel extends AbstractAction {

    public MergeCustomerAfterAddressLabel() {
        super("Customer auf AddressLabel prüfen.");
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        Ui.build().fx().show(Ui.progress().wrap(() -> CustomerConnectorFascade.mergeCustomerAfterAddressLabel()), () -> {
            return new ViolationDialog();
        });
    }

}
