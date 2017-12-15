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

import javax.swing.AbstractAction;
import javax.swing.Action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.redtape.RedTapeWorker;
import eu.ggnet.dwoss.redtape.document.DocumentUpdateController;
import eu.ggnet.dwoss.redtape.document.DocumentUpdateView;
import eu.ggnet.dwoss.redtape.entity.Document;
import eu.ggnet.dwoss.util.UserInfoException;
import eu.ggnet.saft.Ui;
import eu.ggnet.saft.api.Reply;
import eu.ggnet.saft.core.Alert;
import eu.ggnet.saft.core.authorisation.Guardian;
import eu.ggnet.saft.core.swing.OkCancel;

import lombok.AllArgsConstructor;

import static eu.ggnet.saft.core.Client.lookup;
import static eu.ggnet.saft.core.UiAlert.Type.ERROR;

/**
 *
 * @author pascal.perau
 */
@AllArgsConstructor
public class DossierUpdateAction extends AbstractAction {

    private static final Logger L = LoggerFactory.getLogger(DossierUpdateAction.class.getName());

    private final Window parent;

    private final RedTapeController redTapeController;

    private final long id;

    private Document doc;

    {
        putValue(Action.NAME, "Dokument bearbeiten");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if ( doc.getDossier().getId() == 0 ) {
            Alert.show(parent, "Fehler", "Sopo Aufträge können nicht bearbeitet werden.", ERROR);
            return;
        }

        Ui.exec(() -> {
            Ui.swing().parent(parent).eval(() -> {
                DocumentUpdateView view = new DocumentUpdateView(doc);
                DocumentUpdateController controller = new DocumentUpdateController(view, doc);
                view.setController(controller);
                view.setCustomerValues(id);
                return OkCancel.wrap(view);
            })
                    .filter(this::handleFailure)
                    .map(Reply::getPayload)
                    .ifPresent(this::handleSuccess);
        });

    }

    private void handleSuccess(Document doc) {
        Document result = lookup(RedTapeWorker.class).update(doc, null, lookup(Guardian.class).getUsername());
        redTapeController.reloadSelectionOnStateChange(result.getDossier());
    }

    private boolean handleFailure(Reply<Document> reply) {
        if ( reply.hasSucceded() ) return true;
        try {
            doc = lookup(RedTapeWorker.class).revertCreate(doc);
            redTapeController.reloadSelectionOnStateChange(doc.getDossier());
        } catch (UserInfoException ex) {
            Ui.handle(ex);
        }
        return false;
    }

}
