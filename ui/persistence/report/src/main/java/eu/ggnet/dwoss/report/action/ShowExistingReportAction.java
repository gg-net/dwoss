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

import eu.ggnet.dwoss.report.*;
import eu.ggnet.dwoss.report.ReportController.In;
import eu.ggnet.dwoss.report.entity.Report;
import eu.ggnet.saft.core.Ui;
import eu.ggnet.saft.core.authorisation.AccessableAction;

import static eu.ggnet.dwoss.rights.api.AtomicRight.READ_STORED_REPORTS;
import static eu.ggnet.saft.core.Client.lookup;

/**
 *
 * @author pascal.perau
 */
public class ShowExistingReportAction extends AccessableAction {

    public ShowExistingReportAction() {
        super(READ_STORED_REPORTS);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Ui.call(() -> lookup(ReportAgent.class).findAll(Report.class))
                .choiceFx(ReportSelectionPane.class)
                .onOk(r -> new In(lookup(ReportAgent.class).findReportResult(r.selected().getId()), true))
                .openFxml(ReportController.class)
                .exec();

//        Platform.runLater(() -> {
//            ReportSelectionPane stage = new ReportSelectionPane();
//            stage.show();
//        });
    }
}
