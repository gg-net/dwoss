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

import java.util.Objects;

import javafx.scene.control.MenuItem;

import eu.ggnet.dwoss.core.system.GlobalConfig;
import eu.ggnet.dwoss.mandator.api.service.ListingActionConfiguration;
import eu.ggnet.dwoss.misc.ee.listings.SalesListingProducer;
import eu.ggnet.saft.core.Dl;
import eu.ggnet.saft.core.Ui;

/**
 *
 * @author oliver.guenther
 */
public class SalesListingCreateMenuItem extends MenuItem {

    public void setConfig(ListingActionConfiguration config) {
        Objects.requireNonNull(config, "config must not be null");
        setText(config.name);
        setOnAction((e) -> {
            Ui.exec(() -> {
                Ui.progress().title(config.name).call(() -> {
                    Dl.remote().lookup(SalesListingProducer.class).generateListings(config).forEach(fj -> fj.toFile(GlobalConfig.APPLICATION_PATH_OUTPUT));
                    return null;
                });
            });
        });
    }
    
}
