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
package eu.ggnet.dwoss.uniqueunit.ee.op;

import java.awt.Color;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Map.Entry;
import java.util.*;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.apache.commons.lang3.time.DateUtils;

import eu.ggnet.dwoss.common.api.values.TradeName;
import eu.ggnet.dwoss.common.ee.Step;
import eu.ggnet.dwoss.mandator.api.value.Contractors;
import eu.ggnet.dwoss.progress.MonitorFactory;
import eu.ggnet.dwoss.progress.SubMonitor;
import eu.ggnet.dwoss.uniqueunit.ee.assist.UniqueUnits;
import eu.ggnet.dwoss.uniqueunit.ee.eao.BrandContractorCount;
import eu.ggnet.dwoss.uniqueunit.ee.eao.UniqueUnitEao;
import eu.ggnet.dwoss.uniqueunit.ee.entity.UniqueUnit;
import eu.ggnet.dwoss.core.common.FileJacket;
import eu.ggnet.dwoss.core.system.Utils;
import eu.ggnet.lucidcalc.*;
import eu.ggnet.lucidcalc.jexcel.JExcelLucidCalcWriter;

import static eu.ggnet.lucidcalc.CFormat.FontStyle.BOLD_ITALIC;
import static eu.ggnet.lucidcalc.CFormat.HorizontalAlignment.CENTER;
import static eu.ggnet.lucidcalc.CFormat.HorizontalAlignment.RIGHT;
import static java.awt.Color.*;

/**
 *
 * @author pascal.perau
 */
@Stateless
public class UniqueUnitReporterOperation implements UniqueUnitReporter {

    private class UnitQualityContainer {

        private int asNew;

        private int almostNew;

        private int used;

        public void incrementAsNew() {
            asNew++;
        }

        public void incrementAlmostNew() {
            almostNew++;
        }

        public void incrementUsed() {
            used++;
        }

        public int getAsNew() {
            return asNew;
        }

        public int getAlmostNew() {
            return almostNew;
        }

        public int getUsed() {
            return used;
        }
                
    }

    private static final DateFormat YEAR_MONTH = new SimpleDateFormat("yyyy-MM");

    private static final String MONTH_PATTERN = "yyyy-MM";

    @Inject
    @UniqueUnits
    private EntityManager em;

    @Inject
    private MonitorFactory monitorFactory;

    @Inject
    private Contractors contractors;

    // TODO: Document Me
    @Override
    public FileJacket quality(Date start, Date end, TradeName contractor) {

        SubMonitor m = monitorFactory.newSubMonitor("Gerätequalitätsreport", 10);
        m.start();
        m.message("Loading reciepted Units");

        List<UniqueUnit> units = new UniqueUnitEao(em).findBetweenInputDatesAndContractor(start, end, contractor);
        m.worked(5, "Sorting Data");

        Set<String> months = new HashSet<>();

        Date actual = start;

        while (actual.before(end)) {
            months.add(YEAR_MONTH.format(actual));
            actual = DateUtils.addDays(actual, 1);
        }

        //prepare Map sorted by months that contains a map sorted by condition
        SortedMap<String, UnitQualityContainer> unitMap = new TreeMap<>();
        for (String month : months) {
            unitMap.put(month, new UnitQualityContainer());
        }
        m.worked(1);

        //count monthly receipted units sorted by condition
        for (UniqueUnit uniqueUnit : units) {
            actual = uniqueUnit.getInputDate();
            switch (uniqueUnit.getCondition()) {
                case AS_NEW:
                    unitMap.get(YEAR_MONTH.format(actual)).incrementAsNew();
                    break;
                case ALMOST_NEW:
                    unitMap.get(YEAR_MONTH.format(actual)).incrementAlmostNew();
                    break;
                case USED:
                    unitMap.get(YEAR_MONTH.format(actual)).incrementUsed();
                    break;
            }
        }
        m.worked(2, "Creating Document");

        List<Object[]> rows = new ArrayList<>();
        for (String month : unitMap.keySet()) {
            rows.add(new Object[]{
                month,
                unitMap.get(month).getAsNew(),
                unitMap.get(month).getAlmostNew(),
                unitMap.get(month).getUsed()});
            m.worked(5);
        }

        STable table = new STable();
        table.setTableFormat(new CFormat(BLACK, WHITE, new CBorder(BLACK)));
        table.setHeadlineFormat(new CFormat(CFormat.FontStyle.BOLD, Color.BLACK, Color.YELLOW, CFormat.HorizontalAlignment.LEFT, CFormat.VerticalAlignment.BOTTOM, CFormat.Representation.DEFAULT));
        table.add(new STableColumn("Monat", 15)).add(new STableColumn("neuwertig", 15)).add(new STableColumn("nahezu neuwerig", 15)).add(new STableColumn("gebraucht", 15));
        table.setModel(new STableModelList(rows));

        CCalcDocument cdoc = new TempCalcDocument("Qualitaet_");
        cdoc.add(new CSheet("Sheet1", table));

        File file = LucidCalc.createWriter(LucidCalc.Backend.XLS).write(cdoc);
        m.finish();
        return new FileJacket("Aufnahme_nach_Qualität_" + contractor + "_" + Utils.ISO_DATE.format(start) + "_" + Utils.ISO_DATE.format(end), ".xls", file);
    }

