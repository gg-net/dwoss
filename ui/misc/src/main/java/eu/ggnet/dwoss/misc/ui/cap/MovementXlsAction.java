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
package eu.ggnet.dwoss.misc.ui.cap;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import eu.ggnet.dwoss.misc.ee.movement.MovementListingProducer;
import eu.ggnet.dwoss.stock.ee.entity.Stock;
import eu.ggnet.saft.core.Dl;
import eu.ggnet.saft.core.Ui;

/**
 * Opens the MovementList as XLS.
 * 
 * @author oliver.guenther
 */
public class MovementXlsAction extends AbstractAction {

    private final Stock stock;

    private final MovementListingProducer.ListType listType;

    public MovementXlsAction(MovementListingProducer.ListType listType, Stock stock) {
        super(listType.description + " - " + stock.getName() + " - XLS");
        this.stock = stock;
        this.listType = listType;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Ui.exec(() -> {
            Ui.osOpen(Ui.progress().call(() -> Dl.remote().lookup(MovementListingProducer.class).generateXls(listType, stock).toTemporaryFile()));
        });
    }
}
