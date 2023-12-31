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

import javafx.scene.control.MenuItem;

import eu.ggnet.dwoss.core.widget.*;
import eu.ggnet.dwoss.misc.ee.ImageIdHandler;
import eu.ggnet.saft.core.Ui;

import jakarta.enterprise.context.Dependent;

import static eu.ggnet.dwoss.core.common.values.SalesChannel.CUSTOMER;

/**
 *
 * @author oliver.guenther
 */
@Dependent
public class ExportImageIdsForCustomerMenuItem extends MenuItem {

    public ExportImageIdsForCustomerMenuItem() {
        super("Bilder Ids für Verkaufskanal Endkunde exportieren");
        setOnAction((e) -> {
            Ui.exec(() -> {
                FileUtil.osOpen(Progressor.global().run("Bilder Ids", () -> Dl.remote().lookup(ImageIdHandler.class).exportMissing(CUSTOMER).toTemporaryFile()));
            });
        });
    }

}
