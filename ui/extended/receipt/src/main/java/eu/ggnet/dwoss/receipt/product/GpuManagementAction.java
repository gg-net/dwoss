package eu.ggnet.dwoss.receipt.product;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import eu.ggnet.saft.core.Workspace;

import eu.ggnet.dwoss.util.OkCancelDialog;

import static eu.ggnet.saft.core.Client.lookup;

public class GpuManagementAction extends AbstractAction {

    public GpuManagementAction() {
        super("Manage GPUs");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        OkCancelDialog<GpuListPanel> panel = new OkCancelDialog<>(lookup(Workspace.class).getMainFrame(),
                "Liste aller Gpus", new GpuListPanel(new GpuListController()));
        panel.setVisible(true);
    }
}
