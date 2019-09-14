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
package eu.ggnet.dwoss.mandator.api.value.partial;

import java.io.Serializable;

import org.inferred.freebuilder.FreeBuilder;

/**
 * Masterdata for Mandator.
 * <p>
 * @author oliver.guenther
 */
@FreeBuilder
public abstract class Company implements Serializable {

    public abstract String name();

    public abstract String street();

    public abstract String city();

    public abstract String zip();

    public abstract UrlLocation logo();

    public abstract String email();

    public abstract String emailName();

    public String toSingleLine() {
        return name() + " - " + street() + " - " + zip() + " " + city();
    }

    /**
     * ToString HTML representation.
     *
     * @return HTML view of the Company.
     */
    public String toHtml() {
        return "<p>"
                + name() + "( Logo:" + logo() + " )<br />"
                + street() + "<br />"
                + zip() + "&nbsp;" + city() + "<br />"
                + "email:&nbsp;" + emailName() + "&nbsp;&lt;" + email() + "&gt;"
                + "</p>";
    }

    public static class Builder extends Company_Builder {
    };
}
