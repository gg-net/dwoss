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

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * A concise representation of a customer.
 * <p>
 * @author pascal.perau
 */
@Data
@AllArgsConstructor
public class UiCustomer implements Serializable {

    private long id;

    private String title;

    private String firstName;

    private String lastName;

    private String company;

    private String simpleHtml;

    private String email;

    /**
     * Ledger of the FiBu, if 0 not used.
     */
    private int ledger;

    public String toNameCompanyLine() {
        StringBuilder sb = new StringBuilder();
        sb.append(company != null ? company + " " : "");
        sb.append(title != null ? title + " " : "");
        sb.append(firstName != null ? firstName + " " : "");
        sb.append(lastName != null ? lastName : "");
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

}
