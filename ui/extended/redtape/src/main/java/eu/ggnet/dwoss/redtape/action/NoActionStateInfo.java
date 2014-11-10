package eu.ggnet.dwoss.redtape.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;


/**
 * Util Action, should be used in the Ui if no StateAction is available.
 *
 * @author pascal.perau
 */
public class NoActionStateInfo extends AbstractAction {

    private String message;

    public NoActionStateInfo(String message) {
        super("No Transition, Info");
        this.message = message;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JOptionPane.showMessageDialog(null,"The following Characteristic is either in an EndState or does not exist at all:\n" + message);
    }
}
