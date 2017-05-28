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
package eu.ggnet.dwoss.redtape.workflow;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Set;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.customer.api.*;
import eu.ggnet.dwoss.mandator.api.value.Mandator;
import eu.ggnet.dwoss.mandator.api.value.SpecialSystemCustomers;
import eu.ggnet.dwoss.redtape.assist.RedTapes;
import eu.ggnet.dwoss.redtape.emo.AddressEmo;
import eu.ggnet.dwoss.redtape.entity.Document.Directive;
import eu.ggnet.dwoss.redtape.entity.*;
import eu.ggnet.dwoss.redtape.format.DossierFormater;
import eu.ggnet.dwoss.rules.*;

import static eu.ggnet.dwoss.rules.CustomerFlag.*;
import static eu.ggnet.dwoss.rules.DocumentType.BLOCK;

/**
 * Workflow to create a Dossier with one document of the selected Type.
 *
 * @author oliver.guenther
 */
public class RedTapeCreateDossierWorkflow {

    protected final static Logger L = LoggerFactory.getLogger(RedTapeCreateDossierWorkflow.class);

    private final static NumberFormat _00000_ = new DecimalFormat("00000");

    @Inject
    @RedTapes
    private EntityManager redTapeEm;

    @Inject
    private AddressService addressService;

    @Inject
    private CustomerService customerService;

    @Inject
    private Mandator mandator;

    @Inject
    private SpecialSystemCustomers specialSystemCustomers;

    /**
     * Returns the first Directive on a created Document.
     * See Implementation. Uses Dispatch.
     *
     * @param type          the {@link DocumentType}
     * @param paymentMethod the payment method
     * @param flags         the customerFlags
     * @param dispatch      value specifying weither this is a dispatch process
     * @return the Directive.
     */
    public static Document.Directive primeDirective(DocumentType type, PaymentMethod paymentMethod, Set<CustomerFlag> flags, boolean dispatch) {
        if ( paymentMethod == null ) throw new NullPointerException("PaymentMethod is Null");
        if ( type == DocumentType.BLOCK ) return Directive.NONE;
        if ( type == DocumentType.CAPITAL_ASSET ) return Directive.HAND_OVER_GOODS;
        // Default für Block, Returns und Anlagevermögen.
        switch (paymentMethod) {
            case CASH_ON_DELIVERY: // Implies dispatch
                if ( !dispatch ) throw new IllegalArgumentException("A PickUp Order cannot be of PaymentMethod " + paymentMethod);
                if ( flags.contains(CONFIRMED_CASH_ON_DELIVERY) && flags.contains(CONFIRMS_DOSSIER) ) return Directive.SEND_ORDER;
                if ( flags.contains(CONFIRMED_CASH_ON_DELIVERY) ) return Directive.PREPARE_SHIPPING;
                return Directive.SEND_CASH_ON_DELIVERY_CONTRACT;
            case ADVANCE_PAYMENT:
                if ( dispatch ) return Directive.SEND_ORDER;
                return Directive.WAIT_FOR_MONEY;
            case DIRECT_DEBIT:
            case INVOICE:
                if ( dispatch && flags.contains(CONFIRMS_DOSSIER) ) return Directive.SEND_ORDER;
                if ( dispatch ) return Directive.PREPARE_SHIPPING;
                // This is also ok for Returns and Capital Asset
                return Directive.HAND_OVER_GOODS;
            default:
        }
        throw new IllegalArgumentException("A Prime Directive for " + paymentMethod
                + ", " + flags + "dispatch=" + dispatch + " not found");
    }

    private PaymentMethod selectPaymentMethod(boolean dispatch, CustomerMetaData customer) {
        // A PickUp Order with Cash on Delivery is impossible.
        if ( !dispatch && customer.getPaymentMethod() == PaymentMethod.CASH_ON_DELIVERY ) return PaymentMethod.ADVANCE_PAYMENT;
        else return customer.getPaymentMethod();
    }

    /**
     * Creates the Dossier.
     *
     * @param customer the customer
     * @return the Dossier.
     */
    Dossier createDossier(long customerId, boolean dispatch, DocumentType type, PaymentMethod paymentMethod, Directive directive, String arranger) {
        if ( specialSystemCustomers.get(customerId).map(x -> x != type).orElse(false) ) {
            throw new IllegalStateException(type + " is not allowed for Customer " + customerId);
        }

        Dossier dos = new Dossier();
        dos.setPaymentMethod(paymentMethod);
        dos.setDispatch(dispatch);
        dos.setCustomerId(customerId);

        Document doc = new Document();
        doc.setType(type);
        doc.setActive(true);
        doc.setDirective(directive);
        doc.setHistory(new DocumentHistory(arranger, "Automatische Erstellung eines leeren Dokuments"));

        AddressEmo adEmo = new AddressEmo(redTapeEm);
        doc.setInvoiceAddress(adEmo.request(addressService.defaultAddressLabel(customerId, AddressType.INVOICE)));
        doc.setShippingAddress(adEmo.request(addressService.defaultAddressLabel(customerId, AddressType.SHIPPING)));
        dos.add(doc);

        redTapeEm.persist(dos);
        redTapeEm.flush(); // Make sure the dos.id is generated an stored in the database. 
        dos.setIdentifier(mandator.getDossierPrefix() + _00000_.format(dos.getId()));
        redTapeEm.flush(); // Force store Identifier
        L.info("Created {} by {}", DossierFormater.toSimpleLine(dos), arranger);
        return dos;
    }

    /**
     * Executes the Workflow.
     *
     * @param customerId
     * @param dispatch
     * @param arranger
     * @return the Dossier.
     */
    public Dossier execute(long customerId, boolean dispatch, String arranger) {
        DocumentType type = DocumentType.ORDER;
        CustomerMetaData customer = customerService.asCustomerMetaData(customerId);
        L.debug("Found Customer: {}", customer);
        if ( customer.getFlags().contains(SYSTEM_CUSTOMER) ) {
            type = specialSystemCustomers.get(customerId).orElse(BLOCK);
            L.debug("CustomerId {} is SystemCustomer, using DocumentType: {}, source {}", customerId, type, specialSystemCustomers);
        }
        PaymentMethod paymentMethod = selectPaymentMethod(dispatch, customer);
        Directive directive = primeDirective(type, paymentMethod, customer.getFlags(), dispatch);
        return createDossier(customerId, dispatch, type, paymentMethod, directive, arranger);
    }
}
