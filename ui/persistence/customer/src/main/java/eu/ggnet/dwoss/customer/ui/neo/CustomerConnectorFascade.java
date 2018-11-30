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
package eu.ggnet.dwoss.customer.ui.neo;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Consumer;

import eu.ggnet.dwoss.customer.ee.CustomerAgent;
import eu.ggnet.dwoss.customer.ee.CustomerAgent.Root;
import eu.ggnet.dwoss.customer.ee.entity.*;
import eu.ggnet.dwoss.customer.ee.entity.AddressLabel;
import eu.ggnet.saft.api.Reply;
import eu.ggnet.saft.core.Dl;
import eu.ggnet.saft.core.Ui;
import eu.ggnet.saft.core.ui.UiParent;

import lombok.NonNull;

/**
 * Contains all opperations for modification of customer objects in the database.
 * Fasaced of all operations, not really any logic.
 *
 * @author oliver.guenther
 */
public class CustomerConnectorFascade {

    public static Customer updateAddressLabels(long customerId, AddressLabel invoiceLabel, Optional<AddressLabel> shippingLabel) {
        CustomerAgent agent = Dl.remote().lookup(CustomerAgent.class);

        if ( invoiceLabel.getId() < 1l ) {
            agent.create(new Root(Customer.class, customerId), invoiceLabel);
        } else {
            agent.update(invoiceLabel);
        }
        if ( shippingLabel.isPresent() ) {
            if ( shippingLabel.get().getId() < 1l ) {
                agent.create(new Root(Customer.class, customerId), shippingLabel.get());
            } else {
                agent.update(shippingLabel.get());
            }
        }

        return agent.findByIdEager(Customer.class, customerId);
    }

    public static Contact updateAddressOnContact(long contactId, Address address) {
        CustomerAgent agent = Dl.remote().lookup(CustomerAgent.class);
        agent.update(address);
        return agent.findByIdEager(Contact.class, contactId);
    }

    public static Contact deleteAddressOnContact(long contactId, Address address) {
        CustomerAgent agent = Dl.remote().lookup(CustomerAgent.class);
        agent.delete(new Root(Contact.class, contactId), address);
        return agent.findByIdEager(Contact.class, contactId);
    }

    public static Contact createAddressOnContact(long contactId, Address address) {
        CustomerAgent agent = Dl.remote().lookup(CustomerAgent.class);
        agent.create(new Root(Contact.class, contactId), address);
        return agent.findByIdEager(Contact.class, contactId);
        // INFO: DB must fail if contact is part of a bussines customer.
    }

    public static Contact updateCommunicationOnContact(long contactId, Communication communication) {
        CustomerAgent agent = Dl.remote().lookup(CustomerAgent.class);
        agent.update(communication);
        return agent.findByIdEager(Contact.class, contactId);
    }

    public static Contact deleteCommunicationOnContact(long contactId, Communication communication) {
        CustomerAgent agent = Dl.remote().lookup(CustomerAgent.class);
        agent.delete(new Root(Contact.class, contactId), communication);
        return agent.findByIdEager(Contact.class, contactId);
    }

    public static Contact createCommunicationOnContact(long contactId, Communication communication) {
        CustomerAgent agent = Dl.remote().lookup(CustomerAgent.class);
        agent.create(new Root(Contact.class, contactId), communication);
        return agent.findByIdEager(Contact.class, contactId);
    }

    public static Customer createContactOnCustomer(long customerid, Contact contact) {
        CustomerAgent agent = Dl.remote().lookup(CustomerAgent.class);
        agent.create(new Root(Customer.class, customerid), contact);
        return agent.findByIdEager(Customer.class, customerid);
    }

    public static Customer updateContactOnCustomer(long customerId, Contact contact) {
        CustomerAgent agent = Dl.remote().lookup(CustomerAgent.class);
        agent.update(contact);
        return agent.findByIdEager(Customer.class, customerId);
    }

    public static Customer deleteContactOnCustomer(long customerId, Contact contact) {
        CustomerAgent agent = Dl.remote().lookup(CustomerAgent.class);
        agent.delete(new Root(Customer.class, customerId), contact);
        return agent.findByIdEager(Customer.class, customerId);
    }

    public static Customer createCompanyOnCustomer(long customerId, Company company) {
        CustomerAgent agent = Dl.remote().lookup(CustomerAgent.class);
        agent.create(new Root(Customer.class, customerId), company);
        return agent.findByIdEager(Customer.class, customerId);
    }

    public static Customer updateCompanyOnCustomer(long customerId, Company company) {
        CustomerAgent agent = Dl.remote().lookup(CustomerAgent.class);
        agent.update(company);
        return agent.findByIdEager(Customer.class, customerId);
    }

    public static Customer deleteCompanyOnCustomer(long customerId, Company company) {
        CustomerAgent agent = Dl.remote().lookup(CustomerAgent.class);
        agent.delete(new Root(Customer.class, customerId), company);
        return agent.findByIdEager(Customer.class, customerId);
    }

    public static Company createCommunicationOnCompany(long companyId, Communication communication) {
        CustomerAgent agent = Dl.remote().lookup(CustomerAgent.class);
        agent.create(new Root(Company.class, companyId), communication);
        return agent.findByIdEager(Company.class, companyId);
    }

    public static Company updateCommunicationOnCompany(long companyId, Communication communication) {
        CustomerAgent agent = Dl.remote().lookup(CustomerAgent.class);
        agent.update(communication);
        return agent.findByIdEager(Company.class, companyId);
    }

