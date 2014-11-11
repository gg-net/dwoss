/* 
 * Copyright (C) 2014 pascal.perau
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
package eu.ggnet.dwoss.redtape;

import eu.ggnet.dwoss.rules.DocumentType;
import eu.ggnet.dwoss.util.DateFormats;
import eu.ggnet.dwoss.util.FileJacket;
import eu.ggnet.dwoss.util.UserInfoException;
import eu.ggnet.dwoss.redtape.entity.Dossier;
import eu.ggnet.dwoss.redtape.entity.Position;
import eu.ggnet.dwoss.redtape.entity.Document;
import eu.ggnet.lucidcalc.CBorder;
import eu.ggnet.lucidcalc.CFormat;
import eu.ggnet.lucidcalc.CSheet;
import eu.ggnet.lucidcalc.CCalcDocument;
import eu.ggnet.lucidcalc.TempCalcDocument;
import eu.ggnet.lucidcalc.SFormulaAction;
import eu.ggnet.lucidcalc.STableModelList;
import eu.ggnet.lucidcalc.STableColumn;
import eu.ggnet.lucidcalc.STable;

import java.io.*;
import java.net.URL;
import java.util.*;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.mail.util.ByteArrayDataSource;
import javax.persistence.EntityManager;

import org.apache.commons.io.IOUtils;
import org.apache.commons.mail.*;
import org.slf4j.*;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import eu.ggnet.dwoss.configuration.GlobalConfig;
import eu.ggnet.dwoss.customer.api.CustomerService;
import eu.ggnet.dwoss.customer.api.UiCustomer;
import eu.ggnet.lucidcalc.jexcel.JExcelLucidCalcWriter;

import eu.ggnet.dwoss.mandator.api.DocumentViewType;
import eu.ggnet.dwoss.mandator.api.FreeDocumentTemplateParameter;
import eu.ggnet.dwoss.mandator.api.value.Mandator;
import eu.ggnet.dwoss.mandator.api.value.partial.MailDocumentParameter;
import eu.ggnet.dwoss.mandator.api.value.partial.MandatorMailAttachment;

import eu.ggnet.dwoss.redtape.assist.RedTapes;
import eu.ggnet.dwoss.redtape.eao.DocumentEao;
import eu.ggnet.dwoss.redtape.eao.DossierEao;
import eu.ggnet.dwoss.redtape.entity.Document.Flag;
import eu.ggnet.dwoss.redtape.format.DocumentFormater;


import eu.ggnet.dwoss.uniqueunit.assist.UniqueUnits;
import eu.ggnet.dwoss.uniqueunit.eao.UniqueUnitEao;
import eu.ggnet.dwoss.uniqueunit.entity.PriceType;
import eu.ggnet.dwoss.uniqueunit.entity.UniqueUnit;

import static eu.ggnet.lucidcalc.CFormat.FontStyle.BOLD_ITALIC;
import static eu.ggnet.lucidcalc.CFormat.FontStyle.ITALIC;
import static eu.ggnet.lucidcalc.CFormat.HorizontalAlignment.*;
import static eu.ggnet.lucidcalc.CFormat.Representation.*;
import static eu.ggnet.lucidcalc.SUtil.SR;
import static eu.ggnet.dwoss.redtape.DocumentSupporterOperation.TemplateParameter.*;
import static java.awt.Color.*;

/**
 * Supporting Operations for Documents, mostly printing or mailing.
 * <p/>
 * @author oliver.guenther
 */
@Stateless
public class DocumentSupporterOperation implements DocumentSupporter {

