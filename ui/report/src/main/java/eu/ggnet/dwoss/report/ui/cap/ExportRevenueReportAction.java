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

import java.awt.event.ActionEvent;

import eu.ggnet.dwoss.report.ee.op.RevenueReporter;
import eu.ggnet.dwoss.report.ui.cap.support.RevenueReportSelectionView;
import eu.ggnet.dwoss.core.widget.Dl;
import eu.ggnet.saft.core.Ui;
import eu.ggnet.dwoss.core.widget.AccessableAction;

import static eu.ggnet.dwoss.rights.api.AtomicRight.EXPORT_REVENUE_REPORT;

/**
 * Opens the revenue report selector.
 *
 * @author pascal.perau
 */
public class ExportRevenueReportAction extends AccessableAction {

    public ExportRevenueReportAction() {
        super(EXPORT_REVENUE_REPORT);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Ui.exec(() -> {
            Ui.build().fx().eval(() -> new RevenueReportSelectionView()).opt()
                    .ifPresent(v -> Ui.osOpen(Ui.progress().call(() -> Dl.remote().lookup(RevenueReporter.class).toXls(v.getStart(), v.getEnd(), v.getStep(), v.isExtraReported()).toTemporaryFile())));
        });
    }
}
