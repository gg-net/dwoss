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

import eu.ggnet.dwoss.report.ee.ReportAgent;
import eu.ggnet.dwoss.report.ee.entity.Report;
import eu.ggnet.dwoss.report.ui.cap.support.SelectExistingReportView;
import eu.ggnet.dwoss.report.ui.main.ReportController;
import eu.ggnet.dwoss.report.ui.main.ReportController.In;
import eu.ggnet.saft.core.Dl;
import eu.ggnet.saft.core.Ui;
import eu.ggnet.dwoss.common.ui.AccessableAction;

import static eu.ggnet.dwoss.rights.api.AtomicRight.READ_STORED_REPORTS;

/**
 * Opens a view of all reports to select a stored report for inspection.
 *
 * @author pascal.perau, oliver.guenther
 */
public class SelectExistingReportAction extends AccessableAction {

    public SelectExistingReportAction() {
        super(READ_STORED_REPORTS);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Ui.exec(() -> {
            Ui.build().fx().eval(() -> Dl.remote().lookup(ReportAgent.class).findAll(Report.class), () -> new SelectExistingReportView()).opt()
                    .ifPresent(id -> Ui.build().fxml().show(() -> new In(Ui.progress().call(() -> Dl.remote().lookup(ReportAgent.class).findReportResult(id)), true), ReportController.class));
        });
    }
}
