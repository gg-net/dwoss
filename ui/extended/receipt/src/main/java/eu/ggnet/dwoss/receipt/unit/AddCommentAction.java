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
