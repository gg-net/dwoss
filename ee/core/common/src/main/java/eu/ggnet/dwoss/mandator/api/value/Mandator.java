package eu.ggnet.dwoss.mandator.api.value;

import java.io.Serializable;
import java.net.URL;
import java.util.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;
import org.metawidget.inspector.annotation.UiLarge;

import eu.ggnet.dwoss.mandator.api.value.partial.*;
import eu.ggnet.dwoss.rules.DocumentType;
import eu.ggnet.dwoss.rules.TradeName;

import lombok.Value;
import lombok.experimental.Builder;

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
    private final URL mailDocumentTemplate;

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

}
