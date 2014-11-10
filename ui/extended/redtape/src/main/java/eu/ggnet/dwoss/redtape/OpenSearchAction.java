package eu.ggnet.dwoss.redtape;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

/**
 *
 * @author pascal.perau
 */
public class OpenSearchAction extends AbstractAction {

    public OpenSearchAction() {
        super("Universelle Suche");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        UniversalSearchViewCask.showSingleInstance();
    }
}
