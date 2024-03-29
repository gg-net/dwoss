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

import jakarta.ejb.Remote;

import net.sf.jasperreports.engine.JasperPrint;

import eu.ggnet.dwoss.core.common.FileJacket;
import eu.ggnet.dwoss.redtape.ee.entity.Document;
import eu.ggnet.dwoss.redtape.ee.entity.Document.Directive;
import eu.ggnet.dwoss.stock.api.PicoStock;

/**
 *
 * @author oliver.guenther
 */
@Remote
public interface MovementListingProducer {

    public static enum ListType {

        SHIPMENT("Versandliste", Document.Directive.PREPARE_SHIPPING),
        PICK_UP("Abholliste", Document.Directive.HAND_OVER_GOODS);

        public final String description;

        public final Document.Directive directive;

        private ListType(String description, Directive directive) {
            this.description = description;
            this.directive = directive;
        }

    }

    /**
     * Generates either the shipping or pickup list of the supplied stock
     *
     * @param listType the list type
     * @param stockId  the stock id
     * @return a JasperPrint
     */
    JasperPrint generateList(ListType listType, PicoStock stockId);

    /**
     * Generates either the shipping or pickup list of the supplied stock
     *
     * @param listType the list type
     * @param stockId  the stock id
     * @return a filejacket with an xls
     */
    FileJacket generateXls(ListType listType, PicoStock stockId);
}
