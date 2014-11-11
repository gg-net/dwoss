/* 
 * Copyright (C) 2014 pascal.perau
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
package eu.ggnet.dwoss.redtape.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import eu.ggnet.dwoss.common.DesktopUtil;
import eu.ggnet.saft.core.Workspace;

import eu.ggnet.dwoss.redtape.reporting.CreditMemoReporter;

import eu.ggnet.dwoss.util.DateRangeChooserDialog;

import static eu.ggnet.saft.core.Client.lookup;
import static javax.swing.Action.NAME;

public class OptimizedCreditMemoReportAction extends AbstractAction {

    public OptimizedCreditMemoReportAction() {
        putValue(NAME, "Stornoreport gekürzt");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        DateRangeChooserDialog dialog = new DateRangeChooserDialog(lookup(Workspace.class).getMainFrame());
        dialog.setVisible(true);
        if ( dialog.isOk() ) {
            DesktopUtil.open(lookup(CreditMemoReporter.class).toOptimizedXls(dialog.getStart(), dialog.getEnd()).toTemporaryFile());
        }
    }
}
