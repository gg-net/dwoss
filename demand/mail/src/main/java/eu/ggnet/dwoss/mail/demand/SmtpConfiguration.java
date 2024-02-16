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
package eu.ggnet.dwoss.mail.demand;

import java.io.Serializable;
import java.util.Objects;

/**
 * Configuration for SMTP Operations.
 * <p/>
 * @author oliver.guenther
 */
public class SmtpConfiguration implements Serializable {

    public final String hostname;

    public final String smtpAuthenticationUser;

    public final String smtpAuthenticationPass;

    public final String charset;

    public final boolean useStartTls;

    public final boolean useSsl;

    public String toHtml() {
        return "<p>"
                + "Host:&nbsp;" + hostname + "<br />"
                + "User:&nbsp;" + smtpAuthenticationUser + "<br />"
                + "Pass:&nbsp;" + smtpAuthenticationPass + "<br />"
                + "Charset:&nbsp;" + charset + "<br />"
                + "StartTls:&nbsp;" + useStartTls + "<br />"
                + "Ssl:&nbsp;" + useSsl + "</p>";
    }

    /**
     *
     * @param hostname               the hostname, must not be null
     * @param smtpAuthenticationUser the user, may be null which implies no authentication
     * @param smtpAuthenticationPass the pass, may be null which implies no authentication
     * @param charset                the charset to use, must not be null
     * @param useStartTls
     * @param useSsl
     */
    public SmtpConfiguration(String hostname, String smtpAuthenticationUser, String smtpAuthenticationPass, String charset, boolean useStartTls, boolean useSsl) {
        this.hostname = Objects.requireNonNull(hostname, "new SmtpConfiguration with hostname=null called, not allowed");
        this.smtpAuthenticationUser = smtpAuthenticationUser;
        this.smtpAuthenticationPass = smtpAuthenticationPass;
        this.charset = Objects.requireNonNull(charset, "new SmtpConfiguration with charset=null called, not allowed");
        this.useStartTls = useStartTls;
        this.useSsl = useSsl;
    }

}
