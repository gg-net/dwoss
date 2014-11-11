/* 
 * Copyright (C) 2014 pascal.perau
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
package eu.ggnet.dwoss.receipt;

import java.awt.Color;
import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.HashSet;

import javax.swing.JTable;
import javax.swing.KeyStroke;

/**
 *
 * @author pascal.perau
 */
public class UiUtil {

    /**
     * The focus goes to the next component with TAB.
     * This Method does NOT override the default Traversal Keyset. Ensure that
     * the used components don't have the KeyEvents i.e. as backward traversal keys.
     * @param component The focus goes to the next component with TAB
     */
    public static void forwardTab(final Component... component) {
        for (Component component1 : component) {
            component1.setFocusTraversalKeys(
                    KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,
                    new HashSet<KeyStroke>(Arrays.asList(KeyStroke.getKeyStroke("pressed TAB"))));
        }
    }

    /**
     * The focus goes back to the last component with CTRL + TAB.
     * This Method does NOT override the default Traversal Keyset. Ensure that
     * the used components don't have the KeyEvents i.e. as forward traversal keys.
     * @param component The focus goes back to the last component with CTRL + TAB
     */
    public static void backwardTab(final Component... component) {
        for (Component component1 : component) {
            component1.setFocusTraversalKeys(
                    KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS,
                    new HashSet<KeyStroke>(Arrays.asList(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, KeyEvent.CTRL_DOWN_MASK))));
        }
    }

    /**
     * Set the background if focus is gained and lost.
     * @param gainedColor Represents the color the components are highlighted if focus is gained
     * @param lostColor Represents the background color by losing focus
     * @param component The highlighted components
     */
    public static void highlightFocus(final Color gainedColor, Component... component) {
        FocusListener evt = new FocusListener() {

            Color oldBackground;
            @Override
            public void focusGained(FocusEvent e) {
                oldBackground = e.getComponent().getBackground();
                e.getComponent().setBackground(gainedColor);
            }

            @Override
            public void focusLost(FocusEvent e) {
                e.getComponent().setBackground(oldBackground);
            }
        };
        for (Component component1 : component) {
            component1.addFocusListener(evt);
        }
    }

    /**
     * Let a CheckBoxTableModel using JTable select a value by pressing
     * space on the keyboard.
     * @param jTable The table this method shall be used on
     */
    public static void spaceSelection(final JTable jTable){
        jTable.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent evt){
            if ( evt.getKeyCode() != KeyEvent.VK_SPACE || jTable.getSelectedRow() == -1 ) return;

        if ( jTable.getValueAt(jTable.getSelectedRow(), 0) == Boolean.TRUE ) {
            jTable.setValueAt(false, jTable.getSelectedRow(), 0);
        } else {
            jTable.setValueAt(true, jTable.getSelectedRow(), 0);
        }
            }
        });
    }
}
