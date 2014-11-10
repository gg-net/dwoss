package eu.ggnet.dwoss.receipt.product;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import eu.ggnet.saft.core.Workspace;

import eu.ggnet.dwoss.util.OkCancelDialog;

import static eu.ggnet.saft.core.Client.lookup;

public class CpuManagementAction extends AbstractAction {

    public CpuManagementAction() {
        super("Manage CPUs");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        OkCancelDialog<CpuListPanel> panel = new OkCancelDialog<>(lookup(Workspace.class).getMainFrame()
                , "Liste aller Cpus", new CpuListPanel(new CpuListController()));
        panel.setVisible(true);
    }
}
