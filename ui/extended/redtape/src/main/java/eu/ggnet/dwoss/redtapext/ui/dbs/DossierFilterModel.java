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
package eu.ggnet.dwoss.redtapext.ui.dbs;

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
