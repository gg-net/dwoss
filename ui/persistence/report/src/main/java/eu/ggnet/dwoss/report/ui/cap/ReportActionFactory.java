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
package eu.ggnet.dwoss.report.ui.cap;

import java.util.Arrays;
import java.util.List;

import org.openide.util.lookup.ServiceProvider;

import eu.ggnet.saft.core.ActionFactory;

@ServiceProvider(service = ActionFactory.class)
public class ReportActionFactory implements ActionFactory {

    @Override
    public List<MetaAction> createMetaActions() {
        return Arrays.asList(
                new MetaAction("Geschäftsführung", "Abschluss Reporte", new OpenRawReportLinesAction()),
                new MetaAction("Geschäftsführung", "Abschluss Reporte", new CreateNewReportAction()),
                new MetaAction("Geschäftsführung", "Abschluss Reporte", new SelectExistingReportAction()),
                new MetaAction("Geschäftsführung", "Abschluss Reporte", new CreateReturnsReportAction()),
                new MetaAction("Geschäftsführung", "Abschluss Reporte", new ExportRevenueReportAction()));
    }
}