    @Override
    public FileJacket unitInputAsXls(Date start, Date end, Step step) {
        String name = "Aufnahmemengereport";
        SubMonitor m = monitorFactory.newSubMonitor(name);
        m.start();

        UniqueUnitEao eao = new UniqueUnitEao(em);
        NavigableSet<TradeName> usedManufacturers = eao.findUsedManufactuers();
        NavigableMap<Date, BrandContractorCount> revenue = eao.countByInputDateContractor(start, end, step);

        STable template = new STable();
        template.setTableFormat(new CFormat(BLACK, WHITE, new CBorder(BLACK)));
        template.setHeadlineFormat(new CFormat(BOLD_ITALIC, WHITE, BLUE, CENTER, new CBorder(BLACK)));
        template.add(new STableColumn(step.name(), 12));
        for (TradeName manufacturer : usedManufacturers) {
            template.add(new STableColumn(manufacturer.getName(), 15, new CFormat(RIGHT)));
        }
        template.add(new STableColumn("Summe", 18, new CFormat(RIGHT)));

        STable all = new STable(template);
        all.setModel(new STableModelList(buildSumModel(step, usedManufacturers, revenue)));

        CCalcDocument cdoc = new TempCalcDocument(name);
        cdoc.add(new CSheet("Input_All", all));

        for (TradeName contractor : contractors.all()) {
            STable simple = new STable(template);
            simple.setModel(new STableModelList(buildContractorModel(step, contractor, usedManufacturers, revenue)));
            cdoc.add(new CSheet("Input_" + contractor, simple));
        }

        FileJacket result = new FileJacket(name, ".xls", new JExcelLucidCalcWriter().write(cdoc));
        m.finish();
        return result;
    }

    private List<Object[]> buildSumModel(Step step, NavigableSet<TradeName> usedManufacturers, NavigableMap<Date, BrandContractorCount> revenue) {
        List<Object[]> rows = new ArrayList<>();
        for (Entry<Date, BrandContractorCount> e : revenue.entrySet()) {
            BrandContractorCount r = e.getValue();

            Object[] row = new Object[usedManufacturers.size() + 2];
            row[0] = step.format(e.getKey());
            int count = 1;
            for (TradeName manufacturer : usedManufacturers) {
                row[count] = r.countByManufacturer(manufacturer);
                count++;
            }
            row[count] = r.count();
            rows.add(row);
        }
        return rows;
    }

    private List<Object[]> buildContractorModel(Step step, TradeName contractor, NavigableSet<TradeName> usedManufacturers, NavigableMap<Date, BrandContractorCount> revenue) {
        List<Object[]> rows = new ArrayList<>();
        for (Entry<Date, BrandContractorCount> e : revenue.entrySet()) {
            BrandContractorCount r = e.getValue();

            Object[] row = new Object[usedManufacturers.size() + 2];
            row[0] = step.format(e.getKey());
            int count = 1;
            for (TradeName manufacturer : usedManufacturers) {
                row[count] = r.countByContractorManufacturer(contractor, manufacturer);
                count++;
            }
            row[count] = r.countByContractor(contractor);
            rows.add(row);
        }
        return rows;
    }

}
