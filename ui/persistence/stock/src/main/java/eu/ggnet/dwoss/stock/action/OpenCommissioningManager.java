package eu.ggnet.dwoss.stock.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import eu.ggnet.saft.core.Workspace;

import eu.ggnet.dwoss.stock.CommissioningManagerController;
import eu.ggnet.dwoss.stock.CommissioningManagerModel;
import eu.ggnet.dwoss.stock.CommissioningManagerView;

import static eu.ggnet.saft.core.Client.lookup;

/**
 *
 * @author oliver.guenther
 */
public class OpenCommissioningManager extends AbstractAction {

    public OpenCommissioningManager() {
        super("Kommissionsmanager");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        CommissioningManagerModel model = new CommissioningManagerModel();
        CommissioningManagerView view = new CommissioningManagerView(lookup(Workspace.class).getMainFrame());
        CommissioningManagerController controller = new CommissioningManagerController();
        view.setModel(model);
        view.setController(controller);
        controller.setModel(model);
        controller.setView(view);
        view.setVisible(true);
    }
}
