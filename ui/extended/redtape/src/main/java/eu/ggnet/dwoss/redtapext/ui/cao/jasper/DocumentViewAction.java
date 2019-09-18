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

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.util.Optional;

import javax.swing.AbstractAction;

import org.apache.commons.lang.StringUtils;
import org.openide.util.Lookup;

import eu.ggnet.dwoss.customer.api.CustomerMetaData;
import eu.ggnet.dwoss.customer.api.CustomerService;
import eu.ggnet.dwoss.mandator.api.DocumentViewType;
import eu.ggnet.dwoss.redtape.ee.entity.Document;
import eu.ggnet.dwoss.redtape.ee.entity.Dossier;
import eu.ggnet.dwoss.redtapext.ee.DocumentSupporter;
import eu.ggnet.dwoss.redtapext.ee.RedTapeWorker;
import eu.ggnet.dwoss.redtapext.ee.state.CustomerDocument;
import eu.ggnet.dwoss.redtapext.ee.state.RedTapeStateTransition;
import eu.ggnet.dwoss.redtapext.ui.cao.RedTapeController;
import eu.ggnet.saft.api.Reply;
import eu.ggnet.saft.core.*;
import eu.ggnet.saft.experimental.auth.Guardian;
import eu.ggnet.statemachine.StateTransition;

/**
 *
 * @author pascal.perau
 */
public class DocumentViewAction extends AbstractAction {

    private Document document;

    private DocumentViewType type;

    private RedTapeController controller;

    private long customerId;

    @SuppressWarnings("OverridableMethodCallInConstructor")
    public DocumentViewAction(Document document, DocumentViewType type, RedTapeController controller, long customerId) {
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

        Ui.build(controller.getView()).fx().eval(() -> {
            JasperFxViewData.Builder bin = new JasperFxViewData.Builder()
                    .document(document)
                    .jasperPrint(Dl.remote().lookup(DocumentSupporter.class).render(document, type));
            if ( !StringUtils.isBlank(Dl.remote().lookup(CustomerService.class).defaultEmailCommunication(customerId)) )
                bin.mailCallback(() -> Dl.remote().lookup(DocumentSupporter.class).mail(document, type));

            return bin.build();
        }, () -> new JasperFxView())
                .cf()
                .thenApplyAsync(r -> updateBriefedInDatabase(r), UiCore.getExecutor())
                .thenAcceptAsync(od -> od.ifPresent(d -> controller.reloadSelectionOnStateChange(d)), EventQueue::invokeLater);
    }

    // TODO: Verbessere mich :-)
    /**
     * Updates the Briefed status of a dossier.
     * If the document of the view result needs transition, the transition is applied. Allso the briefed status is updated.
     * If nothing changed in the briefed world, nothing is happening.
     *
     * @param result the result of the view.
     * @return a dossier if something changed.
     */
    private Optional<Dossier> updateBriefedInDatabase(JasperFxViewResult result) {
        if ( !result.correctlyBriefed() ) return Optional.empty();
        CustomerMetaData customer = Dl.remote().lookup(CustomerService.class).asCustomerMetaData(customerId);
        CustomerDocument customerDocument = new CustomerDocument(customer.flags(), document, customer.shippingCondition(), customer.paymentMethod());
        for (StateTransition<CustomerDocument> stateTransition : Dl.remote().lookup(RedTapeWorker.class).getPossibleTransitions(customerDocument)) {
            RedTapeStateTransition redTapeStateTransition = (RedTapeStateTransition)stateTransition;
            for (RedTapeStateTransition.Hint hint : redTapeStateTransition.getHints()) {
                if ( hint == RedTapeStateTransition.Hint.SENDED_INFORMATION ) {
                    this.document = Optional.of(Dl.remote().lookup(RedTapeWorker.class)
                            .stateChange(customerDocument, redTapeStateTransition, Lookup.getDefault().lookup(Guardian.class).getUsername()))
                            .filter(Ui.failure()::handle)
                            .map(Reply::getPayload).orElse(document);
                }
            }
        }
        Dossier dossier = Dl.remote().lookup(DocumentSupporter.class).briefed(document, Dl.local().lookup(Guardian.class).getUsername());
        return Optional.of(dossier);
    }
}
