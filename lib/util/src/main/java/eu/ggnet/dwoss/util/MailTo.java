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
package eu.ggnet.dwoss.util;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;

/**
 *
 * @author oliver.guenther
 */
public class MailTo {

    private final String to;

    private String subject;

    private String body;

    public MailTo(String to) {
        this.to = to;
    }

    public MailTo setSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public MailTo setBody(String body) {
        this.body = body;
        return this;
    }

    public URI toUri() {
        try {
            return new URI("mailto:" + to + "?subject=" + URLEncoder.encode(subject, "UTF-8").replaceAll("\\+", "%20")
                    + "&body=" + URLEncoder.encode(body, "UTF-8").replaceAll("\\+", "%20"));
        } catch (URISyntaxException | IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
