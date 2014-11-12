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
package eu.ggnet.dwoss.util;

import java.util.Collection;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

/**
 *
 * @author oliver.guenther
 */
public class ComboBoxController<T> {

    private JComboBox comboBox;

    public ComboBoxController(JComboBox comboBox, Collection<T> elems) {
        this(comboBox, elems.toArray());
    }

    public ComboBoxController(JComboBox comboBox, Object[] elems) {
        this.comboBox = comboBox;
        this.comboBox.setModel(new DefaultComboBoxModel(elems));
    }

    public T getSelected() {
        return (T)comboBox.getSelectedItem();
    }

    public void setEnabled(boolean enable) {
        comboBox.setEnabled(enable);
    }

    public void setSelected(T t) {
        // TODO: Check all usages, if this doesn't impact anything
        if (t == null) return;
        comboBox.setSelectedItem(t);
    }

    public void replaceElements(Collection<T> elems) {
        T lastSelection = getSelected();
        comboBox.setModel(new DefaultComboBoxModel(elems.toArray()));
        if ( elems.contains(lastSelection) ) comboBox.setSelectedItem(elems);
    }
}