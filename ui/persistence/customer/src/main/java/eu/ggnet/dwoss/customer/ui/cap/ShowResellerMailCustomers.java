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
package eu.ggnet.dwoss.customer.ui.cap;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.customer.ee.CustomerAgent;
import eu.ggnet.dwoss.customer.ui.neo.ResellerListView;
import eu.ggnet.saft.core.Dl;
import eu.ggnet.saft.core.Ui;

/**
 * Zeigt die liste aller eMail Adressen, die die Händlerliste erhalten.
 *
 * @author oliver.guenther
 */
public class ShowResellerMailCustomers extends AbstractAction {

    private final static Logger L = LoggerFactory.getLogger(ShowResellerMailCustomers.class);

    public ShowResellerMailCustomers() {
        super("Anzeige der Händlerlistenabos");
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        Ui.build().fx().show(() -> Dl.remote().lookup(CustomerAgent.class).findAllResellerListCustomersEager(),
                () -> new ResellerListView());
    }

}
