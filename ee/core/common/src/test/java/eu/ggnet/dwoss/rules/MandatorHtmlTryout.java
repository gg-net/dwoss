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

import java.awt.EventQueue;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import javax.swing.JFrame;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebView;

import org.junit.Test;

import eu.ggnet.dwoss.mandator.api.value.Mandator;
import eu.ggnet.dwoss.mandator.api.value.partial.DocumentIdentifierGeneratorConfiguration.PrefixType;
import eu.ggnet.dwoss.mandator.api.value.partial.*;

/**
 *
 * @author oliver.guenther
 */
public class MandatorHtmlTryout {

    @Test
    public void tryout() throws InterruptedException, InvocationTargetException {

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

        Mandator mandator = Mandator.builder()
                .smtpConfiguration(smtpConfiguration)
                .company(company)
                .dossierPrefix("DW")
                .documentIntermix(documentIntermix)
                .documentIdentifierGeneratorConfigurations(documentIdentifierGeneratorConfigurations)
                .receiptMode(TradeName.ACER)
                .applyDefaultChannelOnRollIn(false)
                .matchCode("SAMPLE")
                .bugMail("error@localhost")
                .build();

        CountDownLatch latch = new CountDownLatch(1);

        EventQueue.invokeAndWait(() -> {
            final JFXPanel jfxPanel = new JFXPanel();
            Platform.runLater(() -> {
                WebView view = new WebView();
                view.getEngine().loadContent(Css.toHtml5WithStyle(mandator.toHtml()));
                BorderPane p = new BorderPane(view);
                Scene sc = new Scene(p);
                jfxPanel.setScene(sc);
            });
            JFrame f = new JFrame("Tryout");
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.getContentPane().add(jfxPanel);
            f.setSize(300, 300);
            f.setVisible(true);
            f.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    latch.countDown();
                }

            });

        });

        latch.await();

    }

}
