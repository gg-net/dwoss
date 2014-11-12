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
package eu.ggnet.dwoss.redtape.action;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JDialog;

import org.openide.util.Lookup;

import net.sf.jasperreports.engine.JasperPrint;

import eu.ggnet.saft.core.authorisation.Guardian;

import eu.ggnet.dwoss.customer.api.CustomerService;
import eu.ggnet.dwoss.customer.api.CustomerMetaData;

import eu.ggnet.dwoss.redtape.entity.Document;

import eu.ggnet.dwoss.redtape.DocumentSupporter;
import eu.ggnet.dwoss.redtape.RedTapeWorker;
import eu.ggnet.dwoss.redtape.state.CustomerDocument;
import eu.ggnet.dwoss.redtape.state.RedTapeStateTransition;

import eu.ggnet.dwoss.redtape.JRViewerCask;
import eu.ggnet.dwoss.redtape.RedTapeController;

import eu.ggnet.dwoss.mandator.api.DocumentViewType;

import eu.ggnet.statemachine.StateTransition;

import static eu.ggnet.saft.core.Client.lookup;

/**
 *
 * @author pascal.perau
 */
public class DocumentPrintAction extends AbstractAction {

    private Document document;

    private DocumentViewType type;

    private RedTapeController controller;

    private long customerId;

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
        JasperPrint print = lookup(DocumentSupporter.class).render(document, type);

        JDialog d = new JDialog(controller.getView(), "Dokument drucken/versenden");
        d.setSize(800, 1000);
        d.setModalityType(Dialog.ModalityType.DOCUMENT_MODAL);
        d.setLocationRelativeTo(controller.getView());
        d.getContentPane().setLayout(new BorderLayout());
        CustomerMetaData customer = lookup(CustomerService.class).asCustomerMetaData(customerId);
        boolean mailAvailable = customer.getEmail() != null && !customer.getEmail().trim().isEmpty();
        JRViewerCask jrViewerCask = new JRViewerCask(print, document, type, mailAvailable);
        d.getContentPane().add(jrViewerCask, BorderLayout.CENTER);
        d.setVisible(true);
        if ( jrViewerCask.isCorrectlyBriefed() ) {
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
        }
    }
}
