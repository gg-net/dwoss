/*
 * Copyright (C) 2018 GG-Net GmbH
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
package eu.ggnet.dwoss.redtape.itest.eao;

import eu.ggnet.dwoss.common.api.values.PositionType;
import eu.ggnet.dwoss.common.api.values.DocumentType;
import eu.ggnet.dwoss.common.api.values.PaymentMethod;
import eu.ggnet.dwoss.redtape.ee.entity.*;

import static eu.ggnet.dwoss.redtape.ee.entity.Document.Directive.NONE;
import static eu.ggnet.dwoss.common.api.values.DocumentType.ORDER;

/**
 * Helper and shortcut.
 *
 * @author oliver.guenther
 */
public class RedTapeHelper {

    public static Document makeOrderDossier(PaymentMethod method, Address address) {
        Dossier dos = new Dossier(method, true, 1);
        Document doc = new Document(ORDER, NONE, new DocumentHistory("JUnit", "A History"));
        doc.setInvoiceAddress(address);
        doc.setShippingAddress(address);
        dos.add(doc);
        return doc;
    }

    public static void addUnitServiceAndComment(Document doc) {
        doc.append(Position.builder().type(PositionType.UNIT).amount(1).price(1000).name("A Unit").description("A Unit").tax(0.19).uniqueUnitId(123).uniqueUnitProductId(321).build());
        doc.append(Position.builder().type(PositionType.SERVICE).amount(10).price(222).name("A Service").description("A Service").tax(0.19).build());
        doc.append(Position.builder().type(PositionType.COMMENT).name("A Comment").description("A Comment").build());
    }

    public static Document transitionTo(Document last, DocumentType type) { // Keep the last active
        last.setActive(true);
        Document result = last.partialClone();
        result.setPredecessor(last);
        result.setDossier(last.getDossier());
        result.setType(type);
        result.setHistory(new DocumentHistory("Junit", "History"));
        return result;
    }

}
