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
package eu.ggnet.dwoss.receipt.ui.cap;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import eu.ggnet.dwoss.core.widget.*;
import eu.ggnet.dwoss.receipt.ee.reporting.AuditReporter;
import eu.ggnet.saft.core.Ui;

import jakarta.enterprise.context.Dependent;

/**
 *
 * @author oliver.guenther
 */
@Dependent
public class AuditReportByRangeAction extends AbstractAction {

    public AuditReportByRangeAction() {
        super("Audit Report nach Datum erzeugen");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Ui.exec(() -> {
            Ui.build().title("Audit Report nach Datum").fx().eval(() -> new DateRangeChooserView()).opt().ifPresent(r -> {
                FileUtil.osOpen(Progressor.global().run("Auditreporter", () -> Dl.remote().lookup(AuditReporter.class).byRange(r.startAsDate(), r.endAsDate()).toTemporaryFile()));
            });
        });
    }
}
