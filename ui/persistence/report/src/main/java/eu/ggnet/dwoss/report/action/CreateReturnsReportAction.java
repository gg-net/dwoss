package eu.ggnet.dwoss.report.action;

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
