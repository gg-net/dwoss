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
package eu.ggnet.dwoss.report.ui.main;

import java.util.*;
import java.util.stream.Collectors;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import eu.ggnet.dwoss.report.ee.entity.ReportLine;

/**
 * Selectable wrapper for the report line.
 *
 * @author oliver.guenther
 */
public class SelectableReportLine implements Comparable<SelectableReportLine> {

    public final BooleanProperty selectionProperty = new SimpleBooleanProperty(true);

    public final ReportLine reportLine;

    public SelectableReportLine(ReportLine reportLine) {
        this.reportLine = Objects.requireNonNull(reportLine, "ReportLine must not be null");
    }

    public static NavigableSet<SelectableReportLine> wrap(NavigableSet<ReportLine> lines) {
        return Objects.requireNonNull(lines).stream().map(l -> new SelectableReportLine(l)).collect(Collectors.toCollection(() -> new TreeSet<>()));
    }

    @Override
    public int compareTo(SelectableReportLine other) {
        return reportLine.compareTo(other.reportLine);
    }

}
