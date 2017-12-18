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

import eu.ggnet.dwoss.report.ReportAgent;
import eu.ggnet.dwoss.report.ui.cap.support.CreateNewReportView;
import eu.ggnet.dwoss.report.ui.main.ReportController;
import eu.ggnet.dwoss.report.ui.main.ReportController.In;
import eu.ggnet.saft.Ui;
import eu.ggnet.saft.api.Reply;
import eu.ggnet.saft.core.authorisation.AccessableAction;
import eu.ggnet.saft.core.swing.OkCancel;

import static eu.ggnet.dwoss.rights.api.AtomicRight.CREATE_SALES_REPORT;
import static eu.ggnet.saft.core.Client.lookup;

/**
 * Create a new sales report.
 *
 * @author pascal.perau
 */
public class CreateNewReportAction extends AccessableAction {

    public CreateNewReportAction() {
        super(CREATE_SALES_REPORT);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Ui.exec(() -> {
            Ui.swing().eval(() -> OkCancel.wrap(new CreateNewReportView()))
                    .filter(Reply::hasSucceded)
                    .map(Reply::getPayload)
                    .ifPresent(v -> Ui.fxml().show(() -> Ui.progress().call(() -> new In(lookup(ReportAgent.class).prepareReport(v.getParameter(), v.loadUnreported()), false)), ReportController.class));
        });
    }

}
