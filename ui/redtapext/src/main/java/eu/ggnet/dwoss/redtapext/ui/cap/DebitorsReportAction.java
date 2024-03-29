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
package eu.ggnet.dwoss.redtapext.ui.cap;

import java.awt.event.ActionEvent;

import eu.ggnet.dwoss.core.widget.*;
import eu.ggnet.dwoss.redtapext.ee.reporting.DebitorsReporter;
import eu.ggnet.saft.core.Ui;

import jakarta.enterprise.context.Dependent;

import static eu.ggnet.dwoss.rights.api.AtomicRight.CREATE_DEBITOR_REPORT;

/**
 *
 * @author pascal.perau
 */
@Dependent
public class DebitorsReportAction extends AccessableAction {

    public DebitorsReportAction() {
        super(CREATE_DEBITOR_REPORT);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Ui.exec(() -> {
            Ui.build().fx().eval(() -> new DateRangeChooserView()).opt().ifPresent(r -> {
                FileUtil.osOpen(Progressor.global().run("Debitorenreport", () -> Dl.remote().lookup(DebitorsReporter.class).toXls(r.startAsDate(), r.endAsDate()).toTemporaryFile()));
            });
        });
    }
}
