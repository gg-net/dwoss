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
package eu.ggnet.dwoss.redtape.renderer;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.EtchedBorder;

import eu.ggnet.dwoss.redtape.entity.Position;
import eu.ggnet.dwoss.redtape.format.PositionFormater;

/**
 *
 * @author pascal.perau
 */
public class PositionStringRenderer extends DefaultListCellRenderer {

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        String content;
        if ( value instanceof Position ) {
            content = "<html>" + PositionFormater.toHtmlShort(((Position)value), ((int)(list.getWidth() - (list.getWidth() * 0.23))) + "px") + "</html>";
        } else {
            content = value.toString();
        }
        JLabel label = (JLabel)super.getListCellRendererComponent(list, content, index, isSelected, cellHasFocus);
        setBorder(new EtchedBorder(1, new Color(204, 204, 255), Color.BLACK));
        return label;
    }
}
