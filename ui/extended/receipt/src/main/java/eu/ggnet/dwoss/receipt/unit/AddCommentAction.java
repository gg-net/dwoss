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

import java.awt.event.ActionEvent;

import eu.ggnet.dwoss.uniqueunit.op.AddUnitHistory;
import eu.ggnet.dwoss.util.OkCancelDialog;
import eu.ggnet.saft.*;
import eu.ggnet.saft.core.auth.AccessableAction;
import eu.ggnet.saft.core.auth.Guardian;
import eu.ggnet.saft.core.swing.OkCancelWrap;

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
        Ui.exec(() -> {
            Ui.build().title("Füge eine Unit Kommentar hinzu")
                    .swing()
                    .eval(() -> OkCancelWrap.result(new AddCommentCask()))
                    .ifPresent(r -> Dl.remote().lookup(AddUnitHistory.class).addCommentHistory(r.getPayload().RefurbishId, r.getPayload().Comment, Dl.local().lookup(Guardian.class).getUsername()));
        });
    }

}
