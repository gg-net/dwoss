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
package eu.ggnet.dwoss.price.ui.cap;

import java.awt.event.ActionEvent;

import eu.ggnet.dwoss.core.widget.*;
import eu.ggnet.dwoss.price.ee.Exporter;
import eu.ggnet.saft.core.Ui;

import jakarta.enterprise.context.Dependent;

import static eu.ggnet.dwoss.rights.api.AtomicRight.EXPORT_PRICEMANAGMENT;

/**
 * Export the price management xls.
 *
 * @author pascal.perau
 */
@Dependent
public class PriceExportAction extends AccessableAction {

    public PriceExportAction() {
        super(EXPORT_PRICEMANAGMENT);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Ui.exec(() -> FileUtil.osOpen(Progressor.global().run(() -> Dl.remote().lookup(Exporter.class).toXls().toTemporaryFile())));
    }

}
