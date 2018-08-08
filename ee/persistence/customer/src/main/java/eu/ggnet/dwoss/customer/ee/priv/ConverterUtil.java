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
package eu.ggnet.dwoss.customer.ee.priv;

import java.util.EnumSet;
import java.util.HashSet;

import org.apache.commons.lang3.StringUtils;

import eu.ggnet.dwoss.customer.ee.entity.Communication.Type;
import eu.ggnet.dwoss.customer.ee.entity.*;
import eu.ggnet.dwoss.mandator.api.value.DefaultCustomerSalesdata;
import eu.ggnet.dwoss.common.api.values.CustomerFlag;
import eu.ggnet.dwoss.common.api.values.SalesChannel;

import static eu.ggnet.dwoss.customer.ee.entity.Communication.Type.*;
import static eu.ggnet.dwoss.customer.ee.entity.Contact.Sex.FEMALE;
import static eu.ggnet.dwoss.customer.ee.entity.Contact.Sex.MALE;
import static eu.ggnet.dwoss.common.api.values.AddressType.INVOICE;
import static eu.ggnet.dwoss.common.api.values.AddressType.SHIPPING;

/**
 * Utility Class to convert a (sopo)OldCustomer to New and visa verse.
 * <p>
 * @author oliver.guenther
 */
public class ConverterUtil {

    /**
     * Converts a customer to old customer.
     * <p>
     * @param c                 the customer
     * @param mandatorMatchCode the mandator matchcode
     * @param defaults          the defaults
     * @return the converted old customer
     * @deprecated use only customer any more.
     */
    @Deprecated
    public static OldCustomer convert(Customer c, String mandatorMatchCode, DefaultCustomerSalesdata defaults) {
        OldCustomer old = new OldCustomer();
        old.setId((int)c.getId());
        old.setAnmerkung(c.getComment());
        for (CustomerFlag flag : c.getFlags()) {
            old.addFlag(flag);
        }
        old.getAdditionalCustomerIds().putAll(c.getAdditionalCustomerIds());
        old.setKeyAccounter(c.getKeyAccounter());
        old.setSource(c.getSource());
        if ( !c.getCompanies().isEmpty() ) {
            Company company = c.getCompanies().get(0);
            old.setFirma(company.getName());
            old.setLedger(company.getLedger());
            old.setTaxId(company.getTaxId());
        }
        if ( !c.getContacts().isEmpty() ) {
            Contact contact = c.getContacts().get(0);
            old.setVorname(contact.getFirstName());
            old.setNachname(contact.getLastName());
            if ( contact.getSex() != null )
                switch (contact.getSex()) {
                    case MALE:
                        old.setTitel("Herr");
                        break;
                    case FEMALE:
                        old.setTitel("Frau");
                        break;
                }
            for (Communication com : contact.getCommunications()) {
                set(old, com.getType(), com.getIdentifier());
            }
            for (Address address : contact.getAddresses()) {
                switch (address.getPreferedType()) {
                    case INVOICE:
                        old.setREAdresse(address.getStreet());
                        old.setREOrt(address.getCity());
                        old.setREPlz(address.getZipCode());
                        break;
                    case SHIPPING:
                        old.setLIAdresse(address.getStreet());
                        old.setLIOrt(address.getCity());
                        old.setLIPlz(address.getZipCode());
                        break;
                }
            }
        }
        MandatorMetadata.MergedView metadata = new MandatorMetadata.MergedView(c.getMandatorMetadata(mandatorMatchCode), defaults);
        old.setAllowedSalesChannels(new HashSet<>(metadata.getAllowedSalesChannels()));
        old.setPaymentCondition(metadata.getPaymentCondition());
        old.setPaymentMethod(metadata.getPaymentMethod());
        old.setShippingCondition(metadata.getShippingCondition());
        return old;
    }

