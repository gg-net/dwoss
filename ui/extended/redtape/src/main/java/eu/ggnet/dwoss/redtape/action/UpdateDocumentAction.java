package eu.ggnet.dwoss.redtape.action;

import eu.ggnet.dwoss.util.CloseType;
import eu.ggnet.dwoss.util.OkCancelDialog;
import eu.ggnet.dwoss.common.ExceptionUtil;

import java.awt.Dialog;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.saft.core.Workspace;
import eu.ggnet.saft.core.authorisation.Guardian;

import eu.ggnet.dwoss.redtape.entity.Document;

import eu.ggnet.dwoss.redtape.RedTapeWorker;

import eu.ggnet.dwoss.redtape.RedTapeController;
import eu.ggnet.dwoss.redtape.document.DocumentUpdateController;
import eu.ggnet.dwoss.redtape.document.DocumentUpdateView;

import eu.ggnet.dwoss.util.UserInfoException;

import lombok.AllArgsConstructor;
import lombok.Data;

import static eu.ggnet.saft.core.Client.lookup;

/**
 *
 * @author pascal.perau
 */
@AllArgsConstructor
public class UpdateDocumentAction extends AbstractAction {

    private static final Logger L = LoggerFactory.getLogger(UpdateDocumentAction.class.getName());

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
            JOptionPane.showMessageDialog(parent, "Sopo Aufträge können nicht bearbeitet werden.");
            return;
        }
        DocumentUpdateView view = new DocumentUpdateView(doc);
        OkCancelDialog<DocumentUpdateView> dialog = new OkCancelDialog<>(parent, Dialog.ModalityType.DOCUMENT_MODAL, "Dokument bearbeiten", view);
        DocumentUpdateController controller = new DocumentUpdateController(view, doc);
        view.setController(controller);
        view.setCustomerValues(id);

        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
        if ( dialog.getCloseType() == CloseType.OK ) {
            doc = lookup(RedTapeWorker.class).update(view.getDocument(), null, lookup(Guardian.class).getUsername());
        } else {
            try {
                doc = lookup(RedTapeWorker.class).revertCreate(doc);
            } catch (UserInfoException ex) {
                ExceptionUtil.show(parent, ex);
            }
        }
        redTapeController.reloadSelectionOnStateChange(doc.getDossier());
    }
}
