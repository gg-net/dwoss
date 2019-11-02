/*
 * Copyright (C) 2019 GG-Net GmbH
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
package eu.ggnet.dwoss.mail.ui.cap;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import eu.ggnet.dwoss.mail.ee.MailSalesListingService;
import eu.ggnet.saft.core.Dl;
import eu.ggnet.saft.core.Ui;

/**
 *
 * @author oliver.guenther
 */
public class SendResellerListToSubscribedCustomersAction extends AbstractAction {

    public SendResellerListToSubscribedCustomersAction() {
        super("Händlerliste versenden (neu)");
        if ( !Dl.remote().optional(MailSalesListingService.class).isPresent() ) { // Just for the ELUS Case.
            setEnabled(false);
            putValue(Action.SHORT_DESCRIPTION, "No MailSalesListingService found, disabling action");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Ui.exec(Ui.progress().wrap(() -> Dl.remote().lookup(MailSalesListingService.class).generateResellerXlsAndSendToSubscribedCustomers()));
    }
}
