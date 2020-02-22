/*
 * Copyright (C) 2020 GG-Net GmbH
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
package eu.ggnet.dwoss.price.ui.cap.build;

import java.util.Objects;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import javafx.scene.control.Menu;

import eu.ggnet.dwoss.core.common.values.tradename.TradeName;
import eu.ggnet.dwoss.mandator.spi.CachedMandators;
import eu.ggnet.saft.core.Dl;

/**
 *
 * @author oliver.guenther
 */
public class PriceSubMenuBuilder {

    public static class PriceSubMenu {

        public final Menu menu;

        public PriceSubMenu(Menu menu) {
            this.menu = Objects.requireNonNull(menu);
        }

    }

    @Inject
    private Instance<Object> instance;

    @Produces
    public PriceSubMenu createMenu() {
        Menu menu = new Menu("Im-/Export");

        for (TradeName contractor : Dl.local().lookup(CachedMandators.class).loadContractors().all()) {
            if ( contractor.isManufacturer() ) {
                menu.getItems().add(instance.select(ManufacturerExportMenuItem.class).get().init(contractor));
            } else {
                menu.getItems().add(instance.select(ContractorExportMenuItem.class).get().init(contractor, true));
                menu.getItems().add(instance.select(ContractorExportMenuItem.class).get().init(contractor, false));
            }
            menu.getItems().add(instance.select(ContractorImportMenuItem.class).get().init(contractor));
        }
        return new PriceSubMenu(menu);
    }

}
