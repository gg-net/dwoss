/*
 * Copyright (C) 2021 GG-Net GmbH
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
package eu.ggnet.dwoss.receipt.ui.tryout.stub;

import java.awt.Color;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import eu.ggnet.dwoss.core.common.FileJacket;
import eu.ggnet.dwoss.core.common.values.tradename.TradeName;
import eu.ggnet.dwoss.core.system.GlobalConfig;
import eu.ggnet.dwoss.receipt.ee.reporting.RefurbishmentReporter;
import eu.ggnet.dwoss.uniqueunit.ee.entity.UniqueUnit;
import eu.ggnet.dwoss.uniqueunit.ee.entity.UniqueUnit.Identifier;
import eu.ggnet.dwoss.uniqueunit.ee.entity.UniqueUnit.StaticInternalComment;
import eu.ggnet.dwoss.uniqueunit.ee.format.ProductFormater;
import eu.ggnet.lucidcalc.*;

import static eu.ggnet.lucidcalc.CFormat.FontStyle.BOLD;
import static eu.ggnet.lucidcalc.CFormat.FontStyle.NORMAL;
import static eu.ggnet.lucidcalc.CFormat.HorizontalAlignment.LEFT;
import static eu.ggnet.lucidcalc.CFormat.HorizontalAlignment.RIGHT;
import static eu.ggnet.lucidcalc.CFormat.Representation.CURRENCY_EURO;
import static eu.ggnet.lucidcalc.CFormat.VerticalAlignment.MIDDLE;

/**
 *
 * @author mirko.schulze
 */
public class RefurbishmentReporterStub implements RefurbishmentReporter {

    private final static CFormat EURO_FORMAT = new CFormat(NORMAL, Color.BLACK, Color.WHITE, RIGHT, MIDDLE, CURRENCY_EURO, new CBorder(Color.LIGHT_GRAY, CBorder.LineStyle.HAIR));

    private final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");

    @Override
    public FileJacket toXls(TradeName contractor, Date start, Date end) {
        double singleRefurbishPrice = 0.; // TODO: Init me from contractor
        double singleRefillPrice = 0.; // TODO: Init me from contractor.
        double refurbishedPriceSum = 0.;
        double refilledPriceSum = 0.;
        List<Object[]> refurbishedSopoUnits = new ArrayList<>();
        List<Object[]> refilledSopoUnits = new ArrayList<>();
//        List<UniqueUnit> units = new UniqueUnitEao(uuem).findBetweenInputDatesAndContractor(start, end, contractor);
        List<UniqueUnit> uus = new ProductProcessorStub().uniqueUnitAgent().findAll(UniqueUnit.class);
        List<UniqueUnit> units = uus.stream().filter(u -> (u.getMfgDate().after(start)&&u.getMfgDate().before(end))).collect(Collectors.toList());
        for (UniqueUnit uu : units) {
            if ( uu.getInternalComments().contains(StaticInternalComment.REFILLED) ) {
                refilledSopoUnits.add(new Object[]{uu.getIdentifier(Identifier.SERIAL), ProductFormater.toName(uu.getProduct())});
                refilledPriceSum += singleRefillPrice;
            } else if ( uu.getInternalComments().contains(StaticInternalComment.RECOVERT) ) {
                refurbishedSopoUnits.add(new Object[]{uu.getIdentifier(Identifier.SERIAL), ProductFormater.toName(uu.getProduct())});
                refurbishedPriceSum += singleRefurbishPrice;
            }
        }

        double tax = (refilledPriceSum + refurbishedPriceSum) * GlobalConfig.DEFAULT_TAX.tax();

        CSheet summary = new CSheet("Summery", 5, 30, 15, 15, 15);
        SBlock headerAndDate = new SBlock();
        SBlock data = new SBlock();
        SBlock prices = new SBlock();
        headerAndDate.setFormat(new CFormat(BOLD, Color.BLACK, Color.WHITE, LEFT, new CBorder(Color.LIGHT_GRAY, CBorder.LineStyle.HAIR)));
        headerAndDate.add("Report über recoverte und wiederaufgefüllte Geräte");
        headerAndDate.add("Reportzeitraum:", DATE_FORMAT.format(start) + " - " + DATE_FORMAT.format(end));
        summary.addBelow(1, 1, headerAndDate);
        data.add("", "Anzahl", "Einzelpreis", "Summe");
        data.add("Recoverte Geräte", refurbishedSopoUnits.size(), singleRefurbishPrice, EURO_FORMAT, refurbishedPriceSum, EURO_FORMAT);
        data.add("Wiederaufgefüllte Geräte", refilledSopoUnits.size(), singleRefillPrice, EURO_FORMAT, refilledPriceSum, EURO_FORMAT);
        summary.addBelow(1, 1, data);
        prices.add("", "", "netto", refilledPriceSum + refurbishedPriceSum, EURO_FORMAT);
        prices.add("", "", "Mwst", tax, EURO_FORMAT);
        prices.add("", "", "Mwst", refilledPriceSum + refurbishedPriceSum + tax, EURO_FORMAT);
        summary.addBelow(1, 1, prices);

        STable refurbishedTable = new STable();
        refurbishedTable.setHeadlineFormat(new CFormat(BOLD, Color.BLACK, Color.YELLOW, RIGHT, new CBorder(Color.BLACK)));
        refurbishedTable.add(new STableColumn("Seriennummer", 22)).add(new STableColumn("Bezeichnnung", 40));
        refurbishedTable.setModel(new STableModelList(refurbishedSopoUnits));

        STable refilledTable = new STable(refurbishedTable);
        refilledTable.setModel(new STableModelList(refilledSopoUnits));

        CCalcDocument cdoc = new TempCalcDocument("RefurbishedReport_" + contractor);
        cdoc.add(summary);
        cdoc.add(new CSheet("Refurbished", refurbishedTable));
        cdoc.add(new CSheet("Aufgefüllt", refilledTable));

        File file = LucidCalc.createWriter(LucidCalc.Backend.XLS).write(cdoc);
        FileJacket result = new FileJacket("RefurbishedReport_" + contractor, ".xls", file);
        return result;
    }

}
