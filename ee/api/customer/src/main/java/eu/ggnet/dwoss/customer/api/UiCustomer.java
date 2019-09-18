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
 * 
 * <p>
 * @author pascal.perau
 */
// TODO: As every field of this class can be null, keep it like this.
public class UiCustomer implements Serializable {

    public long id;

    public String title;

    public String firstName;

    public String lastName;

    public String company;

    public String simpleHtml;

    public String email;

    /**
     * Ledger of the FiBu, if 0 not used.
     */
    private int ledger;

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

    /**
     * Returns id.
     * 
     * @return
     * @deprecated use public field
     */
    @Deprecated
    public long getId() {
        return id;
    }

    /**
     * Returns id.
     * 
     * @return
     * @deprecated use public field
     */
    @Deprecated
    public String getTitle() {
        return title;
    }

    /**
     * Returns id.
     * 
     * @return
     * @deprecated use public field
     */
    @Deprecated
    public String getFirstName() {
        return firstName;
    }

    /**
     * Returns id.
     * 
     * @return
     * @deprecated use public field
     */
    @Deprecated
    public String getLastName() {
        return lastName;
    }

    /**
     * Returns id.
     * 
     * @return
     * @deprecated use public field
     */
    @Deprecated
    public String getCompany() {
        return company;
    }

    /**
     * Returns id.
     * 
     * @return
     * @deprecated use public field
     */
    @Deprecated
    public String getSimpleHtml() {
        return simpleHtml;
    }

    /**
     * Returns id.
     * 
     * @return
     * @deprecated use public field
     */
    @Deprecated
    public String getEmail() {
        return email;
    }

    /**
     * Returns id.
     * 
     * @return
     * @deprecated use public field
     */
    @Deprecated
    public int getLedger() {
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
}
