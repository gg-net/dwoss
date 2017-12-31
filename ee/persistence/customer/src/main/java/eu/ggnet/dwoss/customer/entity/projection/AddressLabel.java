/*
 * Copyright (C) 2017 GG-Net GmbH
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
package eu.ggnet.dwoss.customer.entity.projection;

import java.util.Optional;

import eu.ggnet.dwoss.configuration.GlobalConfig;
import eu.ggnet.dwoss.customer.entity.*;
import eu.ggnet.dwoss.rules.AddressType;

import lombok.Value;

/**
 * A combination of address, optional company and contact.
 *
 * @author oliver.guenther
 */
@Value
public class AddressLabel {

    private final Optional<Company> optCompany;

    private final Optional<Contact> optContact;

    private final AddressType type;

    /**
     * Returns a HTML representation of the address label.
     * If this label is empty, an appropriated text is shown.
     *
     * @param showType if true shows the type.
     * @return
     */
    public String toHtml() {
        if ( isEmpty() ) return "Keine bevorzugten Addresdaten hinterlegt";
        return optCompany.map(c -> c.getName() + "<br />").orElse("")
                + optContact.map(c -> c.toFullName() + "<br />").orElse("")
                + optContact.map(c -> c.prefered(type)).map(a -> addressLine(a, "<br />")).orElse("");
    }

    /**
     * Prints, what you would expect on a smail envelope.
     *
     * @return multiline string to be printed.
     */
    public String toLabel() {
        if ( isEmpty() ) return "Keine bevorzugten Addresdaten hinterlegt";
        return optCompany.map(c -> c.getName() + System.lineSeparator()).orElse("")
                + optContact.map(c -> c.toFullName() + System.lineSeparator()).orElse("")
                + optContact.map(c -> c.prefered(type)).map(a -> addressLine(a, System.lineSeparator())).orElse("");
    }

    private String addressLine(Address address, String nl) {
        return address.getStreet() + nl
                + (GlobalConfig.LOCAL_ISO_COUNTRY.equalsIgnoreCase(address.getIsoCountry()) ? "" : address.getIsoCountry() + " ")
                + address.getZipCode() + " " + address.getCity();
    }

    /**
     * Returns true if company,contact and address are empty.
     *
     * @return true if company,contact and address are empty.
     */
    public boolean isEmpty() {
        return !optCompany.isPresent() && !optContact.isPresent();
    }

}
