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
package eu.ggnet.dwoss.redtapext.ui.cao.document;

import java.awt.Component;

import javax.swing.JOptionPane;

import eu.ggnet.dwoss.util.UserInfoException;
import eu.ggnet.dwoss.util.UserInfoException.Type;
import eu.ggnet.dwoss.util.interactiveresult.InteractionListener;

import static eu.ggnet.dwoss.util.UserInfoException.Type.*;
import static eu.ggnet.dwoss.util.interactiveresult.InteractionListener.TrueFalseCancel.CANCEL;

/**
 * Swing Implementation of the InteractionListener
 * <p>
 * @author oliver.guenther
 */
public class SwingInteraction implements InteractionListener {

    /**
     * Retuns the JOptionPane value for the UserInfoException Type.
     * <p>
     * @param type the type
     * @return JOptionPane Type.
     */
    private int toOptionPane(UserInfoException.Type type) {
        switch (type) {
            case WARNING:
                return JOptionPane.WARNING_MESSAGE;
            case ERROR:
                return JOptionPane.ERROR_MESSAGE;
            case INFO:
            default:
                return JOptionPane.INFORMATION_MESSAGE;
        }
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
