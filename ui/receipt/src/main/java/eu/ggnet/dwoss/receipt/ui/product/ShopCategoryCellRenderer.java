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
package eu.ggnet.dwoss.receipt.ui.product;



import java.awt.Component;

import javax.swing.*;

import eu.ggnet.dwoss.uniqueunit.api.ShopCategory;

/**
 *
 * @author pascal.perau
 */
public class ShopCategoryCellRenderer extends DefaultListCellRenderer {

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel)super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if ( value == null ) {
            label.setText("Keine Auswahl");
            return label;
        }
        if (value instanceof ShopCategory) {
            ShopCategory sc = (ShopCategory)value;
            label.setText(sc.name() + " (" + sc.shopId() + ")");
        } else {
            label.setText(value.toString());
        }
        return label;
    }
}
