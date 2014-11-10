package eu.ggnet.dwoss.redtape.reporting;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import eu.ggnet.dwoss.common.DesktopUtil;
import eu.ggnet.saft.core.Workspace;

import eu.ggnet.dwoss.redtape.reporting.CreditMemoReporter;

import eu.ggnet.dwoss.util.DateRangeChooserDialog;

import static eu.ggnet.saft.core.Client.lookup;

/**
 * Action to create the CreditMemo Report.
 *
 * @author pascal.perau
 */
public class CreditMemoReportAction extends AbstractAction {

    public CreditMemoReportAction() {
        putValue(NAME, "Stornoreport lang");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        DateRangeChooserDialog dialog = new DateRangeChooserDialog(lookup(Workspace.class).getMainFrame());
        dialog.setVisible(true);
        if ( dialog.isOk() ) {
            DesktopUtil.open(lookup(CreditMemoReporter.class).toXls(dialog.getStart(), dialog.getEnd()).toTemporaryFile());
        }
    }
}
