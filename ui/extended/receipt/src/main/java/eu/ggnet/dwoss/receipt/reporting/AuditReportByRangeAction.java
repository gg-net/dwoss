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

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import eu.ggnet.dwoss.util.DateRangeChooserDialog;
import eu.ggnet.saft.Ui;
import eu.ggnet.saft.core.ui.Workspace;

import static eu.ggnet.saft.Client.lookup;

/**
 *
 * @author oliver.guenther
 */
public class AuditReportByRangeAction extends AbstractAction {

    public AuditReportByRangeAction() {
        super("Audit Report nach Datum erzeugen");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final DateRangeChooserDialog dialog = new DateRangeChooserDialog(lookup(Workspace.class).getMainFrame());
        dialog.setVisible(true);
        if ( !dialog.isOk() ) return;
        Ui.exec(() -> {
            Ui.osOpen(Ui.progress().title("Auditreport").call(() -> lookup(AuditReporter.class).byRange(dialog.getStart(), dialog.getEnd()).toTemporaryFile()));
        });
    }
}
