package eu.ggnet.dwoss.redtape.tryout;

import java.awt.Component;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;

import eu.ggnet.dwoss.core.common.UserInfoException;
import eu.ggnet.dwoss.core.common.UserInfoException.Type;
import eu.ggnet.dwoss.redtape.ee.interactiveresult.InteractionListener;
import eu.ggnet.dwoss.redtape.ee.interactiveresult.InteractionListener.TrueFalseCancel;

import static eu.ggnet.dwoss.core.common.UserInfoException.Type.*;
import static eu.ggnet.dwoss.redtape.ee.interactiveresult.InteractionListener.TrueFalseCancel.CANCEL;

/**
 * Swing Implementation of the InteractionListener
 * <p>
 * @author oliver.guenther
 */
public class SwingInteraction implements InteractionListener {

    private final static Map<UserInfoException.Type, Integer> mapping;

    static {
        Map<UserInfoException.Type, Integer> t = new HashMap<>();
        t.put(INFO, JOptionPane.INFORMATION_MESSAGE);
        t.put(WARNING, JOptionPane.WARNING_MESSAGE);
        t.put(ERROR, JOptionPane.ERROR_MESSAGE);
        mapping = t;
    }

    /**
     * Retuns the JOptionPane value for the UserInfoException Type.
     * <p>
     * @param type the type
     * @return
     */
    public static int toOptionPane(UserInfoException.Type type) {
        return mapping.get(type);
    }

    private final Component parent;

    public SwingInteraction(Component parent) {
        this.parent = parent;
    }

    public SwingInteraction() {
        this(null);
    }

    @Override
    public void notify(String head, String message, Type type) {
        JOptionPane.showMessageDialog(parent, message, head, toOptionPane(type));
    }

    @Override
    public boolean interactYesNo(String head, String message, Type type) {
        return JOptionPane.showConfirmDialog(parent, message, head, JOptionPane.YES_NO_OPTION, toOptionPane(type)) == JOptionPane.YES_OPTION;
    }

    @Override
    public boolean interactOkCancel(String head, String message, Type type) {
        return JOptionPane.showConfirmDialog(parent, message, head, JOptionPane.OK_CANCEL_OPTION, toOptionPane(type)) == JOptionPane.OK_OPTION;
    }

    @Override
    public TrueFalseCancel interactYesNoCancel(String head, String message, Type type) {
        int result = JOptionPane.showConfirmDialog(parent, message, head, JOptionPane.YES_NO_CANCEL_OPTION, toOptionPane(type));
        if ( result == JOptionPane.YES_OPTION ) return TrueFalseCancel.TRUE;
        if ( result == JOptionPane.NO_OPTION ) return TrueFalseCancel.FALSE;
        return CANCEL;
    }

}
