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

import eu.ggnet.dwoss.core.widget.saft.OkCancelWrap;
import eu.ggnet.dwoss.redtape.ee.entity.Document;
import eu.ggnet.dwoss.redtapext.ee.RedTapeWorker;
import eu.ggnet.dwoss.redtapext.ui.cao.document.DocumentUpdateController;
import eu.ggnet.dwoss.redtapext.ui.cao.document.DocumentUpdateView;
import eu.ggnet.dwoss.core.common.UserInfoException;
import eu.ggnet.dwoss.core.widget.saft.Reply;
import eu.ggnet.dwoss.core.widget.Dl;
import eu.ggnet.saft.core.Ui;
import eu.ggnet.dwoss.core.widget.auth.Guardian;

import static eu.ggnet.saft.core.ui.AlertType.ERROR;

/**
 *
 * @author pascal.perau
 */
public class DossierUpdateAction extends AbstractAction {

    private static final Logger L = LoggerFactory.getLogger(DossierUpdateAction.class.getName());

    private final Window parent;

    private final RedTapeController redTapeController;

    private final long id;

    private Document doc;

    public DossierUpdateAction(Window parent, RedTapeController redTapeController, long id, Document doc) {
        this.parent = parent;
        this.redTapeController = redTapeController;
        this.id = id;
        this.doc = doc;
        putValue(Action.NAME, "Dokument bearbeiten");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if ( doc.getDossier().getId() == 0 ) {
            Ui.build(parent).title("Fehler").alert().message("Sopo Aufträge können nicht bearbeitet werden.").show(ERROR);
            return;
        }

        Ui.exec(() -> {
            Ui.build().parent(parent).swing().eval(() -> {
                DocumentUpdateView view = new DocumentUpdateView(doc);
                DocumentUpdateController controller = new DocumentUpdateController(view, doc);
                view.setController(controller);
                view.setCustomerValues(id);
                return OkCancelWrap.vetoResult(view);
            }).opt().filter(this::handleFailure).map(Reply::getPayload).ifPresent(this::handleSuccess);
        });

    }

    private void handleSuccess(Document doc) {
        Document result = Dl.remote().lookup(RedTapeWorker.class).update(doc, null, Dl.local().lookup(Guardian.class).getUsername());
        redTapeController.reloadSelectionOnStateChange(result.getDossier());
    }

    private boolean handleFailure(Reply<Document> reply) {
        if ( reply.hasSucceded() ) return true;
        try {
            doc = Dl.remote().lookup(RedTapeWorker.class).revertCreate(doc);
            redTapeController.reloadSelectionOnStateChange(doc.getDossier());
        } catch (UserInfoException ex) {
            Ui.handle(ex);
        }
        return false;
    }

}
