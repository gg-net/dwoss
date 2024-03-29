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
import eu.ggnet.dwoss.core.widget.auth.Guardian;
import eu.ggnet.dwoss.price.ee.Importer;
import eu.ggnet.saft.core.Ui;

import jakarta.enterprise.context.Dependent;

import static eu.ggnet.dwoss.rights.api.AtomicRight.EXPORT_AND_IMPORT_PRICEMANAGMENT;

/**
 * Executing the price management generator and stores the generated values.
 *
 * @author pascal.perau
 */
@Dependent
public class PriceExportImportAction extends AccessableAction {

    public PriceExportImportAction() {
        super(EXPORT_AND_IMPORT_PRICEMANAGMENT);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Ui.exec(() -> Progressor.global().run(() -> {
            Dl.remote().lookup(Importer.class).direct(Dl.local().lookup(Guardian.class).getUsername());
            return null;
        }));
    }
}