    public static Company deleteCommunicationOnCompany(long companyId, Communication communication) {
        CustomerAgent agent = Dl.remote().lookup(CustomerAgent.class);
        agent.delete(new Root(Company.class, companyId), communication);
        return agent.findByIdEager(Company.class, companyId);
    }

    public static Customer reload(Customer customer) {
        CustomerAgent agent = Dl.remote().lookup(CustomerAgent.class);
        return agent.findByIdEager(Customer.class, customer.getId());
    }

    public static Company reload(Company company) {
        CustomerAgent agent = Dl.remote().lookup(CustomerAgent.class);
        return agent.findByIdEager(Company.class, company.getId());
    }

    public static Company updateAddressOnCompany(long companyId, Address address) {
        CustomerAgent agent = Dl.remote().lookup(CustomerAgent.class);
        agent.update(address);
        return agent.findByIdEager(Company.class, companyId);
    }

    public static Company deleteAddressOnCompany(long companyId, Address address) {
        CustomerAgent agent = Dl.remote().lookup(CustomerAgent.class);
        agent.delete(new Root(Company.class, companyId), address);
        return agent.findByIdEager(Company.class, companyId);
    }

    public static Company createAddressOnCompany(long companyId, Address address) {
        CustomerAgent agent = Dl.remote().lookup(CustomerAgent.class);
        agent.create(new Root(Company.class, companyId), address);
        return agent.findByIdEager(Company.class, companyId);
    }

    public static Company createContactOnCompany(long companyId, Contact contact) {
        CustomerAgent agent = Dl.remote().lookup(CustomerAgent.class);
        agent.create(new Root(Company.class, companyId), contact);
        return agent.findByIdEager(Company.class, companyId);
    }

    public static Company updateContactOnCompany(long companyId, Contact contact) {
        CustomerAgent agent = Dl.remote().lookup(CustomerAgent.class);
        agent.update(contact);
        return agent.findByIdEager(Company.class, companyId);
    }

    public static Company deleteContactOnCompany(long companyId, Contact contact) {
        CustomerAgent agent = Dl.remote().lookup(CustomerAgent.class);
        agent.delete(new Root(Company.class, companyId), contact);
        return agent.findByIdEager(Company.class, companyId);
    }

    public static Customer createOrUpdateMandatorMetadata(long customerId, MandatorMetadata mandatorMetadata) {
        CustomerAgent agent = Dl.remote().lookup(CustomerAgent.class);
        if ( mandatorMetadata.getId() == 0 ) agent.create(new Root(Customer.class, customerId), mandatorMetadata);
        else agent.update(mandatorMetadata);

        return agent.findByIdEager(Customer.class, customerId);
    }

    /**
     * Open the Uis for editing an existing customer, simple or enhance.
     *
     * @param c the customer, must not be null
     * @param p a parent for the ui
     */
    public static void edit(@NonNull Customer c, UiParent p) {
        if ( c.isSimple() ) {
            Ui.build().parent(p).fxml().eval(() -> c, CustomerSimpleController.class).cf()
                    .thenApply(CustomerConnectorFascade::optionalStore)
                    .thenCompose(cc -> CustomerConnectorFascade.optionalEnhancedEditorAndStore(p, cc))
                    .handle(Ui.handler());
        } else {
            CompletableFuture.completedFuture(CustomerCommand.enhance(c))
                    .thenCompose(cc -> CustomerConnectorFascade.optionalEnhancedEditorAndStore(p, cc))
                    .handle(Ui.handler());
        }
    }

    /**
     * Opens the Uis to create a new customer.
     *
     * @param p a parent for the ui
     */
    public static void create(UiParent p) {
        Ui.build().parent(p).fxml().eval(CustomerSimpleController.class).cf()
                .thenApply(CustomerConnectorFascade::optionalStore)
                .thenCompose(cc -> CustomerConnectorFascade.optionalEnhancedEditorAndStore(p, cc))
                .handle(Ui.handler());
    }

    /**
     * Opens the Uis to select and optionaly edit or create a new customer.
     *
     * @param p           a parent for the ui
     * @param selectedcid a consumer for the id of the customer, which becomes selected at the end.
     */
    public static void selectOrEdit(UiParent p, Consumer<Long> selectedcid) {
        Ui.build().parent(p).fxml().eval(CustomerSimpleController.class).cf()
                .thenApply(CustomerConnectorFascade::optionalStore)
                .thenCompose(cc -> CustomerConnectorFascade.optionalEnhancedEditorAndStore(p, cc))
                .thenAccept(c -> selectedcid.accept(c.getId()))
                .handle(Ui.handler());
    }

    private static CustomerCommand optionalStore(CustomerCommand cc) {
        if ( !cc.simpleStore ) return cc;
        Reply<Customer> reply = Dl.remote().lookup(CustomerAgent.class).store(cc.simpleCustomer);
        if ( !Ui.failure().handle(reply) ) return null; // TODO: Switch to Exception
        return cc.enhance
                ? CustomerCommand.enhance(reply.getPayload())
                : CustomerCommand.select(reply.getPayload());
    }

    private static CompletionStage<Customer> optionalEnhancedEditorAndStore(UiParent p, CustomerCommand cc) {
        if ( !cc.enhance ) return CompletableFuture.completedFuture(cc.customer);
        return Ui.build().parent(p).fxml().eval(() -> cc.customer, CustomerEnhanceController.class).cf()
                .thenApply(c -> Dl.remote().lookup(CustomerAgent.class).update(c));
    }

}
