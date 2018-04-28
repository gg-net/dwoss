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
package eu.ggnet.dwoss.redtapext.ee.reporting;

import eu.ggnet.lucidcalc.CBorder;
import eu.ggnet.lucidcalc.CFormat;
import eu.ggnet.lucidcalc.CSheet;
import eu.ggnet.lucidcalc.CCalcDocument;
import eu.ggnet.lucidcalc.TempCalcDocument;
import eu.ggnet.lucidcalc.STableModelList;
import eu.ggnet.lucidcalc.STableColumn;
import eu.ggnet.lucidcalc.STable;
import eu.ggnet.lucidcalc.LucidCalc;

import java.awt.Color;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import eu.ggnet.dwoss.customer.api.UiCustomer;
import eu.ggnet.dwoss.customer.ee.CustomerServiceBean;
import eu.ggnet.dwoss.progress.MonitorFactory;
import eu.ggnet.dwoss.progress.SubMonitor;
import eu.ggnet.dwoss.redtape.ee.assist.RedTapes;
import eu.ggnet.dwoss.redtape.ee.eao.DocumentEao;
import eu.ggnet.dwoss.redtape.ee.entity.Document;
import eu.ggnet.dwoss.redtape.ee.entity.Position;

import eu.ggnet.dwoss.rules.DocumentType;
import eu.ggnet.dwoss.rules.PositionType;

import eu.ggnet.dwoss.uniqueunit.ee.assist.UniqueUnits;
import eu.ggnet.dwoss.uniqueunit.ee.eao.UniqueUnitEao;
import eu.ggnet.dwoss.uniqueunit.ee.entity.PriceType;
import eu.ggnet.dwoss.uniqueunit.ee.entity.UniqueUnit;

import eu.ggnet.dwoss.util.FileJacket;

import static eu.ggnet.lucidcalc.CFormat.HorizontalAlignment.RIGHT;
import static eu.ggnet.lucidcalc.CFormat.Representation.CURRENCY_EURO;
import static eu.ggnet.lucidcalc.CFormat.Representation.SHORT_DATE;
import static java.awt.Color.*;

/**
 *
 * @author pascal.perau
 */
@Stateless
public class CreditMemoReporterOperation implements CreditMemoReporter {

    private static final Comparator<Object[]> comperator = new Comparator<Object[]>() {
        @Override
        public int compare(Object[] o1, Object[] o2) {
            Integer parseOne;
            Integer parseTwo;
            try {
                parseOne = Integer.parseInt((String)o1[0]);
            } catch (NumberFormatException e) {
                parseOne = -1;
            }
            try {
                parseTwo = Integer.parseInt((String)o2[0]);
            } catch (NumberFormatException e) {
                parseTwo = -1;
            }

            return parseOne.compareTo(parseTwo);
        }
    };

    @Inject
    @RedTapes
    private EntityManager redTapeEm;

    @Inject
    @UniqueUnits
    private EntityManager uuEm;

    @Inject
    private MonitorFactory monitorFactory;

    @Inject
    private CustomerServiceBean customerService;

    public final static CFormat EURO = new CFormat(RIGHT, CURRENCY_EURO);

