/* 
 * Copyright (C) 2014 pascal.perau
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
package eu.ggnet.dwoss.misc.saleschannel;

import eu.ggnet.dwoss.rules.SalesChannel;
import eu.ggnet.dwoss.util.table.IColumnGetSetAction;
import eu.ggnet.dwoss.util.table.SimpleTableModel;
import eu.ggnet.dwoss.util.table.Column;

import java.util.*;

import eu.ggnet.dwoss.stock.entity.Stock;
import eu.ggnet.dwoss.stock.model.SalesChannelLine;

public class SalesChannelTableModel extends SimpleTableModel<SalesChannelLine> {

    private final Map<SalesChannel, Stock> stockToChannel;

    private static class ChannelStockColumn implements IColumnGetSetAction {

        private final List<SalesChannelLine> lines;

        private final Map<SalesChannel, Stock> stockToChannel;

        private final SalesChannel channel;

        public ChannelStockColumn(List<SalesChannelLine> lines, Map<SalesChannel, Stock> stockToChannel, SalesChannel channel) {
            this.lines = lines;
            this.stockToChannel = stockToChannel;
            this.channel = channel;
        }

        @Override
        public Object getValue(int row) {
            return lines.get(row).getSalesChannel() == channel;
        }

        @Override
        public void setValue(int row, Object value) {
            if ( (Boolean)value == true ) {
                SalesChannelLine l = lines.get(row);
                l.setSalesChannel(channel);
                Stock destination = stockToChannel.get(channel);
                if ( destination != null && destination.getId() != l.getStockId() ) l.setDestination(destination);
                else l.setDestination(null);
            }
        }
    }

    public SalesChannelTableModel(final List<SalesChannelLine> lines, final Map<SalesChannel, Stock> stockToChannel) {
        super(lines,
                new Column<>("SopoNr", false, 20, String.class, (int row) -> lines.get(row).getRefurbishedId()),
                new Column<>("End", false, 2, String.class, (int row) -> {
                    String id = lines.get(row).getRefurbishedId();
                    return id.substring(id.length() - 1);
                }),
                new Column<>("Bezeichnung", false, 150, String.class, (int row) -> lines.get(row).getDescription()),
                new Column<>("Bemerkung", false, 100, String.class, (int row) -> lines.get(row).getComment()),
                new Column<>("HEK", false, 2, Double.class, (int row) -> lines.get(row).getRetailerPrice()),
                new Column<>("EVK", false, 2, Double.class, (int row) -> lines.get(row).getCustomerPrice()),
                new Column<>("Lager", false, 50, String.class, (int row) -> lines.get(row).getStockName()),
                new Column<>("Unknown", true, 1, Boolean.class, new ChannelStockColumn(lines, stockToChannel, SalesChannel.UNKNOWN)),
                new Column<>("Retailer", true, 1, Boolean.class, new ChannelStockColumn(lines, stockToChannel, SalesChannel.RETAILER)),
                new Column<>("Customer", true, 1, Boolean.class, new ChannelStockColumn(lines, stockToChannel, SalesChannel.CUSTOMER)),
                new Column<>("auf Primärlager", false, 1, Boolean.class, (int row)
                        -> Optional.ofNullable(stockToChannel.get(lines.get(row).getSalesChannel())).map(s -> s.getId() == lines.get(row).getStockId()).orElse(false)),
                new Column<>("Transfer nach", true, 80, Stock.class, new IColumnGetSetAction() {

                    @Override
                    public void setValue(int row, Object value) {
                        lines.get(row).setDestination((Stock)value);
                    }

                    @Override
                    public Object getValue(int row) {
                        return lines.get(row).getDestination();
                    }

                }));
        this.stockToChannel = stockToChannel;
    }

    public void autoSelectChannel() {
        for (SalesChannelLine line : getDataModel()) {
            if ( line.getSalesChannel() != SalesChannel.UNKNOWN ) continue;
            for (SalesChannel sc : SalesChannel.values()) {
                Stock salesChannelStock = stockToChannel.get(sc);
                if ( salesChannelStock == null ) continue;
                if ( line.getStockId() == salesChannelStock.getId() && line.getRetailerPrice() > 0.01 ) {
                    line.setSalesChannel(sc);
                    break;
                }
            }
        }
        fireTableDataChanged();
    }
}
