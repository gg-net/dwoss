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
package eu.ggnet.dwoss.receipt.unit;

import java.awt.Window;
import java.awt.event.ActionEvent;

import eu.ggnet.saft.core.Workspace;
import eu.ggnet.saft.core.authorisation.Guardian;
import eu.ggnet.saft.core.authorisation.AccessableAction;

import eu.ggnet.dwoss.uniqueunit.op.AddUnitHistory;

import eu.ggnet.dwoss.util.OkCancelDialog;

import static eu.ggnet.saft.core.Client.lookup;
import static eu.ggnet.dwoss.rights.api.AtomicRight.CREATE_COMMENT_UNIQUE_UNIT_HISTORY;

/**
 *
 * @author bastian.venz
 */
public class AddCommentAction extends AccessableAction {

    public AddCommentAction() {
        super(CREATE_COMMENT_UNIQUE_UNIT_HISTORY);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Window mainFrame = lookup(Workspace.class).getMainFrame();
        OkCancelDialog<AddCommentCask> okCancelDialog = new OkCancelDialog<>(mainFrame, "Füge eine Unit Kommentar hinzu", new AddCommentCask());
        okCancelDialog.setVisible(true);
        if ( okCancelDialog.isCancel() ) return;
        String comment = okCancelDialog.getSubContainer().getComment();
        String refurbishId = okCancelDialog.getSubContainer().getRefurbishId();
        String username = lookup(Guardian.class).getUsername();
        lookup(AddUnitHistory.class).addCommentHistory(refurbishId, comment, username);
    }

}
