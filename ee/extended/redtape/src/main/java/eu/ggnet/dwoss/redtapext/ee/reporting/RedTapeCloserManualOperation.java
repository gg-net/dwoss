/*
 * Copyright (C) 2019 GG-Net GmbH
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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.core.common.FileJacket;
import eu.ggnet.dwoss.core.system.persistence.DefaultEao;
import eu.ggnet.dwoss.report.api.StockCount;
import eu.ggnet.dwoss.report.ee.assist.Reports;
import eu.ggnet.lucidcalc.*;

import static eu.ggnet.lucidcalc.CFormat.FontStyle.BOLD_ITALIC;
import static eu.ggnet.lucidcalc.CFormat.HorizontalAlignment.CENTER;
import static java.awt.Color.*;

/**
 *
 * @author oliver.guenther
 */
@Stateless
public class RedTapeCloserManualOperation implements RedTapeCloserManual {

    private final static Logger L = LoggerFactory.getLogger(RedTapeCloserManualOperation.class);

    @Inject
    private RedTapeCloserAutomaticOperation op;

    @Inject
    @Reports
    private EntityManager reportEm;

    /**
     * Executes the closing manual.
     * See {@link #closeing(java.lang.String, boolean) } for details.
     * <p>
     * @param arranger the arranger
     */
    @Override
    public void executeManual(String arranger) {
        L.debug("{} called manual closing operation", arranger);
        op.closeing(arranger, true);
    }

    @Override
    public StockCount countStock() {
        return op.countStock();
    }

    @Override
    public FileJacket countStockAsXls() {
        StockCount c = op.countStock();

        List<Object[]> rows = new ArrayList<>();

        rows.add(new Object[]{
            c.created(),
            c.shipmentsAnnounced(),
            c.shipmentsAnnouncedUnits(),
            c.shipmentsDelivered(),
            c.shipmentsDeliveredUnits(),
            c.shipmentsOpened(),
            c.shipmentsOpenedRemainderUnits(),
            c.stockUnitsAvailable(),
            c.stockUnitsAvailablePriceZero(),
            c.stockUnitsAvailablePriceBelowOneHundred(),
            c.stockUnitsAvailablePriceBelowThreeHundred(),
            c.stockUnitsAvailablePriceAboveThreeHundred(),
            c.stockUnitsInTransfer(),
            c.stockUnitsInTransferPriceZero(),
            c.stockUnitsInTransferPriceBelowOneHundred(),
            c.stockUnitsInTransferPriceBelowThreeHundred(),
            c.stockUnitsInTransferPriceAboveThreeHundred()
        });

        STable table = new STable();
        table.setTableFormat(new CFormat(BLACK, WHITE, new CBorder(BLACK)));
        table.setHeadlineFormat(new CFormat(BOLD_ITALIC, WHITE, BLUE, CENTER, new CBorder(BLACK)));
        table.add(new STableColumn("Datum", 13))
                .add(new STableColumn("Angekündigt", 18))
                .add(new STableColumn("Angekündigt Geräte", 22))
                .add(new STableColumn("Geliefert", 18))
                .add(new STableColumn("Geliefert Geräte", 20))
                .add(new STableColumn("Geöffnet", 18))
                .add(new STableColumn("Geöffnet Restmenge", 22))
                .add(new STableColumn("Geräte verfügbar", 20))
                .add(new STableColumn("Geräte verfügbar 0 €", 22))
                .add(new STableColumn("Geräte verfügbar < 100 €", 25))
                .add(new STableColumn("Geräte verfügbar < 300 €", 25))
                .add(new STableColumn("Geräte verfügbar > 300 €", 25))
                .add(new STableColumn("Geräte in Transfer", 20))
                .add(new STableColumn("Geräte in Transfer 0 €", 22))
                .add(new STableColumn("Geräte in Transfer < 100 €", 25))
                .add(new STableColumn("Geräte in Transfer < 300 €", 25))
                .add(new STableColumn("Geräte in Transfer > 300 €", 25));
        table.setModel(new STableModelList(rows));

        CCalcDocument cdoc = new TempCalcDocument("Lagerbestand");
        cdoc.add(new CSheet("Lager", table));

        File file = LucidCalc.createWriter(LucidCalc.Backend.XLS).write(cdoc);
        FileJacket result = new FileJacket("Lagerbestand", ".xls", file);
        return result;
    }

    @Override
    public FileJacket countStockHistoryAsXls() {
        DefaultEao<eu.ggnet.dwoss.report.ee.entity.StockCount> stockCountEao = new DefaultEao<>(eu.ggnet.dwoss.report.ee.entity.StockCount.class, reportEm);

        List<Object[]> rows = new ArrayList<>();

        for (eu.ggnet.dwoss.report.ee.entity.StockCount stockCount : stockCountEao.findAll()) {
            StockCount c = stockCount.toApi();

            rows.add(new Object[]{
                c.created(),
                c.shipmentsAnnounced(),
                c.shipmentsAnnouncedUnits(),
                c.shipmentsDelivered(),
                c.shipmentsDeliveredUnits(),
                c.shipmentsOpened(),
                c.shipmentsOpenedRemainderUnits(),
                c.stockUnitsAvailable(),
                c.stockUnitsAvailablePriceZero(),
                c.stockUnitsAvailablePriceBelowOneHundred(),
                c.stockUnitsAvailablePriceBelowThreeHundred(),
                c.stockUnitsAvailablePriceAboveThreeHundred(),
                c.stockUnitsInTransfer(),
                c.stockUnitsInTransferPriceZero(),
                c.stockUnitsInTransferPriceBelowOneHundred(),
                c.stockUnitsInTransferPriceBelowThreeHundred(),
                c.stockUnitsInTransferPriceAboveThreeHundred()
            });
        }

        STable table = new STable();
        table.setTableFormat(new CFormat(BLACK, WHITE, new CBorder(BLACK)));
        table.setHeadlineFormat(new CFormat(BOLD_ITALIC, WHITE, BLUE, CENTER, new CBorder(BLACK)));
        table.add(new STableColumn("Datum", 13))
                .add(new STableColumn("Angekündigt", 18))
                .add(new STableColumn("Angekündigt Geräte", 22))
                .add(new STableColumn("Geliefert", 18))
                .add(new STableColumn("Geliefert Geräte", 20))
                .add(new STableColumn("Geöffnet", 18))
                .add(new STableColumn("Geöffnet Restmenge", 22))
                .add(new STableColumn("Geräte verfügbar", 20))
                .add(new STableColumn("Geräte verfügbar 0 €", 22))
                .add(new STableColumn("Geräte verfügbar < 100 €", 25))
                .add(new STableColumn("Geräte verfügbar < 300 €", 25))
                .add(new STableColumn("Geräte verfügbar > 300 €", 25))
                .add(new STableColumn("Geräte in Transfer", 20))
                .add(new STableColumn("Geräte in Transfer 0 €", 22))
                .add(new STableColumn("Geräte in Transfer < 100 €", 25))
                .add(new STableColumn("Geräte in Transfer < 300 €", 25))
                .add(new STableColumn("Geräte in Transfer > 300 €", 25));
        table.setModel(new STableModelList(rows));

        CCalcDocument cdoc = new TempCalcDocument("Lagerbestand");
        cdoc.add(new CSheet("Lager", table));

        File file = LucidCalc.createWriter(LucidCalc.Backend.XLS).write(cdoc);
        FileJacket result = new FileJacket("Lagerbestand", ".xls", file);
        return result;

    }

}
