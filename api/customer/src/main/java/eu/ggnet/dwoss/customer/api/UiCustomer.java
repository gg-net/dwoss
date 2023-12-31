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
package eu.ggnet.dwoss.customer.api;

import java.io.Serializable;

/**
 * A concise representation of a customer.
 * Every field can be null.
 */
public class UiCustomer implements Serializable {

    private final long id;

    private final String title;

    private final String firstName;

    private final String lastName;

    private final String company;

    private final String simpleHtml;

    private final String email;

    /**
     * Customer.additionalCustomerIds(SAGE_FIBU_DEBITOR_LEDGER), 0 if not used.
     */
    public int ledger;

    public UiCustomer(long id, String title, String firstName, String lastName, String company, String simpleHtml, String email, int ledger) {
        this.id = id;
        this.title = title;
        this.firstName = firstName;
        this.lastName = lastName;
        this.company = company;
        this.simpleHtml = simpleHtml;
        this.email = email;
        this.ledger = ledger;
    }

    public long id() {
        return id;
    }

    public String title() {
        return title;
    }

    public String firstName() {
        return firstName;
    }

    public String lastName() {
        return lastName;
    }

    public String company() {
        return company;
    }

    public String simpleHtml() {
        return simpleHtml;
    }

    public String email() {
        return email;
    }

    public int ledger() {
        return ledger;
    }

    public String toNameCompanyLine() {
        StringBuilder sb = new StringBuilder();
        if ( company != null ) sb.append(company).append(" - ");
        if ( title != null ) sb.append(title).append(" ");
        if ( firstName != null ) sb.append(firstName).append(" ");
        if ( lastName != null ) sb.append(lastName);
        return sb.toString();
    }

    public String toTitleNameLine() {
        StringBuilder sb = new StringBuilder();
        sb.append(title != null ? title + " " : "");
        sb.append(firstName != null ? firstName + " " : "");
        sb.append(lastName != null ? lastName : "");
        return sb.toString();
    }

    public String toNameLine() {
        StringBuilder sb = new StringBuilder();
        sb.append(firstName != null ? firstName + " " : "");
        sb.append(lastName != null ? lastName : "");
        return sb.toString();
    }

    public String toIdNameCompanyHtml() {
        StringBuilder sb = new StringBuilder();
        sb.append("<b>").append(id).append("</b> ");
        sb.append(company != null ? "<i>Unternehmen</i><br />" : "<i>Endkunde</i><br />");
        sb.append(company != null ? company + "<br />" : "");
        sb.append(title != null ? title + " " : "");
        sb.append(firstName != null ? firstName + " " : "");
        sb.append(lastName != null ? lastName : "");
        return sb.toString();
    }

    @Override
    public String toString() {
        return "UiCustomer{" + "id=" + id + ", title=" + title + ", firstName=" + firstName + ", lastName=" + lastName + ", company=" + company + ", simpleHtml=" + simpleHtml + ", email=" + email + ", ledger=" + ledger + '}';
    }
    
    
}
