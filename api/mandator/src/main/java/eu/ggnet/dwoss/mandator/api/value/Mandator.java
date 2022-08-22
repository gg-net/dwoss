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
package eu.ggnet.dwoss.mandator.api.value;

import java.io.Serializable;
import java.util.Map.Entry;
import java.util.*;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;
import org.inferred.freebuilder.FreeBuilder;

import eu.ggnet.dwoss.core.common.values.DocumentType;
import eu.ggnet.dwoss.mandator.api.value.partial.*;

/**
 *
 * @author oliver.guenther
 */
// @Value
@FreeBuilder
public abstract class Mandator implements Serializable {

    public static class Builder extends Mandator_Builder {
    };

    public abstract String defaultMailSignature();

    public abstract SmtpConfiguration smtpConfiguration();

    public abstract UrlLocation mailTemplateLocation();

    public abstract Set<MandatorMailAttachment> defaultMailAttachment();

    public abstract Map<DocumentType, Set<MandatorMailAttachment>> mailAttachmentByDocumentType();

    public abstract Company company();

    public abstract String dossierPrefix();

    public abstract DocumentIntermix documentIntermix();

    public abstract Map<DocumentType, DocumentIdentifierGeneratorConfiguration> documentIdentifierGeneratorConfigurations();

    public abstract boolean applyDefaultChannelOnRollIn();

    public abstract String matchCode();

    public abstract String bugMail();

    /**
     * Prepares a eMail to be send direct over the mandator smtp configuration.
     * The email is missing: to, subject, message and optional attachments.
     *
     * @return the email
     * @throws EmailException if something is wrong in the subsystem.
     */
    public MultiPartEmail prepareDirectMail() throws EmailException {
        MultiPartEmail email = new MultiPartEmail();
        email.setHostName(smtpConfiguration().hostname);
        email.addBcc(company().email());
        email.setFrom(company().email(), company().emailName());
        if ( smtpConfiguration().smtpAuthenticationUser != null && smtpConfiguration().smtpAuthenticationPass != null ) {
            email.setAuthentication(smtpConfiguration().smtpAuthenticationUser, smtpConfiguration().smtpAuthenticationPass);
        }
        email.setStartTLSEnabled(smtpConfiguration().useStartTls);
        email.setSSLCheckServerIdentity(false);
        email.setSSLOnConnect(smtpConfiguration().useSsl);
        email.setCharset(smtpConfiguration().charset);
        return email;
    }

    public String toHtml() {
        StringBuilder sb = new StringBuilder("<table>");
        sb.append("<tr>");
        sb.append("<td><p><b>Company</b></p>");
        sb.append(company().toHtml());
        sb.append("</td>");
        sb.append("<td><p><b>Smtp Configuration</b></p>");
        sb.append(smtpConfiguration().toHtml());
        sb.append("</td>");
        sb.append("</tr>");

        sb.append("<tr><td colspan=\"2\"><ul>");
        sb.append("<li><b>Dossier Prefix: </b>");
        sb.append(dossierPrefix());
        sb.append("</li><li><b>MailTemplateLocation: </b>");
        sb.append(mailTemplateLocation().getLocation());
        sb.append("</li><li><b>ApplyDefaultChannelOnRollIn: </b>");
        sb.append(applyDefaultChannelOnRollIn());
        sb.append("</li><li><b>matchCode: </b>");
        sb.append(matchCode());
        sb.append("</li><li><b>Bug Report Mail:</b>");
        sb.append(bugMail());
        sb.append("</li>");

        sb.append("</td></tr><tr><td colspan=\"2\">");
        sb.append("<p><b>DocumentIntermix</b></p>");
        sb.append(documentIntermix().toHtml());
        sb.append("</td></tr>");

        sb.append("<tr><td colspan=\"2\"><p><b>DefaultMailSignature</b></p>");
        sb.append(defaultMailSignature());
        sb.append("</td></tr>");

        sb.append("<tr><td colspan=\"2\"><p><b>Default Mail Attachment:</b></p>");
        if ( defaultMailAttachment().isEmpty() ) {
            sb.append("<b>No Attachment</b>");
        } else {
            Iterator<MandatorMailAttachment> it = defaultMailAttachment().iterator();
            sb.append("<ul>");
            while (it.hasNext()) {
                MandatorMailAttachment attachment = it.next();
                sb.append("<li>");
                sb.append(attachment);
                sb.append("</li>");
            }
            sb.append("</ul>");
        }
        sb.append("</td></tr>");

        sb.append("<tr><td colspan=\"2\"><p><b>Mail Attachment by Document Type:</b></p>");
        if ( mailAttachmentByDocumentType().isEmpty() ) {
            sb.append("<b>No Attachment</b>");
        } else {
            Iterator<Entry<DocumentType, Set<MandatorMailAttachment>>> it = mailAttachmentByDocumentType().entrySet().iterator();
            sb.append("<ul>");
            while (it.hasNext()) {
                Entry<DocumentType, Set<MandatorMailAttachment>> entry = it.next();
                sb.append("<li>");
                sb.append(entry.getKey());
                sb.append(" -> ");
                sb.append(entry.getValue());
                sb.append("</li>");
            }
            sb.append("</ul>");
        }
        sb.append("</td></tr>");

        sb.append("<tr><td colspan=\"2\"><p><b>Document Identifier Generator Configurations:</b></p>");
        if ( documentIdentifierGeneratorConfigurations().isEmpty() ) {
            sb.append("<b>No Document Identifier Generator Configuration</b>");
        } else {
            sb.append("<ul>");
            documentIdentifierGeneratorConfigurations().forEach((type, generatorConfig) -> {
                sb.append("<li>");
                sb.append(type);
                sb.append(" : ");
                sb.append(generatorConfig);
                sb.append("</li>");
            });
            sb.append("</ul>");
        }

        sb.append("</td></tr>");

        sb.append("<table>");
        return sb.toString();
    }

}
