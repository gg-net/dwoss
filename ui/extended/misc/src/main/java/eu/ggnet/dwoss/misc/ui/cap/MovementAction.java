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
import java.util.concurrent.ExecutionException;

import javax.swing.AbstractAction;
import javax.swing.SwingWorker;

import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;

import eu.ggnet.dwoss.misc.ee.movement.MovementListingProducer;
import eu.ggnet.dwoss.stock.ee.entity.Stock;
import eu.ggnet.saft.core.Dl;
import eu.ggnet.saft.core.Ui;

/**
 *
 * @author oliver.guenther
 */
public class MovementAction extends AbstractAction {

    private final Stock stock;

    private final MovementListingProducer.ListType listType;

    public MovementAction(MovementListingProducer.ListType listType, Stock stock) {
        super(listType.description + " - " + stock.getName());
        this.stock = stock;
        this.listType = listType;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        new SwingWorker<JasperPrint, Object>() {
            @Override
            protected JasperPrint doInBackground() throws Exception {
                return Dl.remote().lookup(MovementListingProducer.class).generateList(listType, stock);
            }

            @Override
            protected void done() {
                try {
                    JasperViewer viewer = new JasperViewer(get(), false);
                    viewer.setVisible(true);
                } catch (InterruptedException | ExecutionException ex) {
                    Ui.handle(ex);
                }
            }
        }.execute();
    }
}
