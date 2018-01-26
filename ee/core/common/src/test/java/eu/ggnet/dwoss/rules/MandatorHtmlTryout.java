/*
 * Copyright (C) 2017 GG-Net GmbH
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
package eu.ggnet.dwoss.rules;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.*;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import eu.ggnet.dwoss.mandator.api.value.Mandator;
import eu.ggnet.dwoss.mandator.api.value.partial.DocumentIdentifierGeneratorConfiguration.PrefixType;
import eu.ggnet.dwoss.mandator.api.value.partial.*;

/**
 *
 * @author oliver.guenther
 */
public class MandatorHtmlTryout extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        Company company = Company.builder()
                .name("Example GmbH")
                .street("Test Street 7")
                .zip("99999")
                .city("Testcity")
                .email("test@example.de")
                .emailName("Example GmbH Shop")
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

        String defaultMailSignature = "Mail Signatur \n Test Test \t Senior System Requirements Specilist \n  Mobiel: 0174 123 45 67 \n  Phone; 040 123 45 67 \n Impressums: xxxx";
        UrlLocation mailTemplateLocation = new UrlLocation(new URL("http://example.com/"));

        MandatorMailAttachment attachment1 = MandatorMailAttachment.builder()
                .attachmentName("NewFile.txt")
                .attachmentDescription("txt file")
                .attachmentData(mailTemplateLocation)
                .build();
        MandatorMailAttachment attachment2 = MandatorMailAttachment.builder()
                .attachmentName("NewFile.docx")
                .attachmentDescription("Microsoft Word Document")
                .attachmentData(mailTemplateLocation)
                .build();

        Set<MandatorMailAttachment> defaultMailAttachment = new HashSet<>();
        defaultMailAttachment.add(attachment1);
        defaultMailAttachment.add(attachment2);

        Mandator mandator = Mandator.builder()
                .smtpConfiguration(smtpConfiguration)
                .company(company)
                .dossierPrefix("DW")
                .documentIntermix(documentIntermix)
                .defaultMailSignature(defaultMailSignature)
                .mailTemplateLocation(mailTemplateLocation)
                .defaultMailAttachment(defaultMailAttachment)
                .documentIdentifierGeneratorConfigurations(documentIdentifierGeneratorConfigurations)
                .applyDefaultChannelOnRollIn(false)
                .matchCode("SAMPLE")
                .bugMail("error@localhost")
                .build();

        stage.setTitle("HtmlViewer");
        WebView view = new WebView();
        view.getEngine().loadContent(Css.toHtml5WithStyle(mandator.toHtml()));
        BorderPane p = new BorderPane(view);
        Scene scene = new Scene(p, Color.ALICEBLUE);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
