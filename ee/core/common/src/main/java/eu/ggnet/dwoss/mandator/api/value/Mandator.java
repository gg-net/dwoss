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
import java.util.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;
import org.metawidget.inspector.annotation.UiLarge;

import eu.ggnet.dwoss.mandator.api.value.partial.*;
import eu.ggnet.dwoss.rules.DocumentType;
import eu.ggnet.dwoss.rules.TradeName;

import lombok.Builder;
import lombok.Value;

/**
 *
 * @author oliver.guenther
 */
@Value
@Builder
public class Mandator implements Serializable {

    /**
     * The default Signature below mails.
     */
    @UiLarge
    @NotNull
    private final String defaultMailSignature;

    /**
     * A Smtp Configuration, used for direct mailing.
     */
    @Valid
    @NotNull
    private final SmtpConfiguration smtpConfiguration;

    /**
     * A generic velocity template for emails.
     * Available Parameters are:
     * <ul>
     * <li>$parameter.neutralTitle - Boolean, is true if neither Herr nor Frau is set.</li>
     * <li>$parameter.name - last name of the customer</li>
     * <li>$parameter.title - the title of the customer</li>
     * <li>$parameter.documentType - the documentType</li>
     * </ul>
     */
    @NotNull
    private final UrlLocation mailTemplateLocation;

    /**
     * File used as default attachment when sending mails.
     */
    @NotNull
    private Set<MandatorMailAttachment> defaultMailAttachment;

    /**
     * The company master data information.
     */
    @NotNull
    private final Company company;

    /**
     * The Prefix for Dossiers.
     */
    @NotNull
    private final String dossierPrefix;

    @NotNull
    private final DocumentIntermix documentIntermix;

    @NotNull
    private final Map<DocumentType, DocumentIdentifierGeneratorConfiguration> documentIdentifierGeneratorConfigurations;

    /**
     * The default Mode for Receipt, for now may be null,Acer or Apple.
     */
    private final TradeName receiptMode;

    @Deprecated // Remove if ServicePositionTemplate cleanup complete
    public TradeName getReceiptMode() {
        return receiptMode;
    }

    /**
     * Defines, if the primary sales channel should be set on roll in.
     */
    private final boolean applyDefaultChannelOnRollIn;

    @NotNull
    private final String matchCode;

    private String bugMail;

    /**
     * Prepares a eMail to be send direct over the mandator smtp configuration.
     * The email is missing: to, subject, message and optional attachments.
     *
     * @return the email
     * @throws EmailException if something is wrong in the subsystem.
     */
    public MultiPartEmail prepareDirectMail() throws EmailException {
        MultiPartEmail email = new MultiPartEmail();
        email.setHostName(smtpConfiguration.getHostname());
        email.addBcc(company.getEmail());
        email.setFrom(company.getEmail(), company.getEmailName());
        email.setAuthentication(smtpConfiguration.getSmtpAuthenticationUser(), smtpConfiguration.getSmtpAuthenticationPass());
        email.setStartTLSEnabled(false);
        email.setSSLCheckServerIdentity(false);
        email.setSSLOnConnect(false);
        email.setCharset(smtpConfiguration.getCharset());
        return email;
    }

    public String toHtml() {
        StringBuilder sb = new StringBuilder("<table>");
        sb.append("<tr>");
        sb.append("<td><p><b>Company</b></p>");
        sb.append(company == null ? "null" : company.toHtml());
        sb.append("</td>");
        sb.append("<td><p><b>Smtp Configuration</b></p>");
        sb.append(smtpConfiguration == null ? "null" : smtpConfiguration.toHtml());
        sb.append("</td>");
        sb.append("</tr>");

        sb.append("<tr><td colspan=\"2\"><ul>");
        sb.append("<li><b>ReceiptMode: </b>");
        sb.append(receiptMode);
        sb.append("</li><li><b>Dossier Prefix: </b>");
        sb.append(dossierPrefix);
        sb.append("</li><li><b>MailTemplateLocation: </b>");
        sb.append(mailTemplateLocation == null ? "null" : mailTemplateLocation.getLocation());
        sb.append("</li><li><b>ApplyDefaultChannelOnRollIn: </b>");
        sb.append(applyDefaultChannelOnRollIn);
        sb.append("</li><li><b>matchCode: </b>");
        sb.append(matchCode);
        sb.append("</li><li><b>Bug Report Mail:</b>");
        sb.append(bugMail);
        sb.append("</li>");

        sb.append("</td></tr><tr><td colspan=\"2\">");
        sb.append("<p><b>DocumentIntermix</b></p>");
        sb.append(documentIntermix == null ? "null" : documentIntermix.toHtml());
        sb.append("</td></tr>");

        sb.append("<tr><td colspan=\"2\"><p><b>DefaultMailSignature</b></p>");
        sb.append(defaultMailSignature);
        sb.append("</td></tr>");

        sb.append("<tr><td colspan=\"2\"><p><b>Default Mail Attachment:</b></p>");
        if ( defaultMailAttachment == null || defaultMailAttachment.isEmpty() ) {
            sb.append("<b>No Attachment</b>");
        } else {
            Iterator<MandatorMailAttachment> it = defaultMailAttachment.iterator();
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

        sb.append("<tr><td colspan=\"2\"><p><b>Document Identifier Generator Configurations:</b></p>");
        if ( documentIdentifierGeneratorConfigurations == null || documentIdentifierGeneratorConfigurations.isEmpty() ) {
            sb.append("<b>No Document Identifier Generator Configuration</b>");
        } else {
            sb.append("<ul>");
            documentIdentifierGeneratorConfigurations.forEach((type, generatorConfig) -> {
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
