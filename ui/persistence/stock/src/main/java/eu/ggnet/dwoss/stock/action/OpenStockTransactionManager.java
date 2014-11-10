package eu.ggnet.dwoss.stock.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import eu.ggnet.saft.core.Workspace;

import eu.ggnet.dwoss.stock.StockTransactionManagerController;
import eu.ggnet.dwoss.stock.StockTransactionManagerModel;
import eu.ggnet.dwoss.stock.StockTransactionManagerView;

import static eu.ggnet.saft.core.Client.lookup;

/**
 *
 * @author oliver.guenther
 */
public class OpenStockTransactionManager extends AbstractAction {

    public OpenStockTransactionManager() {
        super("Transaktionsmanager");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        StockTransactionManagerView view = new StockTransactionManagerView(lookup(Workspace.class).getMainFrame());
        StockTransactionManagerModel model = new StockTransactionManagerModel();
        StockTransactionManagerController controller = new StockTransactionManagerController();
        view.setModel(model);
        view.setController(controller);
        controller.setModel(model);
        controller.setView(view);
        controller.reload();
        view.setVisible(true);
    }
}
