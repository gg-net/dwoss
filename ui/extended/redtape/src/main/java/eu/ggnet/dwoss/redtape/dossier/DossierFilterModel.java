package eu.ggnet.dwoss.redtape.dossier;

import java.util.ArrayList;
import java.util.Date;

import eu.ggnet.dwoss.redtape.entity.Dossier;

import eu.ggnet.dwoss.rules.DocumentType;

import eu.ggnet.dwoss.util.table.PojoColumn;
import eu.ggnet.dwoss.util.table.PojoTableModel;

/**
 * @author bastian.venz
 * @author oliver.guenther
 * @author pascal.perau
 */
public class DossierFilterModel extends PojoTableModel<Dossier> {

    public DossierFilterModel() {
        super(new ArrayList<>(),
                new PojoColumn<>("Kunden Id", false, 10, Integer.class, "customerId"),
                new PojoColumn<>("Dossier Id", false, 10, String.class, "identifier"),
                new PojoColumn<>("Zahlungsmethode", false, 100, String.class, "paymentMethod.note"),
                new PojoColumn<>("Directive", false, 50, String.class, "crucialDirective.name"),
                new PojoColumn<>("Bestelldatum", false, 50, Date.class, ""));//Property Name is never Used
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if ( columnIndex == 4 ) {

            if ( !super.getLines().get(rowIndex).getActiveDocuments(DocumentType.ORDER).isEmpty() )
                return super.getLines().get(rowIndex).getActiveDocuments(DocumentType.ORDER).get(0).getActual();
            return super.getLines().get(rowIndex).getActiveDocuments().get(0).getActual();
        }
        return super.getValueAt(rowIndex, columnIndex);
    }
}
