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

import eu.ggnet.dwoss.redtapext.ee.reporting.DebitorsReporter;

import java.awt.event.ActionEvent;

import eu.ggnet.dwoss.util.DateRangeChooserDialog;
import eu.ggnet.saft.Ui;
import eu.ggnet.saft.core.ui.Workspace;
import eu.ggnet.saft.core.auth.AccessableAction;

import static eu.ggnet.dwoss.rights.api.AtomicRight.CREATE_DEBITOR_REPORT;
import static eu.ggnet.saft.Client.lookup;

/**
 *
 * @author pascal.perau
 */
public class DebitorsReportAction extends AccessableAction {

    public DebitorsReportAction() {
        super(CREATE_DEBITOR_REPORT);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final DateRangeChooserDialog question = new DateRangeChooserDialog(lookup(Workspace.class).getMainFrame());
        question.setTitle("Reportzeitraum für Debitoren");
        question.setVisible(true);
        if ( !question.isOk() ) return;
        Ui.exec(() -> {
            Ui.osOpen(Ui.progress().title("Debitorenreport").call(() -> lookup(DebitorsReporter.class).toXls(question.getStart(), question.getEnd()).toTemporaryFile()));
        });
    }
}
