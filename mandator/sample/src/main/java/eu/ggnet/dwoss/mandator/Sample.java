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
package eu.ggnet.dwoss.mandator;

import eu.ggnet.dwoss.common.api.values.PaymentMethod;
import eu.ggnet.dwoss.common.api.values.ShippingCondition;
import eu.ggnet.dwoss.common.api.values.PaymentCondition;
import eu.ggnet.dwoss.common.api.values.SalesChannel;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.*;

import javax.enterprise.inject.Produces;

import org.apache.commons.io.FileUtils;

import eu.ggnet.dwoss.mandator.api.value.*;
import eu.ggnet.dwoss.mandator.api.value.partial.DocumentIdentifierGeneratorConfiguration.PrefixType;
import eu.ggnet.dwoss.mandator.api.value.partial.*;
import eu.ggnet.dwoss.rules.*;
import eu.ggnet.dwoss.util.ImageFinder;

import static eu.ggnet.dwoss.rules.TradeName.*;

/**
 *
 * @author oliver.guenther
 */
public class Sample {

    static {
        Company company = Company.builder()
                .name("Example GmbH")
                .street("Test Street 7")
                .zip("99999")
                .city("Testcity")
                .email("test@example.de")
                .emailName("Example GmbH Shop")
                .logo(new UrlLocation(loadLogo()))
                .build();

        SmtpConfiguration smtpConfiguration = new SmtpConfiguration("example.de", "user", "password", "UTF-8", true);

        DocumentIntermix documentIntermix = new DocumentIntermix(null);
        documentIntermix.setFooter("Geschäftsführer: Mr. Tester | USt. ID: XXXXXXXXXXX | HRB: 0000\n"
                + "Tel: 1-800-555-0199 | eMail: test@example.de\n"
                + "www.example.de | www.example.de\n"
                + "Bankverbindung: Awesome Bank | Bankleitzal:133 713 37 | Kontonummer: 133713371\n"
                + "IBAN: XXXXXXXXXXXXXXXXXXXXXX | BIC: XXXXXXXX");

        Map<DocumentType, DocumentIdentifierGeneratorConfiguration> documentIdentifierGeneratorConfigurations = new HashMap<>();
        documentIdentifierGeneratorConfigurations.put(DocumentType.INVOICE,
                new DocumentIdentifierGeneratorConfiguration("RS{PREFIX}_{COUNTER}", PrefixType.YY, new DecimalFormat("00000")));
        documentIdentifierGeneratorConfigurations.put(DocumentType.ANNULATION_INVOICE,
                new DocumentIdentifierGeneratorConfiguration("SR{PREFIX}_{COUNTER}", PrefixType.YY, new DecimalFormat("00000")));
        documentIdentifierGeneratorConfigurations.put(DocumentType.CREDIT_MEMO,
                new DocumentIdentifierGeneratorConfiguration("GS{PREFIX}_{COUNTER}", PrefixType.YY, new DecimalFormat("00000")));

        MANDATOR = Mandator.builder()
                .smtpConfiguration(smtpConfiguration)
                .mailTemplateLocation(new UrlLocation(loadMailDocument()))
                .company(company)
                .dossierPrefix("DW")
                .documentIntermix(documentIntermix)
                .documentIdentifierGeneratorConfigurations(documentIdentifierGeneratorConfigurations)
                .applyDefaultChannelOnRollIn(false)
                .matchCode("SAMPLE")
                .bugMail("error@localhost")
                .build();

        DEFAULT_CUSTOMER_SALES_DATA = DefaultCustomerSalesdata.builder()
                .allowedSalesChannels(new TreeSet<>(EnumSet.of(SalesChannel.CUSTOMER)))
                .paymentCondition(PaymentCondition.CUSTOMER)
                .paymentMethod(PaymentMethod.ADVANCE_PAYMENT)
                .shippingCondition(ShippingCondition.DEFAULT)
                .build();

    }

    @Produces
    public final static Mandator MANDATOR;

    @Produces
    public final static DefaultCustomerSalesdata DEFAULT_CUSTOMER_SALES_DATA;

    @Produces
    public final static Contractors CONTRACTORS = new Contractors(EnumSet.of(ONESELF, ACER, HP, DELL), EnumSet.of(ACER, PACKARD_BELL, HP, DELL));

    @Produces
    public final static ImageFinder IMAGE_FINDER = new ImageFinder(FileUtils.getTempDirectoryPath());

    static URL loadMailDocument() {
        return Sample.class.getResource("mailDocument.txt");
    }

    static URL loadLogo() {
        return Sample.class.getResource("logo_example.jpg");
    }
}
