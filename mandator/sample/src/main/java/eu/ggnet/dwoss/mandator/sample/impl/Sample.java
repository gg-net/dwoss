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
package eu.ggnet.dwoss.mandator.sample.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.*;

import javax.enterprise.inject.Produces;

import eu.ggnet.dwoss.core.common.FileJacket;
import eu.ggnet.dwoss.core.common.values.*;
import eu.ggnet.dwoss.core.system.ImageFinder;
import eu.ggnet.dwoss.mandator.api.DocumentViewType;
import eu.ggnet.dwoss.mandator.api.FreeDocumentTemplateParameter;
import eu.ggnet.dwoss.mandator.api.value.*;
import eu.ggnet.dwoss.mandator.api.value.partial.DocumentIdentifierGeneratorConfiguration.PrefixType;
import eu.ggnet.dwoss.mandator.api.value.partial.*;
import eu.ggnet.dwoss.mandator.api.value.qualifier.CustomerAndOrdersIcon;
import eu.ggnet.dwoss.mandator.api.value.qualifier.DeutscheWarenwirtschaftIcon;

import static eu.ggnet.dwoss.core.common.values.tradename.TradeName.*;

/**
 *
 * @author oliver.guenther
 */
public class Sample {

    static {
        Company company = new Company.Builder()
                .name("Example GmbH")
                .street("Test Street 7")
                .zip("99999")
                .city("Testcity")
                .email("test@example.de")
                .emailName("Example GmbH Shop")
                .logo(new UrlLocation(loadLogo()))
                .build();

        SmtpConfiguration smtpConfiguration = new SmtpConfiguration("example.de", "user", "password", "UTF-8", true, false);

        DocumentIntermix documentIntermix = new DocumentIntermix(null);
        documentIntermix.add(FreeDocumentTemplateParameter.TERMS1, "AA bbbbbb ccc ddddddddddd eeeeeeeeeeeeeeeeeeee fff gggggg hhhh.<br />"
                + " aaa bbbb cccccc ddd eee fffffffffffff ggggggggg hhhhh iiiiiiii.");

        documentIntermix.add(FreeDocumentTemplateParameter.TERMS1, DocumentViewType.RESERVATION, "aa bbbbbb ccc ddddddddddd eeeeeeeeeeeeeeeeeeee fff gggggg hhhh.<br />"
                + "<b>aaa bbbbbbb ccc eeeeeeeeeeee fff dddd ggggg hhhhhhhhhhhhh.<br />aaaaaaaaaa bbbbbbbb ccccc ddddddddddddddd eeeeeee ende</b>");

        documentIntermix.add(FreeDocumentTemplateParameter.TERMS2, DocumentViewType.RESERVATION, "aaaaa cccccccc ddd, dddd eeee ffffffffffff eeee 48 aaaaaaa bbbb cccccccccc ddddddddddd eeeeeeee ffffff,"
                + " aaaaaa bbbb. cccccccccc ddddddddd eeeeee fffff gggggggggggggg eeeeee. "
                + "aaaaaaaa-bbbbbb cccccc ddddddddd eee fffffffffff gggggggggggggggggg eeeeeeeeeee aaaaaa (aaaaaaaaaaaaaaaaa: bbbbb ccc dddddddddddd 6 eeeeee).<br />"
                + "aaa bbbbbbbbbbbbbb ccc dddddddddddddddd eeeeeee 1 ende.");

        documentIntermix.add(FreeDocumentTemplateParameter.TERMS1, DocumentType.ORDER, "aa bbbbbb ddd eeeeeeeeeee ffffffffffffffffffff ggg eeeeee aaaa. "
                + "<br />aaa bbbbbbbbbbbb eeeee cccccccccccccccc ddddddddd eee 24 aaaaaaa bbbb cccc ddd eee ffffffff aaa. ccccccccc ddddddd. "
                + "aaaaaa bbbb cccccc ddd eeeeeeee fff eeeeeeeee (aaaaaaaaaaa aaaaaaaaaaaaaaaa) cccccccccccccc ddd eeeeee yyyy xxx aaaaaaaa bbbbbbbbbbbbbbbb."
                + "vvvvvvvv-bbbbbb cccccc nnnnnnnnn mmm aaaaaaaaaaa ssssssssssssssssss ddddddddddd ffffff (ggggggggggggggggg: hhhhh jjj kkkkkkkkkkkk 6 llllll).<br />"
                + "qqq wwwwwwwwwwwwww eee rrrrrrrrrrrrrrrr ttttttt 1  ende");

        documentIntermix.add(FreeDocumentTemplateParameter.TERMS1, DocumentType.INVOICE, "uu iiiiii ooo ppppppppppp aaaaaaaaaaaaaaaaaaaa sss dddddd ffff.<br />"
                + "ggg hhhhhhhhhhhh jjjjj kkkkkkkkkkkkkkkk lllllllll xxx 24 ccccccc vvvv bbbb nnn mmm aaaaaaaa sss. "
                + "aaaaaaaaa sssssss. dddddd ffff gggggg hhh jjjjjjjj kkk kkkkkkkkk (lllllllllll yyyyyyyyyyyyyyyy) xxxxxxxxxxxxxx ccc vvvvvv bbbb nnn mmmmmmmm qqqqqqqqqqqqqqqq. "
                + "(wwwwwwwwwwwwwwwww: eeeee rrr tttttttttttt 6 zzzzzz).<br />"
                + "uuu iiiiiiiiiiiiii ooo pppppppppppppppp aaaaaaa 1 ende");

        documentIntermix.setFooter("aaaaaaaaaaaaaaa: ssssssssss | dddddddddddddddddddd | HRB: 0000\n"
                + "Tel: 1-800-555-0199 | eMail: test@example.de\n"
                + "www.example.de | www.example.de\n"
                + "Bankverbindung: Awesome Bank | Bankleitzal:133 713 37 | Kontonummer: 133713371\n"
                + "IBAN: XXXXXXXXXXXXXXXXXXXXXX | BIC: XXXXXXXX");

        Map<DocumentType, DocumentIdentifierGeneratorConfiguration> documentIdentifierGeneratorConfigurations = new HashMap<>();
        documentIdentifierGeneratorConfigurations.put(DocumentType.INVOICE,
                DocumentIdentifierGeneratorConfiguration.create("RS{PREFIX}_{COUNTER}", PrefixType.YY, new DecimalFormat("00000")));
        documentIdentifierGeneratorConfigurations.put(DocumentType.ANNULATION_INVOICE,
                DocumentIdentifierGeneratorConfiguration.create("SR{PREFIX}_{COUNTER}", PrefixType.YY, new DecimalFormat("00000")));
        documentIdentifierGeneratorConfigurations.put(DocumentType.CREDIT_MEMO,
                DocumentIdentifierGeneratorConfiguration.create("GS{PREFIX}_{COUNTER}", PrefixType.YY, new DecimalFormat("00000")));

        MANDATOR = new Mandator.Builder()
                .smtpConfiguration(smtpConfiguration)
                .mailTemplateLocation(new UrlLocation(loadMailDocument()))
                .company(company)
                .dossierPrefix("DW")
                .documentIntermix(documentIntermix)
                .putAllDocumentIdentifierGeneratorConfigurations(documentIdentifierGeneratorConfigurations)
                .applyDefaultChannelOnRollIn(false)
                .matchCode("SAMPLE")
                .defaultMailSignature("Sample Signatur")
                .bugMail("error@localhost")
                .build();
        DEFAULT_CUSTOMER_SALES_DATA = new DefaultCustomerSalesdata.Builder()
                .addAllAllowedSalesChannels(EnumSet.of(SalesChannel.CUSTOMER))
                .paymentCondition(PaymentCondition.CUSTOMER)
                .paymentMethod(PaymentMethod.ADVANCE_PAYMENT)
                .shippingCondition(ShippingCondition.SIX_MIN_TEN)
                .build();
    }