    /**
     * Contains all Paramters which a Template may support, see also {@link FreeDocumentTemplateParameter}.
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

        public static final String TAX = "TAX";

        public static final String COMPANY_LOGO = "COMPANY_LOGO";

        public static final String COMPANY = "COMPANY";

        public static final String FOOTER = "FOOTER";

        public static final String TERMS1 = "TERMS1";

        public static final String TERMS2 = "TERMS2";

        public static final String ACTUAL = "ACTUAL";

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
    private CustomerService customerService;

    /**
     * Creates a JasperPrint for the Document.
     *
     * @param document the document
     * @return a JasperPrint
     */
    @Override
    public JasperPrint render(Document document, DocumentViewType viewType) {
        return jasper(document, viewType);
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

        String customerMailAddress = customerService.asCustomerMetaData(document.getDossier().getCustomerId()).getEmail();
        if ( customerMailAddress == null ) {
            throw new UserInfoException("Kunde hat keine E-Mail Hinterlegt! Senden einer E-Mail ist nicht Möglich!");
        }

        String doctype = (jtype == DocumentViewType.DEFAULT ? document.getType().getName() : jtype.getName());

        try (InputStream is = mandator.getMailDocumentTemplate().openStream();
                InputStreamReader templateReader = new InputStreamReader(is)) {
            String text = new MailDocumentParameter(customer.toTitleNameLine(), doctype).eval(IOUtils.toString(templateReader));
            MultiPartEmail email = mandator.prepareDirectMail();

            email.addTo(customerMailAddress);
            email.setSubject(document.getType().getName() + " | " + document.getDossier().getIdentifier());
            email.setMsg(text + mandator.getDefaultMailSignature());
            email.attach(
                    new ByteArrayDataSource(JasperExportManager.exportReportToPdf(jasper(document, jtype)), "application/pdf"),
                    "Dokument.pdf", "Das ist das Dokument zu Ihrem Aufrag als PDF.");
            for (MandatorMailAttachment mma : mandator.getDefaultMailAttachment()) {
                email.attach(mma.getAttachmentData(), mma.getAttachmentName(), mma.getAttachmentDescription());
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
                        dossier.getIdentifier() + "_" + document.getType().getName() + "_" + (document.getIdentifier() == null ? "" : document.getIdentifier()),
                        rows);
                for (Position pos : document.getPositions().values()) {
                    if ( pos.getUniqueUnitId() > 0 ) {
                        UniqueUnit uu = uniqueUnitEao.findById(pos.getUniqueUnitId());
                        rows.add(new Object[]{
                            pos.getType().getName(),
                            pos.getAmount(),
                            pos.getName(),
                            pos.getPrice(),
                            pos.getAfterTaxPrice(),
                            DateFormats.ISO.format(uu.getMfgDate()),
                            uu.getProduct().getPrice(PriceType.MANUFACTURER_COST)
                        });
                    } else {
                        rows.add(new Object[]{
                            pos.getType().getName(),
                            pos.getAmount(),
                            pos.getName(),
                            pos.getPrice(),
                            pos.getAfterTaxPrice(),
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
                .add(new STableColumn("Preis", 15, euro))
                .add(new STableColumn("Preis inc. Mwst", 15, euro))
                .add(new STableColumn("MfgDate", 13, date))
                .add(new STableColumn("CostPrice", 12, euro))
                .add(new STableColumn("%Cost", 12, percent).setAction(new SFormulaAction(SR(3), "/", SR(6))));

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
    private JasperPrint jasper(Document document, DocumentViewType viewType) {
        URL url = mandator.getDocumentIntermix().getTemplate(viewType) != null
                ? mandator.getDocumentIntermix().getTemplate(viewType)
                : DocumentSupporterOperation.class.getResource(viewType.getFileName());
        try (InputStream inputStream = url.openStream()) {
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

        double nettoPrice = 0;
        double bruttoPrice = 0;

        for (Position position : document.getPositions().values()) {
            nettoPrice += position.getPrice() * position.getAmount();
            bruttoPrice += position.getAfterTaxPrice() * position.getAmount();
        }

        // Setting Defaults.
        Map<String, Object> reportParameter = new HashMap<>();
        Dossier dossier = document.getDossier();
        reportParameter.put(REF_ID, "K" + dossier.getCustomerId() + (dossier.getIdentifier() == null ? "-" : dossier.getIdentifier()));
        reportParameter.put(CUSTOMER_ID, dossier.getCustomerId());
        reportParameter.put(IDENTIFIER_TYPE, document.getType().getName());
        reportParameter.put(IDENTIFIER_ID, document.getIdentifier() == null ? (dossier.getIdentifier() == null ? "-" : dossier.getIdentifier()) : document.getIdentifier());
        // The two \n are a workaround for Windows/Remote Client. Otherwise the last line of an address is not shown.
        reportParameter.put(INVOICE_ADDRESS, document.getInvoiceAddress().getDescription() + "\n");
        reportParameter.put(SHIPPING_ADDRESS, document.getShippingAddress().getDescription() + "\n");
        reportParameter.put(SUM_NETTO, nettoPrice);
        reportParameter.put(SUM_BRUTTO, bruttoPrice);
        reportParameter.put(SUM_TAX, bruttoPrice - nettoPrice);
        reportParameter.put(TAX, GlobalConfig.TAX * 100);
        reportParameter.put(ACTUAL, document.getActual());
        reportParameter.put(COMPANY, mandator.getCompany().toSingleLine());
        reportParameter.put(COMPANY_LOGO, mandator.getCompany().getLogo());
        reportParameter.put(FOOTER, mandator.getDocumentIntermix().getFooter() + "\n");
        reportParameter.put(PERFOMANCE_ON, document.getActual());
        reportParameter.put(PAYMENT_TEXT, "");

        for (FreeDocumentTemplateParameter parameter : FreeDocumentTemplateParameter.values()) {
            reportParameter.put(parameter.name(), mandator.getDocumentIntermix().getFreeTexts(parameter, viewType, document.getType()));
        }

        if ( document.getType() == DocumentType.ORDER ) reportParameter.put(PAYMENT_TEXT, dossier.getPaymentMethod().getOrderText());
        if ( document.getType() == DocumentType.INVOICE ) reportParameter.put(PAYMENT_TEXT, dossier.getPaymentMethod().getInvoiceText(0));

        // Depending dates in PERFORMANCE_ON, like a CreditMemo depends on the date of the invoice.
        List<Document> invoices = dossier.getActiveDocuments(DocumentType.INVOICE);
        if ( !invoices.isEmpty() ) {
            reportParameter.put(PERFOMANCE_ON, invoices.get(0).getActual());
        }

        if ( viewType != null && viewType != DocumentViewType.DEFAULT ) reportParameter.put(IDENTIFIER_TYPE, viewType.getDocumentTitle());
        // TODO: Should be somethere else, but keep it here for now.
        else if ( document.getType() == DocumentType.ORDER ) reportParameter.put(IDENTIFIER_TYPE, "Auftragsbestätigung/Proformarechnung");

        reportParameter.put(JRParameter.REPORT_LOCALE, Locale.GERMANY);

        return reportParameter;
    }
}
