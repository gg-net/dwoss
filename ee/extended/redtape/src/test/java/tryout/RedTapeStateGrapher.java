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
package tryout;

import java.util.HashSet;

import eu.ggnet.dwoss.core.common.values.*;
import eu.ggnet.dwoss.redtape.ee.entity.Document;
import eu.ggnet.dwoss.redtape.ee.entity.Dossier;
import eu.ggnet.dwoss.redtapext.ee.state.*;
import eu.ggnet.statemachine.StateMachine;
import eu.ggnet.statemachine.swing.Grapher;

/**
 * Tryout for the {@link StateMachine} when displaying RedTape states.
 *
 * @author mirko.schulze
 */
public class RedTapeStateGrapher {

    public static void main(String[] args) {

        Dossier dos = new Dossier();
        dos.setDispatch(false);
        Document doc = new Document();
        doc.setType(DocumentType.ORDER);
        dos.setPaymentMethod(PaymentMethod.ADVANCE_PAYMENT);
        doc.setDirective(Document.Directive.WAIT_FOR_MONEY);
//        doc.add(Condition.CREATED);
        doc.setDossier(dos);

        CustomerDocument cd = new CustomerDocument(new HashSet<>(), doc, ShippingCondition.FIVE, PaymentMethod.DIRECT_DEBIT);
        RedTapeStateMachine om = new RedTapeStateMachine();
        Grapher.showExact(om, new RedTapeStateFormater(), om.I);
        Grapher.showGreedy(om, new RedTapeStateFormater(), om.I);
        // Grapher.showFull(om);
//
        // System.out.println(om.getState(cd));
    }

}
