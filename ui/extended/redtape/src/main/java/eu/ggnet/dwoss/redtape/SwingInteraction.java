package eu.ggnet.dwoss.redtape;

import java.awt.Component;

import javax.swing.JOptionPane;

import eu.ggnet.dwoss.common.DesktopUtil;
import eu.ggnet.dwoss.util.UserInfoException.Type;

import eu.ggnet.dwoss.util.interactiveresult.InteractionListener;

import static eu.ggnet.dwoss.util.interactiveresult.InteractionListener.TrueFalseCancel.CANCEL;

/**
 * Swing Implementation of the InteractionListener
 * <p>
 * @author oliver.guenther
 */
public class SwingInteraction implements InteractionListener {

    private final Component parent;

    public SwingInteraction(Component parent) {
        this.parent = parent;
    }

    public SwingInteraction() {
        this(null);
    }

    @Override
    public void notify(String head, String message, Type type) {
        JOptionPane.showMessageDialog(parent, message, head, DesktopUtil.toOptionPane(type));
    }

    @Override
    public boolean interactYesNo(String head, String message, Type type) {
        return JOptionPane.showConfirmDialog(parent, message, head, JOptionPane.YES_NO_OPTION, DesktopUtil.toOptionPane(type)) == JOptionPane.YES_OPTION;
    }

    @Override
    public boolean interactOkCancel(String head, String message, Type type) {
        return JOptionPane.showConfirmDialog(parent, message, head, JOptionPane.OK_CANCEL_OPTION, DesktopUtil.toOptionPane(type)) == JOptionPane.OK_OPTION;
    }

    @Override
    public TrueFalseCancel interactYesNoCancel(String head, String message, Type type) {
        int result = JOptionPane.showConfirmDialog(parent, message, head, JOptionPane.YES_NO_CANCEL_OPTION, DesktopUtil.toOptionPane(type));
        if ( result == JOptionPane.YES_OPTION ) return TrueFalseCancel.TRUE;
        if ( result == JOptionPane.NO_OPTION ) return TrueFalseCancel.FALSE;
        return CANCEL;
    }

}
