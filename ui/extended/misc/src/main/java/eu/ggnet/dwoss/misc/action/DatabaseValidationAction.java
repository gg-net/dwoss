package eu.ggnet.dwoss.misc.action;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.concurrent.ExecutionException;

import javax.swing.AbstractAction;
import javax.swing.SwingWorker;

import eu.ggnet.saft.core.Workspace;

import eu.ggnet.dwoss.misc.op.PersistenceValidator;

import eu.ggnet.dwoss.util.FileJacket;

import eu.ggnet.dwoss.common.ExceptionUtil;

import static eu.ggnet.saft.core.Client.lookup;

public class DatabaseValidationAction extends AbstractAction {

    public DatabaseValidationAction() {
        super("Datenbank Validieren");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        new SwingWorker<Object, Object>() {
            @Override
            protected Object doInBackground() throws Exception {
                FileJacket jacket = lookup(PersistenceValidator.class).validateDatabase();
                if ( jacket == null ) return null;
                File f = jacket.toTemporaryFile();
                Desktop.getDesktop().open(f);
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                } catch (InterruptedException | ExecutionException ex) {
                    ExceptionUtil.show(lookup(Workspace.class).getMainFrame(), ex);
                }
            }
        }.execute();
    }
}
