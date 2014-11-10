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
