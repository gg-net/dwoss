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
package eu.ggnet.dwoss.uniqueunit.op;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.joda.time.LocalDate;

import eu.ggnet.lucidcalc.CBorder;
import eu.ggnet.lucidcalc.CCalcDocument;
import eu.ggnet.lucidcalc.CFormat;
import eu.ggnet.lucidcalc.CSheet;
import eu.ggnet.lucidcalc.LucidCalc;
import eu.ggnet.lucidcalc.STable;
import eu.ggnet.lucidcalc.STableColumn;
import eu.ggnet.lucidcalc.STableModelList;
import eu.ggnet.lucidcalc.TempCalcDocument;

import eu.ggnet.dwoss.progress.MonitorFactory;
import eu.ggnet.dwoss.progress.SubMonitor;

import eu.ggnet.dwoss.rules.TradeName;

import eu.ggnet.dwoss.uniqueunit.assist.UniqueUnitSupport;
import eu.ggnet.dwoss.uniqueunit.eao.UniqueUnitEao;
import eu.ggnet.dwoss.uniqueunit.entity.UniqueUnit;

import eu.ggnet.dwoss.util.DateFormats;
import eu.ggnet.dwoss.util.FileJacket;

import lombok.Data;

import static java.awt.Color.BLACK;
import static java.awt.Color.WHITE;

/**
 *
 * @author pascal.perau
 */
@Stateless
public class UniqueUnitReporterOperation implements UniqueUnitReporter {

    @Data
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
    }

    private static final String MONTH_PATTERN = "yyyy-MM";

    @Inject
    private UniqueUnitSupport unitSupport;

    @Inject
    private MonitorFactory monitorFactory;

    // TODO: Document Me
    @Override
    public FileJacket quality(Date start, Date end, TradeName contractor) {

        SubMonitor m = monitorFactory.newSubMonitor("Gerätequalitätsreport", 10);
        m.start();
        m.message("Loading reciepted Units");

        LocalDate current = new LocalDate(start.getTime());
        LocalDate endDate = new LocalDate(end.getTime());

        List<UniqueUnit> units = new UniqueUnitEao(unitSupport.getEntityManager()).findBetweenInputDatesAndContractor(start, end, contractor);
        m.worked(5, "Sorting Data");

        Set<String> months = new HashSet<>();

        //prepare set of months
        while (current.isBefore(endDate)) {
            months.add(current.toString(MONTH_PATTERN));
            current = current.plusDays(1);
        }

        //prepare Map sorted by months that contains a map sorted by condition
        SortedMap<String, UnitQualityContainer> unitMap = new TreeMap<>();
        for (String month : months) {
            unitMap.put(month, new UnitQualityContainer());
        }
        m.worked(1);

        //count monthly receipted units sorted by condition
        for (UniqueUnit uniqueUnit : units) {
            current = new LocalDate(uniqueUnit.getInputDate().getTime());
            switch (uniqueUnit.getCondition()) {
                case AS_NEW:
                    unitMap.get(current.toString(MONTH_PATTERN)).incrementAsNew();
                    break;
                case ALMOST_NEW:
                    unitMap.get(current.toString(MONTH_PATTERN)).incrementAlmostNew();
                    break;
                case USED:
                    unitMap.get(current.toString(MONTH_PATTERN)).incrementUsed();
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
        return new FileJacket("Aufnahme_nach_Qualität_" + contractor + "_" + DateFormats.ISO.format(start) + "_" + DateFormats.ISO.format(end), ".xls", file);
    }
}
