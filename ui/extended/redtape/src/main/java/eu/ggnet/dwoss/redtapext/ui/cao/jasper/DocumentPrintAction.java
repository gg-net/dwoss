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
package eu.ggnet.dwoss.redtapext.ui.cao.jasper;

import eu.ggnet.dwoss.redtapext.ee.DocumentSupporter;
import eu.ggnet.dwoss.redtapext.ui.cao.jasper.JRViewerCask;
import eu.ggnet.dwoss.redtapext.ui.cao.RedTapeController;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import net.sf.jasperreports.engine.JasperPrint;

import org.openide.util.Lookup;

import eu.ggnet.dwoss.customer.api.CustomerMetaData;
import eu.ggnet.dwoss.customer.api.CustomerService;
import eu.ggnet.dwoss.mandator.api.DocumentViewType;
import eu.ggnet.dwoss.redtape.*;
import eu.ggnet.dwoss.redtape.entity.Document;
import eu.ggnet.dwoss.redtape.state.CustomerDocument;
import eu.ggnet.dwoss.redtape.state.RedTapeStateTransition;
import eu.ggnet.saft.Ui;
import eu.ggnet.saft.core.auth.Guardian;
import eu.ggnet.statemachine.StateTransition;

import static eu.ggnet.saft.Client.lookup;

/**
 *
 * @author pascal.perau
 */
public class DocumentPrintAction extends AbstractAction {

    private Document document;

    private DocumentViewType type;

    private RedTapeController controller;

    private long customerId;

    @SuppressWarnings("OverridableMethodCallInConstructor")
    public DocumentPrintAction(Document document, DocumentViewType type, RedTapeController controller, long customerId) {
        this.document = document;
        this.controller = controller;
        this.type = type;
        this.customerId = customerId;

        String name = "";
        switch (type) {
            case RESERVATION:
                name = "Reservierung";
                break;
            case SHIPPING:
                name = "Lieferschein";
                break;
            case DEFAULT:
                name = document.getType().getName() + "svorschau";
                break;
        }
        putValue(NAME, name);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Ui.exec(() -> {
            // TODO: This is a very special case, there the Ui needs the result on construction. So the consumer pattern cannot be used.
            // This meeans, for now no progress display.
            JasperPrint print = lookup(DocumentSupporter.class).render(document, type);
            CustomerMetaData customer = lookup(CustomerService.class).asCustomerMetaData(customerId);
            boolean mailAvailable = customer.getEmail() != null && !customer.getEmail().trim().isEmpty();
            Ui.swing().parent(controller.getView()).eval(() -> new JRViewerCask(print, document, type, mailAvailable))
                    .filter(c -> c.isCorrectlyBriefed())
                    .ifPresent(c -> Ui.progress().call(() -> {
                            CustomerDocument customerDocument = new CustomerDocument(customer.getFlags(), document, customer.getShippingCondition(), customer.getPaymentMethod());
                            for (StateTransition<CustomerDocument> stateTransition : lookup(RedTapeWorker.class).getPossibleTransitions(customerDocument)) {
                                RedTapeStateTransition redTapeStateTransition = (RedTapeStateTransition)stateTransition;
                                for (RedTapeStateTransition.Hint hint : redTapeStateTransition.getHints()) {
                                    if ( hint == RedTapeStateTransition.Hint.SENDED_INFORMATION ) {
                                        this.document = lookup(RedTapeWorker.class).stateChange(customerDocument, redTapeStateTransition, Lookup.getDefault().lookup(Guardian.class).getUsername());
                                    }
                                }
                            }
                            controller.reloadSelectionOnStateChange(lookup(DocumentSupporter.class).briefed(document, lookup(Guardian.class).getUsername()));
                            return null;
            }));

        });

    }
}
