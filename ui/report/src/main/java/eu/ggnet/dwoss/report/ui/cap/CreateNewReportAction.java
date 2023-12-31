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

import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.core.widget.*;
import eu.ggnet.dwoss.core.widget.saft.OkCancelWrap;
import eu.ggnet.dwoss.report.ee.ReportAgent;
import eu.ggnet.dwoss.report.ui.cap.support.CreateNewReportView;
import eu.ggnet.dwoss.report.ui.main.ReportController;
import eu.ggnet.dwoss.report.ui.main.ReportController.In;
import eu.ggnet.saft.core.Ui;

import jakarta.enterprise.context.Dependent;

import static eu.ggnet.dwoss.rights.api.AtomicRight.CREATE_SALES_REPORT;

/**
 * Create a new sales report.
 *
 * @author pascal.perau
 */
@Dependent
public class CreateNewReportAction extends AccessableAction {

    public CreateNewReportAction() {
        super(CREATE_SALES_REPORT);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Ui.exec(() -> {
            Ui.build().swing().eval(() -> OkCancelWrap.vetoResult(new CreateNewReportView()))
                    .opt()
                    .ifPresent(v -> {
                        LoggerFactory.getLogger(CreateNewReportAction.class).info("{}", v.getParameter());
                        Ui.build().fxml().show(() -> Progressor.global().run(() -> new In(Dl.remote().lookup(ReportAgent.class).prepareReport(v.getParameter(), v.loadUnreported()), false)), ReportController.class);
                    });
        });
    }

}
