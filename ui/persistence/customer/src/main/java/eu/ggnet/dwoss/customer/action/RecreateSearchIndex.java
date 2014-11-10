package eu.ggnet.dwoss.customer.action;

import java.awt.event.ActionEvent;
import java.util.concurrent.ExecutionException;

import javax.swing.*;

import eu.ggnet.saft.core.Workspace;


import eu.ggnet.dwoss.common.ExceptionUtil;
import eu.ggnet.dwoss.customer.priv.SearchSingleton;

import static eu.ggnet.saft.core.Client.lookup;

/**
 *
 * @author pascal.perau
 */
public class RecreateSearchIndex extends AbstractAction {

    public RecreateSearchIndex() {
        super("Customer Suchindex neu erzeugen.");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                lookup(SearchSingleton.class).reindexSearch();
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    JOptionPane.showMessageDialog(lookup(Workspace.class).getMainFrame(), "Suchindex neu erzeugt.");
                } catch (InterruptedException | ExecutionException ex) {
                    ExceptionUtil.show(lookup(Workspace.class).getMainFrame(), ex);
                }
            }
        }.execute();
    }
}
