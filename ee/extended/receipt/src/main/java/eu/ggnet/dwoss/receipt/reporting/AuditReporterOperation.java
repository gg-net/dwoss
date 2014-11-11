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
package eu.ggnet.dwoss.receipt.reporting;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import eu.ggnet.dwoss.progress.MonitorFactory;
import eu.ggnet.dwoss.progress.SubMonitor;
import eu.ggnet.dwoss.stock.assist.StockSupport;
import eu.ggnet.dwoss.stock.eao.StockTransactionEao;
import eu.ggnet.dwoss.stock.entity.StockTransaction;
import eu.ggnet.dwoss.stock.entity.StockTransactionStatusType;
import eu.ggnet.dwoss.stock.entity.StockTransactionType;
import eu.ggnet.dwoss.stock.entity.StockUnit;
import eu.ggnet.dwoss.uniqueunit.assist.UniqueUnitSupport;
import eu.ggnet.dwoss.uniqueunit.eao.UniqueUnitEao;
import eu.ggnet.dwoss.uniqueunit.entity.UniqueUnit;
import eu.ggnet.dwoss.uniqueunit.format.ProductFormater;
import eu.ggnet.dwoss.uniqueunit.format.UniqueUnitFormater;
import eu.ggnet.lucidcalc.CBorder;
import eu.ggnet.lucidcalc.CCalcDocument;
import eu.ggnet.lucidcalc.CFormat;
import eu.ggnet.lucidcalc.CSheet;
import eu.ggnet.lucidcalc.LucidCalc;
import eu.ggnet.lucidcalc.STable;
import eu.ggnet.lucidcalc.STableColumn;
import eu.ggnet.lucidcalc.STableModelList;
import eu.ggnet.lucidcalc.TempCalcDocument;

import eu.ggnet.dwoss.util.FileJacket;

import static eu.ggnet.lucidcalc.CFormat.FontStyle.BOLD_ITALIC;
import static eu.ggnet.lucidcalc.CFormat.HorizontalAlignment.CENTER;
import static java.awt.Color.*;

/**
 * Operation for Audit Activity of the Stock.
 * <p/>
 * @author oliver.guenther
 */
@Stateless

public class AuditReporterOperation implements AuditReporter {

    @Inject
    private UniqueUnitSupport uus;

    @Inject
    private StockSupport stocks;

    @Inject
    private MonitorFactory monitorFactory;

    /**
     * Returns an audit report of units which are input between the dates.
     * <p/>
     * @return an audit report of units which are input between the dates.
     */
    @Override
    public FileJacket onRollIn() {
        SubMonitor m = monitorFactory.newSubMonitor("AuditReport", 100);
        m.message("loading UniqueUnits");
        m.start();
        List<StockTransaction> rollInTransactions = new StockTransactionEao(stocks.getEntityManager())
                .findByTypeAndStatus(StockTransactionType.ROLL_IN, StockTransactionStatusType.PREPARED);
        List<Integer> uuIds = toUniqueUnitIds(rollInTransactions);

        List<UniqueUnit> uniqueUnits = new UniqueUnitEao(uus.getEntityManager()).findByIds(uuIds);

        m.worked(5, "preparing Document");
        List<Object[]> rows = new ArrayList<>();
        for (UniqueUnit uu : uniqueUnits) {
            rows.add(new Object[]{
                uu.getRefurbishId(),
                uu.getProduct().getGroup().getNote(),
                uu.getProduct().getPartNo(),
                uu.getSerial(),
                ProductFormater.toName(uu.getProduct()),
                uu.getContractor(),
                UniqueUnitFormater.toSingleLineAccessories(uu),
                UniqueUnitFormater.toSingleLineComment(uu),
                UniqueUnitFormater.toSingleLineInternalComment(uu),
                uu.getCondition().getNote(),
                uu.getShipmentLabel(),
                uu.getProduct().getDescription(),
                ""
            });
        }

        CSheet sheet = new CSheet("Audit");
        STable table = new STable();
        table.setHeadlineFormat(new CFormat(BOLD_ITALIC, BLACK, WHITE, CENTER, new CBorder(BLACK)));
        table.add(new STableColumn("SopoNr", 7)).add(new STableColumn("Warengruppe", 13)).add(new STableColumn("ArtikelNr", 15)).add(new STableColumn("Seriennummer", 27));
        table.add(new STableColumn("Name", 30)).add(new STableColumn("Lieferant", 12)).add(new STableColumn("Zubehör", 50)).add(new STableColumn("Bemerkung", 50));
        table.add(new STableColumn("Interne Bemerkung", 30)).add(new STableColumn("Zustand", 12)).add(new STableColumn("Shipment", 12)).add(new STableColumn("Beschreibung", 50));
        table.setModel(new STableModelList(rows));
        sheet.addBelow(table);
        CCalcDocument document = new TempCalcDocument();
        document.add(sheet);
        FileJacket fj = new FileJacket("Audit", ".xls", LucidCalc.createWriter(LucidCalc.Backend.XLS).write(document));
        m.finish();
        return fj;
    }

