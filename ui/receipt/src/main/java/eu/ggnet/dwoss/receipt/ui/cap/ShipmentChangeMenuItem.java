/*
 * Copyright (C) 2021 GG-Net GmbH
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
package eu.ggnet.dwoss.receipt.ui.cap;

import java.time.LocalDate;

import jakarta.inject.Inject;

import jakarta.annotation.PostConstruct;

import javafx.scene.control.MenuItem;

import eu.ggnet.dwoss.core.widget.Dl;
import eu.ggnet.dwoss.core.widget.auth.Guardian;
import eu.ggnet.dwoss.core.widget.dl.RemoteDl;
import eu.ggnet.dwoss.receipt.ui.ShipmentChangeController;
import eu.ggnet.dwoss.stock.api.StockApi;
import eu.ggnet.dwoss.uniqueunit.api.UniqueUnitApi;
import eu.ggnet.saft.core.Saft;
import eu.ggnet.saft.core.UiUtil;

import jakarta.enterprise.context.Dependent;

@Dependent
public class ShipmentChangeMenuItem extends MenuItem {

    @Inject
    private Saft saft;

    @Inject
    private RemoteDl remote;

    public ShipmentChangeMenuItem() {
        super("Shipment an einem Gerät ändern");
    }

    @PostConstruct
    private void init() {
        setOnAction(e -> saft.build().fxml().eval(() -> remote.lookup(StockApi.class).findShipmentsSince(LocalDate.now().minusYears(2)), ShipmentChangeController.class).cf()                
                .thenAccept(r -> UiUtil.exceptionRun(() -> remote.lookup(UniqueUnitApi.class)
                        .changeShipment(r.refurbishedId(), r.shipmentId(), r.shipmentLabel(), Dl.local().lookup(Guardian.class).getUsername())))
                .thenRun(() -> saft.build().alert("Shipmentänderung erfolgreich."))
                .handle(saft.handler())
        );
    }

}
