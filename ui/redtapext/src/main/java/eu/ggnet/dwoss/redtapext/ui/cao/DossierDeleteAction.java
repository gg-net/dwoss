/*
 * Copyright (C) 2014 GG-Net GmbH - Oliver Günther
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.ggnet.dwoss.redtapext.ui.cao;

import java.awt.Window;
import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.JOptionPane;

import eu.ggnet.dwoss.redtape.ee.entity.Dossier;
import eu.ggnet.dwoss.redtapext.ee.RedTapeWorker;
import eu.ggnet.saft.core.Dl;
import eu.ggnet.dwoss.core.widget.AccessableAction;

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
        Dl.remote().lookup(RedTapeWorker.class).delete(dos);
        controller.reloadOnDelete(dos);
    }
}
