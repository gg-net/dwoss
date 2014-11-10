package eu.ggnet.dwoss.misc.help;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import eu.ggnet.saft.core.Workspace;

import static eu.ggnet.saft.core.Client.lookup;

/**
 *
 * @author pascal.perau
 */
public class AboutAction extends AbstractAction {

    public AboutAction() {
        super("Über ...");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        AboutDialog about = new AboutDialog(lookup(Workspace.class).getMainFrame());
        about.setVisible(true);
    }
}
