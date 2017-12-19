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
package eu.ggnet.dwoss.login;

import java.awt.Dialog;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import eu.ggnet.saft.api.auth.Authorisation;

import org.openide.util.Lookup;

import eu.ggnet.saft.core.ui.Workspace;
import eu.ggnet.saft.core.auth.Guardian;

import eu.ggnet.dwoss.util.HtmlDialog;

import static eu.ggnet.saft.Client.lookup;

/**
 * An Action, that opens a HtmlDialog to show all active Rights.
 *
 * @author oliver.guenther
 */
public class ShowRightsAction extends AbstractAction {

    public ShowRightsAction() {
        super("Berechtigungen anzeigen");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        HtmlDialog dialog = new HtmlDialog(lookup(Workspace.class).getMainFrame(), Dialog.ModalityType.MODELESS);
        StringBuilder sb = new StringBuilder();
        Guardian accessCos = Lookup.getDefault().lookup(Guardian.class);
        sb.append("<html><body><u>Benutzer:</u> <b>").append(accessCos.getUsername()).append("</b><br /><u>Berechtigungen</u><ul>");
        for (Authorisation authorisation : accessCos.getRights()) {
            sb.append("<li>").append(authorisation.toName()).append("</li>");
        }

        sb.append("</ul></body></html>");
        dialog.setText(sb.toString());
        dialog.setVisible(true);
    }

}
