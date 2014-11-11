/* 
 * Copyright (C) 2014 pascal.perau
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
