package eu.ggnet.dwoss.receipt.shipment;

import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import eu.ggnet.dwoss.rules.TradeName;
import eu.ggnet.dwoss.stock.entity.Shipment;
import eu.ggnet.dwoss.util.table.PojoColumn;
import eu.ggnet.dwoss.util.table.PojoFilter;
import eu.ggnet.dwoss.util.table.PojoTableModel;

public class ShipmentModel extends PojoTableModel<Shipment> {

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
            boolean o = !isOwner || t.getContractor()== owner;
            boolean st = !isStatus || t.getStatus() == status;
            return s && o && st;
        }

    }

    private ShipmentFilter filter;

    public ShipmentModel(final List<Shipment> lines) {
        super(lines,
                new PojoColumn<Shipment>("ShipmentNamen", false, 5, String.class,"shipmentId"),
                new PojoColumn<Shipment>("Besitzer", false, 5, TradeName.class,"contractor"),
                new PojoColumn<Shipment>("Letzter Status", false, 10, Shipment.Status.class,"status"),
                new PojoColumn<Shipment>("Datum", false, 20, Date.class,"date"));
        filter = new ShipmentFilter();
        setFilter(filter);
    }

    public void filterShipmentId(String s, boolean enable) {
        filter.regexShipment = "(?i).*"+s+".*";
        filter.isShipmentId = enable;
        fireTableDataChanged();
    }

    public void filterStatus(Shipment.Status status ,boolean enable) {
        filter.status = status;
        filter.isStatus = enable;
        fireTableDataChanged();
    }

    public void filterOwner(TradeName owner, boolean enable) {
        filter.owner = owner;
        filter.isOwner = enable;
        fireTableDataChanged();
    }

}
