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
package eu.ggnet.dwoss.redtapext.ee;

import java.io.*;
import java.net.URL;
import java.util.*;

import javax.ejb.Stateless;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.mail.util.ByteArrayDataSource;
import javax.persistence.EntityManager;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.core.common.FileJacket;
import eu.ggnet.dwoss.core.common.UserInfoException;
import eu.ggnet.dwoss.core.common.values.DocumentType;
import eu.ggnet.dwoss.core.system.util.Utils;
import eu.ggnet.dwoss.customer.api.UiCustomer;
import eu.ggnet.dwoss.customer.ee.CustomerServiceBean;
import eu.ggnet.dwoss.mandator.api.DocumentViewType;
import eu.ggnet.dwoss.mandator.api.FreeDocumentTemplateParameter;
import eu.ggnet.dwoss.mandator.api.service.DocumentService;
import eu.ggnet.dwoss.mandator.api.value.Mandator;
import eu.ggnet.dwoss.mandator.api.value.partial.MailDocumentParameter;
import eu.ggnet.dwoss.mandator.api.value.partial.MandatorMailAttachment;
import eu.ggnet.dwoss.redtape.ee.assist.RedTapes;
import eu.ggnet.dwoss.redtape.ee.eao.DocumentEao;
import eu.ggnet.dwoss.redtape.ee.eao.DossierEao;
import eu.ggnet.dwoss.redtape.ee.entity.Document.Flag;
import eu.ggnet.dwoss.redtape.ee.entity.*;
import eu.ggnet.dwoss.redtape.ee.format.DocumentFormater;
import eu.ggnet.dwoss.uniqueunit.ee.assist.UniqueUnits;
import eu.ggnet.dwoss.uniqueunit.ee.eao.UniqueUnitEao;
import eu.ggnet.dwoss.uniqueunit.ee.entity.PriceType;
import eu.ggnet.dwoss.uniqueunit.ee.entity.UniqueUnit;
import eu.ggnet.lucidcalc.*;
import eu.ggnet.lucidcalc.jexcel.JExcelLucidCalcWriter;

import static eu.ggnet.dwoss.redtapext.ee.DocumentSupporterOperation.TemplateParameter.*;
import static eu.ggnet.lucidcalc.CFormat.FontStyle.BOLD_ITALIC;
import static eu.ggnet.lucidcalc.CFormat.FontStyle.ITALIC;
import static eu.ggnet.lucidcalc.CFormat.HorizontalAlignment.CENTER;
import static eu.ggnet.lucidcalc.CFormat.HorizontalAlignment.RIGHT;
import static eu.ggnet.lucidcalc.CFormat.Representation.*;
import static eu.ggnet.lucidcalc.SUtil.SR;
import static java.awt.Color.*;

/**
 * Supporting Operations for Documents, mostly printing or mailing.
 * <p/>
 * @author oliver.guenther
 */
@Stateless
public class DocumentSupporterOperation implements DocumentSupporter {

    @Inject
    private Instance<DocumentService> documentService;

    /**
     * Contains all Parameters which a Template may support, see also {@link FreeDocumentTemplateParameter}.
     */
    public static class TemplateParameter {

        public final static String REF_ID = "REF_ID";

        public final static String CUSTOMER_ID = "CUSTOMER_ID";

        public static final String IDENTIFIER_TYPE = "IDENTIFIER_TYPE";

        public static final String IDENTIFIER_ID = "IDENTIFIER_ID";

        public static final String INVOICE_ADDRESS = "INVOICE_ADDRESS";

        public static final String SHIPPING_ADDRESS = "SHIPPING_ADDRESS";

        public static final String PAYMENT_TEXT = "PAYMENT_TEXT";

        public static final String SUM_NETTO = "SUM_NETTO";

        public static final String SUM_BRUTTO = "SUM_BRUTTO";

        public static final String SUM_TAX = "SUM_TAX";

        public static final String TAX_INFO = "TAX_INFO";

        public static final String COMPANY_LOGO = "COMPANY_LOGO";

        public static final String COMPANY = "COMPANY";

        public static final String FOOTER = "FOOTER";

        /**
         * Terms besides the sum.
         */
        public static final String TERMS1 = FreeDocumentTemplateParameter.TERMS1.name();

        /**
         * Terms below the sum.
         */
        public static final String TERMS2 = FreeDocumentTemplateParameter.TERMS2.name();

        public static final String ACTUAL = "ACTUAL";