    @Override
    public FileJacket toXls(Date start, Date end) {
        SubMonitor m = monitorFactory.newSubMonitor("Generating Report", 25);
        m.message("Loading Credit Memos");
        List<Position> creditMemoPositions = new ArrayList<>();
        for (Document document
                : new DocumentEao(redTapeEm).findDocumentsBetweenDates(start, end,
                        DocumentType.COMPLAINT, DocumentType.CREDIT_MEMO, DocumentType.ANNULATION_INVOICE)) {
            for (Position position : document.getPositions().values()) {
                creditMemoPositions.add(position);
            }
        }
        m.setWorkRemaining(creditMemoPositions.size() * 2);

        UniqueUnitEao unitEao = new UniqueUnitEao(uuEm);

        m.message(null);
        List<Object[]> rows = new ArrayList<>();
        for (Position position : creditMemoPositions) {
            if ( position.getType() == PositionType.COMMENT ) continue;

            Document doc = position.getDocument();

            UiCustomer customer = customerService.asUiCustomer(position.getDocument().getDossier().getCustomerId());
            UniqueUnit unit = unitEao.findById(position.getUniqueUnitId());

            String error = "ERROR FINDING UNIT";
            String sopoNr = "-";
            String serial = "-";
            String contractor = "-";
            String fullOrPartial = "-";
            double customerPrice = 0;
            double retailerPrice = 0;
            if ( position.getType() == PositionType.UNIT || position.getType() == PositionType.UNIT_ANNEX ) {
                if ( unit == null ) {
                    sopoNr = error;
                    serial = error;
                    contractor = error;
                } else {
                    sopoNr = unit.getIdentifier(UniqueUnit.Identifier.REFURBISHED_ID);
                    serial = unit.getIdentifier(UniqueUnit.Identifier.SERIAL);
                    contractor = unit.getContractor().getName();
                    customerPrice = unit.getPrice(PriceType.CUSTOMER);
                    retailerPrice = unit.getPrice(PriceType.RETAILER);
                    fullOrPartial = (position.getType() == PositionType.UNIT ? "V" : "T");
                    if ( position.getDocument().getType() == DocumentType.COMPLAINT ) fullOrPartial = "Rekla";
                }
            }

            rows.add(new Object[]{
                sopoNr,
                serial,
                position.getName(),
                contractor,
                fullOrPartial,
                position.getPrice(),
                position.toAfterTaxPrice(),
                position.getDocument().getDossier().getIdentifier(),
                doc.getActual(),
                position.getDocument().getIdentifier(),
                customer.getId(),
                customer.toNameCompanyLine(),
                customerPrice,
                retailerPrice
            });
            m.worked(5);
        }
        Collections.sort(rows, comperator);

        m.message("Generating Document");
        m.setWorkRemaining(10);
        STable table = new STable();
        table.setTableFormat(new CFormat(BLACK, WHITE, new CBorder(BLACK)));
        table.setHeadlineFormat(new CFormat(CFormat.FontStyle.BOLD, Color.BLACK, Color.YELLOW, CFormat.HorizontalAlignment.LEFT, CFormat.VerticalAlignment.BOTTOM, CFormat.Representation.DEFAULT));
        table.add(new STableColumn("SopoNr", 15))
                .add(new STableColumn("Seriennummer", 30))
                .add(new STableColumn("Name", 50))
                .add(new STableColumn("Contractor", 15))
                .add(new STableColumn("Voll/Teil", 10))
                .add(new STableColumn("Netto", 10, EURO))
                .add(new STableColumn("Brutto", 10, EURO))
                .add(new STableColumn("DossierId", 15))
                .add(new STableColumn("Belegdatum", 10, new CFormat(SHORT_DATE)))
                .add(new STableColumn("DokumentId", 15))
                .add(new STableColumn("Kid", 10))
                .add(new STableColumn("Kunde", 50))
                .add(new STableColumn("Endkundenpreis(Netto)", 25, EURO))
                .add(new STableColumn("Händlerpreis(Netto)", 25, EURO));
        table.setModel(new STableModelList(rows));

        CCalcDocument cdoc = new TempCalcDocument("Gutschriften_");
        cdoc.add(new CSheet("Sheet1", table));

        File file = LucidCalc.createWriter(LucidCalc.Backend.XLS).write(cdoc);
        m.finish();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd_MM_yyy");
        return new FileJacket("Gutschriften_" + dateFormat.format(start) + "-" + dateFormat.format(end), ".xls", file);
    }

