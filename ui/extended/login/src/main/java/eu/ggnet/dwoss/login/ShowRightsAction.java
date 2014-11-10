package eu.ggnet.dwoss.login;

import java.awt.Dialog;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import eu.ggnet.saft.api.Authorisation;

import org.openide.util.Lookup;

import eu.ggnet.saft.core.Workspace;
import eu.ggnet.saft.core.authorisation.Guardian;

import eu.ggnet.dwoss.util.HtmlDialog;

import static eu.ggnet.saft.core.Client.lookup;

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
