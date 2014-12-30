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
package eu.ggnet.dwoss.report.action;

import java.awt.event.ActionEvent;

import eu.ggnet.dwoss.report.RevenueReportSelectorPane;
import eu.ggnet.dwoss.report.op.RevenueReporter;
import eu.ggnet.saft.core.*;
import eu.ggnet.saft.core.authorisation.AccessableAction;

import static eu.ggnet.dwoss.rights.api.AtomicRight.EXPORT_REVENUE_REPORT;
import static eu.ggnet.saft.core.Client.lookup;

/**
 *
 * @author pascal.perau
 */
public class ExportRevenueReportAction extends AccessableAction {

    public ExportRevenueReportAction() {
        super(EXPORT_REVENUE_REPORT);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Ui.exec(
                Ui.choiceFx(RevenueReportSelectorPane.class)
                .onOk(p -> lookup(RevenueReporter.class)
                        .toXls(p.getStart(), p.getEnd(), p.getStep(), p.isExtraReported())
                        .toTemporaryFile())
                .osOpen()
        );
    }
}