        /**
         * Date of the actual activity. Either the invoice date or a reference date.
         */
        public static final String PERFOMANCE_ON = "PERFOMANCE_ON";

    }

    private static final Logger L = LoggerFactory.getLogger(DocumentSupporterOperation.class);

    @Inject
    @RedTapes
    private EntityManager redTapeEm;

    @Inject
    @UniqueUnits
    private EntityManager uuEm;

    @Inject
    private Mandator mandator;

    @Inject
    private CustomerServiceBean customerService;

    /**
     * Creates a JasperPrint for the Document.
     *
     * @param document the document
     * @return a JasperPrint
     */
    @Override
    public JasperPrint render(Document document, DocumentViewType viewType) throws UserInfoException {
        Objects.requireNonNull(document, "document must not be null");
        return jasper(document, viewType);
    }

    public void setMandator(Mandator mandator) {
        this.mandator = mandator;
    }

    /**
     * This method send document to the e-Mail address that is in the customer set.
     * <p/>
     * @param document This is the Document that will be send.
     * @throws UserInfoException if the sending of the Mail is not successful.
     * @throws RuntimeException  if problems exist in the JasperExporter
     */
    @Override
    public void mail(Document document, DocumentViewType jtype) throws UserInfoException, RuntimeException {
        UiCustomer customer = customerService.asUiCustomer(document.getDossier().getCustomerId());

        String customerMailAddress = customerService.defaultEmailCommunication(document.getDossier().getCustomerId());
        if ( customerMailAddress == null ) {
            throw new UserInfoException("Kunde hat keine E-Mail Hinterlegt! Senden einer E-Mail ist nicht Möglich!");
        }

        String doctype = (jtype == DocumentViewType.DEFAULT ? document.getType().description : jtype.description);

        try ( InputStream is = mandator.mailTemplateLocation().toURL().openStream();  InputStreamReader templateReader = new InputStreamReader(is)) {
            String text = new MailDocumentParameter(customer.toTitleNameLine(), doctype).eval(IOUtils.toString(templateReader));
            MultiPartEmail email = mandator.prepareDirectMail();

            email.setCharset("UTF-8");

            email.addTo(customerMailAddress);
            email.setSubject(doctype + " | " + document.getDossier().getIdentifier());
            email.setMsg(text + mandator.defaultMailSignature());

            // Building the Identifier, See also eu.ggnet.dwoss.redtapext.ui.cao.jasper.DocumentJasperFxView.saveToFile
            String identifier = document.getIdentifier() != null ? document.getIdentifier() : document.getDossier().getIdentifier();
            identifier = identifier.replace("/", "_");
            if ( document.getInvoiceAddress() != null ) {
                identifier += " - " + document.getInvoiceAddress().getDescription().split("\\n")[0];
            }
            if ( document.getActual() != null ) {
                identifier += " - " + Utils.ISO_DATE.format(document.getActual());
            }

            email.attach(
                    new ByteArrayDataSource(JasperExportManager.exportReportToPdf(jasper(document, jtype)), "application/pdf"),
                    identifier + ".pdf", document.getType().description() + " zu Ihrem Vorgang."
            );

            //get needed mail attachments
            Map<DocumentType, Set<MandatorMailAttachment>> mailAttachmentByDocumentType = mandator.mailAttachmentByDocumentType();
            Set<MandatorMailAttachment> attachmentByType = mailAttachmentByDocumentType.getOrDefault(document.getType(), new HashSet<>());

            //fallback to default if none is set
            if ( attachmentByType.isEmpty() ) attachmentByType = mandator.defaultMailAttachment();

            //add attachments
            for (MandatorMailAttachment mma : attachmentByType) {
                email.attach(mma.attachmentData().toURL(), mma.attachmentName(), mma.attachmentDescription());
            }

            email.send();
        } catch (EmailException ex) {
            L.error("Error on Mail sending", ex);
            throw new UserInfoException("Das senden der Mail war nicht erfolgreich!\n" + ex.getMessage());
        } catch (IOException | JRException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sets the Flags {@link Flag#CUSTOMER_BRIEFED} and {@link Flag#CUSTOMER_EXACTLY_BRIEFED} at the document.
     * Also appends this change at the DocumentHistory.
     *
     * @param detached the document
     * @return the updated document
     */
    @Override
    public Dossier briefed(Document detached, String arranger) {
        L.info("Setting briefed at {}", DocumentFormater.toSimpleLine(detached));
        Document document = new DocumentEao(redTapeEm).findById(detached.getId());
        document.getDossier().fetchEager();
        document.add(Document.Flag.CUSTOMER_BRIEFED);
        document.add(Document.Flag.CUSTOMER_EXACTLY_BRIEFED);
        document.getHistory().setComment(document.getHistory().getComment() + ", Kunde wurde durch " + arranger + " informiert.");
        return document.getDossier();
    }

    @Override
    public FileJacket toXls(String identifier) {
        DossierEao dossierEao = new DossierEao(redTapeEm);
        UniqueUnitEao uniqueUnitEao = new UniqueUnitEao(uuEm);
        List<Dossier> dossiers = dossierEao.findByIdentifier(identifier);
        if ( dossiers.isEmpty() ) return null;
        Map<String, List<Object[]>> datas = new HashMap<>();
        for (Dossier dossier : dossiers) {
            for (Document document : dossier.getActiveDocuments()) {
                List<Object[]> rows = new ArrayList<>();
                datas.put(
                        dossier.getIdentifier() + "_" + document.getType().description() + "_" + (document.getIdentifier() == null ? "" : document.getIdentifier()),
                        rows);
                for (Position pos : document.getPositions().values()) {
                    if ( pos.getUniqueUnitId() > 0 ) {
                        UniqueUnit uu = uniqueUnitEao.findById(pos.getUniqueUnitId());
                        rows.add(new Object[]{
                            pos.getType().description(),
                            pos.getAmount(),
                            pos.getName(),
                            pos.getRefurbishedId(),
                            pos.getSerial(),
                            pos.getPrice(),
                            pos.toAfterTaxPrice(),
                            Utils.ISO_DATE.format(uu.getMfgDate()),
                            uu.getProduct().getPrice(PriceType.MANUFACTURER_COST)
                        });
                    } else {
                        rows.add(new Object[]{
                            pos.getType().description(),
                            pos.getAmount(),
                            pos.getName(),
                            null,
                            null,
                            pos.getPrice(),
                            pos.toAfterTaxPrice(),
                            null,
                            null
                        });
                    }
                }
            }
        }

        STable template = new STable();
        CFormat euro = new CFormat(RIGHT, CURRENCY_EURO);
        CFormat date = new CFormat(CENTER, SHORT_DATE);
        CFormat percent = new CFormat(ITALIC, BLUE, null, null, null, PERCENT_FLOAT);

        template.setHeadlineFormat(new CFormat(BOLD_ITALIC, BLACK, WHITE, CENTER, new CBorder(BLACK)));
        template.add(new STableColumn("Type", 7))
                .add(new STableColumn("Menge", 10))
                .add(new STableColumn("Name", 30))
                .add(new STableColumn("RefurbishedId", 10))
                .add(new STableColumn("Serial", 15))
                .add(new STableColumn("Preis", 15, euro))
                .add(new STableColumn("Preis inc. Mwst", 15, euro))
                .add(new STableColumn("MfgDate", 13, date))
                .add(new STableColumn("CostPrice", 12, euro))
                .add(new STableColumn("%Cost", 12, percent).setAction(new SFormulaAction(SR(5), "/", SR(8))));

        CCalcDocument document = new TempCalcDocument();
        for (Map.Entry<String, List<Object[]>> entry : datas.entrySet()) {
            CSheet sheet = new CSheet(entry.getKey());
            STable table = new STable(template);
            table.setModel(new STableModelList(entry.getValue()));
            sheet.addBelow(table);
            document.add(sheet);
        }
        FileJacket fj = new FileJacket("Dossiers", ".xls", new JExcelLucidCalcWriter().write(document));
        return fj;
    }

    /**
     * Creates a JasperPrint for the Document.
     *
     * @param document the document
     * @return a JasperPrint
     */
    private JasperPrint jasper(Document document, DocumentViewType viewType) throws UserInfoException {
        if ( !document.hasSingleTax() )
            throw new UserInfoException("Dokument " + document.toSimpleLine() + " hat verschiedenen Werte für Steuer, die Vorlage unterstüzt das nicht");
        URL url = mandator.documentIntermix().getTemplate(viewType) != null
                ? mandator.documentIntermix().getTemplate(viewType)
                : DocumentSupporterOperation.class.getResource(viewType.fileName);
        try ( InputStream inputStream = url.openStream()) {

            JasperReport jasperReport = JasperCompileManager.compileReport(inputStream);
            JasperPrint result = JasperFillManager.fillReport(jasperReport, toTemplateParameters(document, viewType), toNormalizedDataSource(document));
            return result;
        } catch (JRException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private JRDataSource toNormalizedDataSource(Document document) {
        ArrayList<Position> positions = new ArrayList<>();
        for (Position position : document.getPositions().values()) {
            Position p = position;
            String description = position.getDescription().replace("•", " "); // Possible unprintable Character.
            p.setDescription(description);
            positions.add(p);
        }
        return new JRBeanCollectionDataSource(positions);
    }

    private Map<String, Object> toTemplateParameters(Document document, DocumentViewType viewType) {
        // Setting Defaults.
        Map<String, Object> reportParameter = new HashMap<>();
        Dossier dossier = document.getDossier();
        reportParameter.put(REF_ID, (dossier.getIdentifier() == null ? "-" : dossier.getIdentifier()) + ".K" + dossier.getCustomerId());
        reportParameter.put(CUSTOMER_ID, dossier.getCustomerId());
        reportParameter.put(IDENTIFIER_TYPE, document.getType().description());
        reportParameter.put(IDENTIFIER_ID, document.getIdentifier() == null ? (dossier.getIdentifier() == null ? "-" : dossier.getIdentifier()) : document.getIdentifier());
        // The two \n are a workaround for Windows/Remote Client. Otherwise the last line of an address is not shown.
        reportParameter.put(INVOICE_ADDRESS, document.getInvoiceAddress().getDescription() + "\n");
        reportParameter.put(SHIPPING_ADDRESS, document.getShippingAddress().getDescription() + "\n");
        reportParameter.put(SUM_NETTO, document.getPrice());
        reportParameter.put(SUM_BRUTTO, document.toAfterTaxPrice());
        double sumTax = document.toAfterTaxPrice() - document.getPrice();
        // Both tax fields are null, if their value is 0.00
        reportParameter.put(SUM_TAX, Math.abs(sumTax) < 0.001 ? null : sumTax);
        reportParameter.put(TAX_INFO, document.getSingleTax() < 0.001 ? null : String.format("%.0f %% MwSt.", document.getSingleTax() * 100));
        reportParameter.put(ACTUAL, document.getActual());
        reportParameter.put(COMPANY, mandator.company().toSingleLine());
        reportParameter.put(COMPANY_LOGO, mandator.company().logo().toURL());
        reportParameter.put(FOOTER, mandator.documentIntermix().getFooter() + "\n");
        reportParameter.put(PAYMENT_TEXT, "");

        reportParameter.put(TERMS1, mandator.documentIntermix().getFreeTexts(FreeDocumentTemplateParameter.TERMS1, viewType, document.getType()));

        // Building Terms 2
        StringBuilder terms2 = new StringBuilder();
        if ( StringUtils.isNotBlank(document.getTaxType().documentText()) )
            terms2.append("<p>").append(document.getTaxType().documentText()).append("</p>");
        terms2.append("<p>").append(mandator.documentIntermix().getFreeTexts(FreeDocumentTemplateParameter.TERMS2, viewType, document.getType())).append("</p>");

        terms2.append("<p><b>");
        if ( documentService == null || documentService.isAmbiguous() || documentService.isUnsatisfied() ) {
            //default
            if ( document.getType() == DocumentType.ORDER ) terms2.append(dossier.getPaymentMethod().getOrderText());
            if ( document.getType() == DocumentType.INVOICE ) terms2.append(dossier.getPaymentMethod().invoiceText(0));
        } else {
            terms2.append(documentService.get().paymentInstructionText(document.getType(), dossier.getPaymentMethod()));
        }
        terms2.append("</b></p>");

        reportParameter.put(TERMS2, terms2.toString());

        reportParameter.put(PERFOMANCE_ON, document.getActual());
        // Depending dates in PERFORMANCE_ON, like a CreditMemo depends on the date of the invoice.
        List<Document> invoices = dossier.getActiveDocuments(DocumentType.INVOICE);
        if ( !invoices.isEmpty() ) {
            reportParameter.put(PERFOMANCE_ON, invoices.get(0).getActual());
        }

        if ( viewType != null && viewType != DocumentViewType.DEFAULT ) reportParameter.put(IDENTIFIER_TYPE, viewType.documentTitle);
        // TODO: Should be somethere else, but keep it here for now.
        else if ( document.getType() == DocumentType.ORDER ) reportParameter.put(IDENTIFIER_TYPE, "Auftragsbestätigung/Proformarechnung");

        reportParameter.put(JRParameter.REPORT_LOCALE, Locale.GERMANY);

        return reportParameter;
    }
}
