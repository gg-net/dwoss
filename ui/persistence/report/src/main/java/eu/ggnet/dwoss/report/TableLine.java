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
package eu.ggnet.dwoss.report;

import eu.ggnet.dwoss.report.entity.ReportLine;

import javafx.beans.property.*;

import lombok.*;

@ToString
@EqualsAndHashCode
public class TableLine {

    @Getter
    @Setter
    private ReportLine reportLine;

    private final BooleanProperty shouldReportedProperty = new SimpleBooleanProperty(true);

    public TableLine(ReportLine reportLine) {
        this.reportLine = reportLine;
    }

    public double getCpPercentage() {
        if ( reportLine.getContractorReferencePrice() == 0 ) return 0;
        return reportLine.getPrice() / reportLine.getContractorReferencePrice();
    }

    public double getMargin() {
        return reportLine.getPrice() - reportLine.getPurchasePrice();
    }

    public BooleanProperty shouldReportedProperty() {
        return shouldReportedProperty;
    }

    public void setShouldReported(boolean report) {
        shouldReportedProperty.set(report);
    }

    public boolean isShouldReported() {
        return shouldReportedProperty.get();
    }
}
