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

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.util.List;

import eu.ggnet.saft.core.Workspace;
import eu.ggnet.saft.core.authorisation.AccessableAction;

import eu.ggnet.dwoss.report.ReportAgent;
import eu.ggnet.dwoss.report.entity.ReportLine;

import eu.ggnet.dwoss.report.returns.ReturnsCask;

import eu.ggnet.dwoss.util.DateRangeChooserDialog;
import eu.ggnet.dwoss.util.OkCancelDialog;

import static eu.ggnet.saft.core.Client.lookup;
import static eu.ggnet.dwoss.rights.api.AtomicRight.CREATE_RETUNRS_REPORT;
import static eu.ggnet.dwoss.rules.DocumentType.RETURNS;

public class CreateReturnsReportAction extends AccessableAction {

    public CreateReturnsReportAction() {
        super(CREATE_RETUNRS_REPORT);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        Window mainFrame = lookup(Workspace.class).getMainFrame();
        DateRangeChooserDialog chooserDialog = new DateRangeChooserDialog(mainFrame);
        chooserDialog.setVisible(true);
        if ( !chooserDialog.isOk() ) return;
        List<ReportLine> findReportLinesByCustomer = lookup(ReportAgent.class).findReportLinesByDocumentType(
                RETURNS, chooserDialog.getStart(), chooserDialog.getEnd());
        ReturnsCask cask = new ReturnsCask(findReportLinesByCustomer);
        OkCancelDialog<ReturnsCask> ocd = new OkCancelDialog<>(mainFrame, NAME, cask);
        ocd.setVisible(true);
    }

}