    @Produces
    public final static Mandator MANDATOR;

    @Produces
    public final static DefaultCustomerSalesdata DEFAULT_CUSTOMER_SALES_DATA;

    @Produces
    public final static Contractors CONTRACTORS = new Contractors(EnumSet.of(ONESELF, ACER, HP, DELL), EnumSet.of(ACER, PACKARD_BELL, HP, DELL));

    @Produces
    public final static ImageFinder IMAGE_FINDER = new ImageFinder(System.getProperty("java.io.tmpdir"));

    static URL loadMailDocument() {
        return Sample.class.getResource("mailDocument.txt");
    }

    static URL loadLogo() {
        return Sample.class.getResource("logo_example.jpg");
    }

    @Produces
    @DeutscheWarenwirtschaftIcon
    public static FileJacket loadDwIcon() {
        try (InputStream is = Sample.class.getResourceAsStream("money.png")) {
            return new FileJacket("money", "png", is.readAllBytes());
        } catch (IOException | NullPointerException ex) {
            throw new RuntimeException("Can't load money.png", ex);
        }
    }

    @Produces
    @CustomerAndOrdersIcon
    public static FileJacket loadCaoIcon() {
        try (InputStream is = Sample.class.getResourceAsStream("cart.png")) {
            return new FileJacket("cart", "png", is.readAllBytes());
        } catch (IOException | NullPointerException ex) {
            throw new RuntimeException("Can't load cart.png", ex);
        }
    }
}
