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
package eu.ggnet.dwoss.misc.ee.movement;

import javax.ejb.Remote;

import net.sf.jasperreports.engine.JasperPrint;

import eu.ggnet.dwoss.redtape.ee.entity.Document;
import eu.ggnet.dwoss.stock.ee.entity.Stock;

import lombok.*;

/**
 *
 * @author oliver.guenther
 */
@Remote
public interface MovementListingProducer {

    @RequiredArgsConstructor
    @Getter
    public static enum ListType {

        SHIPMENT("Versandliste", Document.Directive.PREPARE_SHIPPING),
        PICK_UP("Abholliste", Document.Directive.HAND_OVER_GOODS);

        private final String name;

        private final Document.Directive directive;
    }

    JasperPrint generateList(ListType listType, Stock stockId);
}
