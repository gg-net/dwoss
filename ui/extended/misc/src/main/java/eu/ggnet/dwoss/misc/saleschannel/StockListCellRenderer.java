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
package eu.ggnet.dwoss.misc.saleschannel;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import eu.ggnet.dwoss.stock.ee.entity.Stock;

/**
 * Cell Renderer for the Stock Class.
 * <p>
 * @author oliver.guenther
 */
public class StockListCellRenderer extends DefaultListCellRenderer {

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if ( value instanceof Stock ) setText(((Stock)value).getName());
        if ( value == null ) setText("Kein Transfer");
        return this;
    }
}
