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

import eu.ggnet.dwoss.customer.ee.CustomerExporter;
import eu.ggnet.saft.Ui;
import eu.ggnet.saft.core.auth.AccessableAction;

import static eu.ggnet.dwoss.rights.api.AtomicRight.EXPORT_ALL_CUSTOMERS;
import static eu.ggnet.saft.Client.lookup;

/**
 *
 * @author pascal.perau
 */
public class ExportAllCustomers extends AccessableAction {

    public ExportAllCustomers() {
        super(EXPORT_ALL_CUSTOMERS);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        Ui.exec(Ui.progress().wrap(() -> Ui.osOpen(lookup(CustomerExporter.class).allToXls().toTemporaryFile())));
    }
}