    /**
     * Returns an audit report of units which are on a roll in transaction, but not yet rolled in.
     * <p/>
     * @return an audit report of units which are on a roll in transaction, but not yet rolled in.
     */
    
    @Override
    public FileJacket byRange(Date start, Date end) {
        SubMonitor m = monitorFactory.newSubMonitor("AuditReport", 100);
        m.message("loading UniqueUnits");
        m.start();
        List<UniqueUnit> uniqueUnits = new UniqueUnitEao(uus.getEntityManager()).findBetweenInputDates(start, end);

        m.worked(5, "preparing Document");
        List<Object[]> rows = new ArrayList<>();
        for (UniqueUnit uu : uniqueUnits) {
            rows.add(new Object[]{
                uu.getRefurbishId(),
                uu.getProduct().getGroup().getNote(),
                uu.getProduct().getPartNo(),
                uu.getSerial(),
                ProductFormater.toName(uu.getProduct()),
                uu.getContractor(),
                UniqueUnitFormater.toSingleLineAccessories(uu),
                UniqueUnitFormater.toSingleLineComment(uu),
                UniqueUnitFormater.toSingleLineInternalComment(uu),
                uu.getCondition().getNote(),
                uu.getShipmentLabel(),
                uu.getProduct().getDescription(),
                ""
            });
        }

        CSheet sheet = new CSheet("Audit");
        STable table = new STable();
        table.setHeadlineFormat(new CFormat(BOLD_ITALIC, BLACK, WHITE, CENTER, new CBorder(BLACK)));
        table.add(new STableColumn("SopoNr", 7)).add(new STableColumn("Warengruppe", 13)).add(new STableColumn("ArtikelNr", 15)).add(new STableColumn("Seriennummer", 27));
        table.add(new STableColumn("Name", 30)).add(new STableColumn("Lieferant", 12)).add(new STableColumn("Zubehör", 50)).add(new STableColumn("Bemerkung", 50));
        table.add(new STableColumn("Interne Bemerkung", 30)).add(new STableColumn("Zustand", 12)).add(new STableColumn("Shipment", 12)).add(new STableColumn("Beschreibung", 50));
        table.setModel(new STableModelList(rows));
        sheet.addBelow(table);
        CCalcDocument document = new TempCalcDocument();
        document.add(sheet);
        FileJacket fj = new FileJacket("Audit", ".xls", LucidCalc.createWriter(LucidCalc.Backend.XLS).write(document));
        m.finish();
        return fj;
    }

    private List<Integer> toUniqueUnitIds(List<StockTransaction> rollInTransactions) {
        List<Integer> uuIds = new ArrayList<>();
        for (StockTransaction stockTransaction : rollInTransactions) {
            for (StockUnit stockUnit : stockTransaction.getUnits()) {
                uuIds.add(stockUnit.getUniqueUnitId());
            }
        }
        return uuIds;
    }
}
