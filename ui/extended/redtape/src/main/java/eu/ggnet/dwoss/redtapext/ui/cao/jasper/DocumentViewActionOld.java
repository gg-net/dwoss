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

import java.awt.event.ActionEvent;
import java.util.Optional;

import javax.swing.AbstractAction;

import net.sf.jasperreports.engine.JasperPrint;

import org.apache.commons.lang.StringUtils;
import org.openide.util.Lookup;

import eu.ggnet.dwoss.customer.api.CustomerMetaData;
import eu.ggnet.dwoss.customer.api.CustomerService;
import eu.ggnet.dwoss.mandator.api.DocumentViewType;
import eu.ggnet.dwoss.redtape.ee.entity.Document;
import eu.ggnet.dwoss.redtapext.ee.DocumentSupporter;
import eu.ggnet.dwoss.redtapext.ee.RedTapeWorker;
import eu.ggnet.dwoss.redtapext.ee.state.CustomerDocument;
import eu.ggnet.dwoss.redtapext.ee.state.RedTapeStateTransition;
import eu.ggnet.dwoss.redtapext.ui.cao.RedTapeController;
import eu.ggnet.saft.api.Reply;
import eu.ggnet.saft.core.Dl;
import eu.ggnet.saft.core.Ui;
import eu.ggnet.saft.experimental.auth.Guardian;
import eu.ggnet.statemachine.StateTransition;

/**
 * Copy of the old version, if all is successful, this code can be removed.
 * 
 * @author pascal.perau
 */
public class DocumentViewActionOld extends AbstractAction {

    private Document document;

    private DocumentViewType type;

    private RedTapeController controller;

    private long customerId;

    @SuppressWarnings("OverridableMethodCallInConstructor")
    public DocumentViewActionOld(Document document, DocumentViewType type, RedTapeController controller, long customerId) {
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
            JasperPrint print = Dl.remote().lookup(DocumentSupporter.class).render(document, type);
            CustomerService cs = Dl.remote().lookup(CustomerService.class);
            CustomerMetaData customer = cs.asCustomerMetaData(customerId);
            boolean canEmail = !StringUtils.isBlank(cs.defaultEmailCommunication(customerId));

            Ui.exec(() -> {
                Ui.build().parent(controller.getView()).swing().eval(() -> new JRViewerCask(print, document, type, canEmail))
                        .opt()
                        .filter(c -> c.isCorrectlyBriefed())
                        .ifPresent(c -> Ui.progress().call(() -> {
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
                    controller.reloadSelectionOnStateChange(Dl.remote().lookup(DocumentSupporter.class).briefed(document, Dl.local().lookup(Guardian.class).getUsername()));
                    return null;
                }));
            });
        });

    }
}
