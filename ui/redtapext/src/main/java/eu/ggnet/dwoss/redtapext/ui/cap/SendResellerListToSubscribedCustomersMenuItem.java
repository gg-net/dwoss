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
package eu.ggnet.dwoss.redtapext.ui.cap;

import java.util.concurrent.CancellationException;

import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.core.widget.Dl;
import eu.ggnet.dwoss.core.widget.Progressor;
import eu.ggnet.dwoss.redtapext.ee.MailSalesListingService;
import eu.ggnet.saft.core.Ui;

import jakarta.enterprise.context.Dependent;

import static javafx.scene.control.ButtonType.OK;

/**
 *
 * @author oliver.guenther
 */
@Dependent
public class SendResellerListToSubscribedCustomersMenuItem extends MenuItem {

    private final static Logger L = LoggerFactory.getLogger(SendResellerListToSubscribedCustomersMenuItem.class);

    public SendResellerListToSubscribedCustomersMenuItem() {
        super("Händlerliste versenden (neu)");
        if ( !Dl.remote().optional(MailSalesListingService.class).isPresent() ) { // Just for the ELUS Case.
            setDisable(true);
        }
        setOnAction((e) -> {
            Ui.build().dialog().eval(() -> {
                Alert alert = new Alert(AlertType.CONFIRMATION);
                alert.setTitle("Händlerliste versenden");
                alert.setHeaderText("Möchten Sie die Händlerliste jetzt versenden");
                return alert;
            }).cf().thenAccept((ButtonType t) -> {
                L.debug("OK/Cancel Dialog result {}", t);
                if ( t != OK ) throw new CancellationException();
            })
                    .thenRun(() -> Progressor.global().run(() -> Dl.remote().lookup(MailSalesListingService.class).generateResellerXlsAndSendToSubscribedCustomers()))
                    .handle(Ui.handler());
        });
    }
}
