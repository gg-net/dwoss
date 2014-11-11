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
package eu.ggnet.dwoss.util.dialog;

import java.awt.Component;

import javax.swing.JOptionPane;

import lombok.experimental.Builder;

/**
 * Alert Dialog (like {@link JOptionPane#showMessageDialog(java.awt.Component, java.lang.Object, java.lang.String, int) ).
 * <p>
 * @author oliver.guenther
 */
@Builder
public class Alert {

    /**
     * The title, show in the decorations of the Window.
     */
    private String title = "";

    /**
     * The body or message.
     */
    private final String body;

    /**
     * A optional parent of Swing.
     */
    private Component parent = null;

    /**
     * Shows the Dialog as Information.
     */
    public void show() {
        JOptionPane.showMessageDialog(parent, body, title, JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Shows the Dialog as Error.
     */
    public void showAsError() {
        JOptionPane.showMessageDialog(parent, body, title, JOptionPane.ERROR_MESSAGE);
    }

}
