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
package eu.ggnet.dwoss.receipt.reporting;

import eu.ggnet.dwoss.receipt.ee.reporting.AuditReporter;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import eu.ggnet.saft.Dl;
import eu.ggnet.saft.Ui;

/**
 *
 * @author oliver.guenther
 */
public class AuditReportOnRollInAction extends AbstractAction {

    public AuditReportOnRollInAction() {
        super("Audit Report für Geräte nach Aufnahme");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Ui.exec(() -> {
            Ui.osOpen(Ui.progress().title("Auditreport").call(() -> Dl.remote().lookup(AuditReporter.class).onRollIn().toTemporaryFile()));
        });
    }
}