    /**
     * Merges the old customer to a supplied new customer instance.
     * The new customer has all information of the old customer set to the first contact.
     * A Company may be added if the old.firma is not blank.
     * This Method also assumes, that there is exactly one Mandator used in creation with its defaults.
     * <p>
     * @param old               old customer
     * @param customer
     * @param mandatorMatchCode the mandatorMatchCode
     * @param defaults          the defaults as filter for specific mandator data.
     * @return new customer.
     */
    public static Customer mergeFromOld(OldCustomer old, Customer customer, String mandatorMatchCode, DefaultCustomerSalesdata defaults) {
        customer.setComment(old.getAnmerkung());
        customer.getFlags().clear();
        for (CustomerFlag customerFlag : old.getFlags()) {
            customer.getFlags().add(customerFlag);
        }
        customer.getAdditionalCustomerIds().clear();
        customer.getAdditionalCustomerIds().putAll(old.getAdditionalCustomerIds());
        customer.setSource(old.getSource());
        customer.setKeyAccounter(old.getKeyAccounter());
        if ( customer.getContacts().isEmpty() ) customer.getContacts().add(new Contact());
        Contact contact = customer.getContacts().get(0);
        contact.setFirstName(old.getVorname() == null ? "" : old.getVorname());
        contact.setLastName(old.getNachname() == null ? "" : old.getNachname());
        if ( old.getTitel() != null ) {
            switch (old.getTitel()) {
                case "Herr":
                    contact.setSex(MALE);
                    break;
                case "Frau":
                    contact.setSex(FEMALE);
                    break;
                default:
            }
        }
        contact.setPrefered(true);
        if ( !StringUtils.isBlank(old.getFirma()) || !customer.getCompanies().isEmpty() ) {
            if ( customer.getCompanies().isEmpty() ) customer.getCompanies().add(new Company());
            Company company = customer.getCompanies().get(0);
            company.setName(old.getFirma());
            company.setLedger(old.getLedger());
            company.setTaxId(old.getTaxId());
            company.setPrefered(true);
        }
        for (Type t : EnumSet.of(EMAIL, FAX, PHONE, MOBILE)) {
            if ( !StringUtils.isBlank(get(old, t)) || contact.prefered(t) != null ) {
                if ( contact.prefered(t) == null ) contact.getCommunications().add(new Communication(t, true));
                contact.prefered(t).setIdentifier(get(old, t));
            }
        }

        if ( !StringUtils.isBlank(old.getREAdresse()) || contact.prefered(INVOICE) != null ) {
            if ( contact.prefered(INVOICE) == null ) contact.getAddresses().add(new Address(INVOICE));
            Address rad = contact.prefered(INVOICE);
            rad.setStreet(old.getREAdresse());
            rad.setCity(old.getREOrt() == null ? "" : old.getREOrt());
            rad.setZipCode(old.getREPlz() == null ? "" : old.getREPlz());
        }
        if ( !StringUtils.isBlank(old.getLIAdresse()) || contact.prefered(SHIPPING) != null ) {
            if ( contact.prefered(SHIPPING) == null ) contact.getAddresses().add(new Address(SHIPPING));
            Address sad = contact.prefered(SHIPPING);
            sad.setStreet(old.getLIAdresse());
            sad.setCity(old.getLIOrt() == null ? "" : old.getLIOrt());
            sad.setZipCode(old.getLIPlz() == null ? "" : old.getLIPlz());
        }
        MandatorMetadata m = customer.getMandatorMetadata(mandatorMatchCode);
        if ( m == null ) m = new MandatorMetadata();
        m.setMandatorMatchcode(mandatorMatchCode);
        m.clearSalesChannels();
        if ( !old.getAllowedSalesChannels().equals(defaults.getAllowedSalesChannels()) ) {
            for (SalesChannel salesChannel : old.getAllowedSalesChannels()) {
                m.add(salesChannel);
            }
        }
        if ( old.getPaymentCondition() == defaults.getPaymentCondition() ) m.setPaymentCondition(null);
        else m.setPaymentCondition(old.getPaymentCondition());
        if ( old.getPaymentMethod() == defaults.getPaymentMethod() ) m.setPaymentMethod(null);
        else m.setPaymentMethod(old.getPaymentMethod());
        if ( old.getShippingCondition() == defaults.getShippingCondition() ) m.setShippingCondition(null);
        else m.setShippingCondition(old.getShippingCondition());
        if ( customer.getMandatorMetadata(mandatorMatchCode) == null && m.isSet() ) customer.getMandatorMetadata().add(m);
        return customer;
    }

    private static String get(OldCustomer old, Communication.Type type) {
        switch (type) {
            case EMAIL:
                return old.getEmail();
            case FAX:
                return old.getFaxnummer();
            case PHONE:
                return old.getTelefonnummer();
            case MOBILE:
                return old.getHandynummer();
        }
        return null;
    }

    private static void set(OldCustomer old, Communication.Type type, String value) {
        switch (type) {
            case EMAIL:
                old.setEmail(value);
                break;
            case FAX:
                old.setFaxnummer(value);
                break;
            case PHONE:
                old.setTelefonnummer(value);
                break;
            case MOBILE:
                old.setHandynummer(value);
        }
    }

}
