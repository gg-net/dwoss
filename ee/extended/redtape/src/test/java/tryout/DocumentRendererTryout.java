/*
 * Copyright (C) 2018 GG-Net GmbH
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
package tryout;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.io.File;

import javax.swing.JFrame;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.swing.JRViewer;

import eu.ggnet.dwoss.core.common.UserInfoException;
import eu.ggnet.dwoss.core.common.values.*;
import eu.ggnet.dwoss.mandator.api.DocumentViewType;
import eu.ggnet.dwoss.mandator.sample.impl.Sample;
import eu.ggnet.dwoss.redtape.ee.entity.*;
import eu.ggnet.dwoss.redtapext.ee.DocumentSupporterOperation;
import eu.ggnet.dwoss.redtapext.op.itest.support.NaivBuilderUtil;

/**
 *
 * @author oliver.guenther
 */
public class DocumentRendererTryout {

    private static JasperPrint makeStaticDocument() throws UserInfoException {
        @SuppressWarnings("UseInjectionInsteadOfInstantion")
        DocumentSupporterOperation documentSupporter = new DocumentSupporterOperation();
        documentSupporter.setMandator(new Sample().getMandator());

        Dossier dos = new Dossier();
        dos.setPaymentMethod(PaymentMethod.ADVANCE_PAYMENT);
        dos.setDispatch(true);
        dos.setCustomerId(1);

        Document doc = new Document();
        doc.setTaxType(TaxType.GENERAL_SALES_TAX_DE_19_PERCENT);
        doc.setType(DocumentType.ORDER);
        doc.setActive(true);
        doc.setDirective(Document.Directive.WAIT_FOR_MONEY);
        doc.setHistory(new DocumentHistory("JUnit", "Automatische Erstellung eines leeren Dokuments"));

        Address a = new Address("Herr Muh\nMuhstrasse 7\n12345 Muhstadt");
        doc.setInvoiceAddress(a);
        doc.setShippingAddress(a);
        dos.add(doc);

        NaivBuilderUtil.overwriteTax(doc.getTaxType());

        doc.append(NaivBuilderUtil.comment());
        doc.append(NaivBuilderUtil.longComment());
        doc.append(NaivBuilderUtil.service());
        doc.append(NaivBuilderUtil.shippingcost());

        System.out.println("Tax: " + doc.getSingleTax());
        System.out.println("Netto " + doc.getPrice());
        System.out.println("Brutto: " + doc.toAfterTaxPrice());
        System.out.println("SumTax: " + (doc.toAfterTaxPrice() - doc.getPrice()));
        return documentSupporter.render(doc, DocumentViewType.SHIPPING);
    }

    private static void show() throws Exception {
        JasperPrint result = makeStaticDocument();

        JasperExportManager.exportReportToPdfFile(result, "target/Dokument.pdf");
        Desktop.getDesktop().open(new File("target/Dokument.pdf"));

        JRViewer viewer = new JRViewer(result);
        JFrame frame = new JFrame("Viewer");
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(viewer, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) throws Exception {
        show();
        // makeStaticDocument();
    }

}
