package eu.ggnet.dwoss.redtape.action;

import java.awt.Window;
import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.JOptionPane;

import eu.ggnet.dwoss.redtape.entity.Dossier;

import eu.ggnet.dwoss.redtape.RedTapeWorker;

import eu.ggnet.dwoss.redtape.RedTapeController;

import eu.ggnet.saft.core.authorisation.AccessableAction;

import static eu.ggnet.saft.core.Client.lookup;
import static eu.ggnet.dwoss.rights.api.AtomicRight.DELETE_DOSSIER;

/**
 *
 * @author pascal.perau
 */
public class DossierDeleteAction extends AccessableAction {

    private final Window parent;

    private final RedTapeController controller;

    private final Dossier dos;

    public DossierDeleteAction(Window parent, RedTapeController controller, Dossier dos) {
        super(DELETE_DOSSIER);
        this.parent = parent;
        this.controller = controller;
        this.dos = dos;
        putValue(Action.SHORT_DESCRIPTION, "Löscht einen Vorgang");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if ( JOptionPane.YES_OPTION != JOptionPane.showConfirmDialog(parent,
                "Möchten Sie den Vorgang " + dos.getIdentifier() + " wirklich löschen ?", "Vorgang löschen", JOptionPane.YES_NO_OPTION) ) return;
        lookup(RedTapeWorker.class).delete(dos);
        controller.reloadOnDelete(dos);
    }
}
