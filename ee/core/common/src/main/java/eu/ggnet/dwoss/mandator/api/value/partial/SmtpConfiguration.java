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

import lombok.Data;

/**
 * Configuration for SMTP Operations.
 * <p/>
 * @author oliver.guenther
 */
@Data
public class SmtpConfiguration implements Serializable {

    private final String hostname;

    private final String smtpAuthenticationUser;

    private final String smtpAuthenticationPass;

    private final String charset;

    private final boolean useStartTls;

    public String toHtml() {
        return "<p>"
                + "Host:&nbsp;" + hostname + "<br />"
                + "User:&nbsp;" + smtpAuthenticationUser + "<br />"
                + "Pass:&nbsp;" + smtpAuthenticationPass + "<br />"
                + "Charset:&nbsp;" + charset + "<br />"
                + "StartTls:&nbsp;" + useStartTls + "</p>";
    }

}
