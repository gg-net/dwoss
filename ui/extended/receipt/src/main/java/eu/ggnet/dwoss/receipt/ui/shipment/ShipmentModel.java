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
package eu.ggnet.dwoss.receipt.ui.shipment;

import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.common.api.values.TradeName;
import eu.ggnet.dwoss.common.ui.table.*;
import eu.ggnet.dwoss.stock.ee.entity.Shipment;

import lombok.ToString;

public class ShipmentModel extends PojoTableModel<Shipment> {

    private final static Logger L = LoggerFactory.getLogger(ShipmentModel.class);

    @ToString
    private class ShipmentFilter implements PojoFilter<Shipment> {

        private boolean isShipmentId;

        private boolean isOwner;

        private boolean isStatus;

        private String regexShipment = "";

        private TradeName owner = null;

        private Shipment.Status status = null;

        @Override
        public boolean filter(Shipment t) {
            boolean s = !isShipmentId || Pattern.matches(regexShipment, t.getShipmentId());
            boolean o = !isOwner || t.getContractor() == owner;
            boolean st = !isStatus || t.getStatus() == status;
            return s && o && st;
        }

    }

    private ShipmentFilter filter;

    public ShipmentModel(final List<Shipment> lines) {
        super(lines,
                new PojoColumn<Shipment>("ShipmentNamen", false, 5, String.class, "shipmentId"),
                new PojoColumn<Shipment>("Besitzer", false, 5, TradeName.class, "contractor"),
                new PojoColumn<Shipment>("Letzter Status", false, 10, Shipment.Status.class, "status"),
                new PojoColumn<Shipment>("Datum", false, 20, Date.class, "date"));
        filter = new ShipmentFilter();
        setFilter(filter);
    }

    public void filterShipmentId(String s, boolean enable) {
        filter.regexShipment = "(?i).*" + s + ".*";
        filter.isShipmentId = enable;
        L.debug("Filter changed. {}", filter);
        fireTableDataChanged();
    }

    public void filterStatus(Shipment.Status status, boolean enable) {
        filter.status = status;
        filter.isStatus = enable;
        L.debug("Filter changed. {}", filter);
        fireTableDataChanged();
    }

    public void filterOwner(TradeName owner, boolean enable) {
        filter.owner = owner;
        filter.isOwner = enable;
        L.debug("Filter changed. {}", filter);
        fireTableDataChanged();
    }

}
