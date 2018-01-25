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
import eu.ggnet.dwoss.report.ui.returns.ReturnsReportView;
import eu.ggnet.dwoss.util.DateRangeChooserView;
import eu.ggnet.saft.Ui;
import eu.ggnet.saft.core.auth.AccessableAction;

import static eu.ggnet.dwoss.rights.api.AtomicRight.CREATE_RETUNRS_REPORT;
import static eu.ggnet.dwoss.rules.DocumentType.RETURNS;
import static eu.ggnet.saft.Client.lookup;

/**
 * Opens the Returns report view to select lines to be exported.
 *
 * @author oliver.guenther
 */
public class CreateReturnsReportAction extends AccessableAction {

    public CreateReturnsReportAction() {
        super(CREATE_RETUNRS_REPORT);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        Ui.exec(() -> {
            Ui.build().fx().eval(() -> new DateRangeChooserView())
                    .ifPresent(r -> {
                        Ui.build().swing().show(() -> lookup(ReportAgent.class).findReportLinesByDocumentType(
                                RETURNS, r.getStartAsDate(), r.getEndAsDate()), () -> new ReturnsReportView());
                    });
        });
    }

}
