package eu.ggnet.dwoss.misc.action.movement;

import java.awt.event.ActionEvent;
import java.util.concurrent.ExecutionException;

import javax.swing.*;

import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;

import eu.ggnet.saft.core.Workspace;

import eu.ggnet.dwoss.misc.op.movement.MovementListingProducer;

import eu.ggnet.dwoss.stock.entity.Stock;
import eu.ggnet.dwoss.common.ExceptionUtil;

import static eu.ggnet.saft.core.Client.lookup;

/**
 *
 * @author oliver.guenther
 */
public class MovementAction extends AbstractAction {

    private final Stock stock;

    private final MovementListingProducer.ListType listType;

    public MovementAction(MovementListingProducer.ListType listType, Stock stock) {
        super(listType.getName() + " - " + stock.getName());
        this.stock = stock;
        this.listType = listType;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        new SwingWorker<JasperPrint, Object>() {
            @Override
            protected JasperPrint doInBackground() throws Exception {
                return lookup(MovementListingProducer.class).generateList(listType, stock);
            }

            @Override
            protected void done() {
                try {
                    JasperViewer viewer = new JasperViewer(get(), false);
                    viewer.setVisible(true);
                } catch (InterruptedException | ExecutionException ex) {
                    ExceptionUtil.show(lookup(Workspace.class).getMainFrame(), ex);
                }
            }
        }.execute();
    }
}
