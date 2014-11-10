package eu.ggnet.dwoss.redtape.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import eu.ggnet.dwoss.redtape.dossier.DossierFilterView;

/**
 * @author bastian.venz
 * @author oliver.guenther
 * @author pascal.perau
 */
public class DossierFilterAction extends AbstractAction {

    public DossierFilterAction() {
        super("Aufträge nach Status");
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        DossierFilterView.showSingleInstance();
    }
}
