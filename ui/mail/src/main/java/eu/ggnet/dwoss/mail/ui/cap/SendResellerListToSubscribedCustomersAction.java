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

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.core.widget.Dl;
import eu.ggnet.dwoss.core.widget.Progressor;
import eu.ggnet.dwoss.mail.ee.MailSalesListingService;
import eu.ggnet.saft.core.Ui;

/**
 *
 * @author oliver.guenther
 */
public class SendResellerListToSubscribedCustomersAction extends AbstractAction {

    private final static Logger L = LoggerFactory.getLogger(SendResellerListToSubscribedCustomersAction.class);

    public SendResellerListToSubscribedCustomersAction() {
        super("Händlerliste versenden (neu)");
        if ( !Dl.remote().optional(MailSalesListingService.class).isPresent() ) { // Just for the ELUS Case.
            setEnabled(false);
            putValue(Action.SHORT_DESCRIPTION, "No MailSalesListingService found, disabling action");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Ui.build().dialog()
                .eval(() -> {
                    Alert alert = new Alert(AlertType.CONFIRMATION);
                    alert.setTitle("Händlerliste versenden");
                    alert.setHeaderText("Möchten Sie die Händlerliste jetzt versenden");
                    return alert;
                }).cf()
                .thenRun(() -> Progressor.global().run(() -> Dl.remote().lookup(MailSalesListingService.class).generateResellerXlsAndSendToSubscribedCustomers()))
                .handle(Ui.handler());
    }

    /*
    
    Progressor.run("Title",() -> Dl.remote().lookup(MailSalesListingService.class).generateResellerXlsAndSendToSubscribedCustomers());
    
    Progressor.run(() -> Dl.remote().lookup(MailSalesListingService.class).generateResellerXlsAndSendToSubscribedCustomers());
     */
}