    @Override
    public FileJacket toOptimizedXls(Date start, Date end) {
        SubMonitor m = monitorFactory.newSubMonitor("Generating Report", 25);
        m.message("Loading Credit Memos");
        UniqueUnitEao unitEao = new UniqueUnitEao(uuEm);

        List<Object[]> rows = new ArrayList<>();
        List<Document> documents = new DocumentEao(redTapeEm).findDocumentsBetweenDates(start, end,
                DocumentType.CREDIT_MEMO, DocumentType.ANNULATION_INVOICE);
        m.setWorkRemaining(documents.size());
        for (Document document : documents) {
            String salesDate = SimpleDateFormat.getDateInstance(SimpleDateFormat.SHORT, Locale.GERMANY).format(document.getActual());
            UiCustomer customer = customerService.asUiCustomer(document.getDossier().getCustomerId());
            double price = 0;
            double afterTaxPrice = 0;
            boolean full = true;
            String sopos = "";
            for (Position position : document.getPositions().values()) {
                if ( position.getType() == PositionType.COMMENT ) continue;
                if ( position.getType() == PositionType.UNIT || position.getType() == PositionType.UNIT_ANNEX ) {
                    full = position.getType() == PositionType.UNIT;
                    String refurbishId = unitEao.findById(position.getUniqueUnitId()).getRefurbishId();
                    sopos += (sopos.isEmpty()) ? refurbishId : " ," + refurbishId;
                } else if ( position.getType() == PositionType.SHIPPING_COST ) {
                    Document invoice = document.getDossier().getActiveDocuments(DocumentType.INVOICE).get(0);
                    SortedMap<Integer, Position> positions = invoice.getPositions(PositionType.SHIPPING_COST);
                    if ( positions.size() > 0 ) {
                        Position get = positions.get(positions.firstKey());
                        if ( Math.abs(Math.abs(get.getPrice()) - Math.abs(position.getPrice())) > 0.001 ) full = false;
                    }
                }
                price += position.getPrice();
                afterTaxPrice += position.toAfterTaxPrice();
            }
            rows.add(new Object[]{
                document.getDossier().getIdentifier(),
                customer.getId(),
                salesDate,
                document.getIdentifier(),
                (full) ? "V" : "T",
                price,
                afterTaxPrice,
                customer.getCompany(),
                customer.toNameLine(),
                sopos
            });
            m.worked(1);
        }
        m.message("Generating Document");
        m.setWorkRemaining(10);
        STable table = new STable();

        table.setTableFormat(new CFormat(BLACK, WHITE, new CBorder(BLACK)));
        table.setHeadlineFormat(new CFormat(CFormat.FontStyle.BOLD, Color.BLACK, Color.YELLOW,
                CFormat.HorizontalAlignment.LEFT, CFormat.VerticalAlignment.BOTTOM, CFormat.Representation.DEFAULT));

        table.add(new STableColumn("DossierId", 15)).add(new STableColumn("Kid", 10)).add(new STableColumn("Belegdatum", 10));
        table.add(new STableColumn("DokumentId", 15)).add(new STableColumn("Voll/Teil", 10));
        table.add(new STableColumn("Netto", 10, new CFormat(CFormat.HorizontalAlignment.RIGHT, CFormat.Representation.CURRENCY_EURO)));
        table.add(new STableColumn("Brutto", 10, new CFormat(CFormat.HorizontalAlignment.RIGHT, CFormat.Representation.CURRENCY_EURO)));
        table.add(new STableColumn("Firma", 25)).add(new STableColumn("Name", 25)).add(new STableColumn("SopoNr.", 10));
        table.setModel(new STableModelList(rows));

        CCalcDocument cdoc = new TempCalcDocument("Gutschriften_");

        cdoc.add(new CSheet("Sheet1", table));

        File file = LucidCalc.createWriter(LucidCalc.Backend.XLS).write(cdoc);

        m.finish();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd_MM_yyy");

        return new FileJacket("Gutschriften_" + dateFormat.format(start) + "-" + dateFormat.format(end), ".xls", file);
    }
}
