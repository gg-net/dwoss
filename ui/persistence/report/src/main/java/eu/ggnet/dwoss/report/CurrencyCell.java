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
package eu.ggnet.dwoss.report;

import java.text.NumberFormat;

import javafx.scene.control.TableCell;
import javafx.scene.paint.Color;

/**
 *
 * @author pascal.perau
 */
public class CurrencyCell extends TableCell<TableLine, Double> {

    public CurrencyCell() {
    }

    @Override
    protected void updateItem(Double item, boolean empty) {
        // calling super here is very important - don't skip this!
        super.updateItem(item, empty);

        // format the number as if it were a monetary value using the 
        // formatting relevant to the current locale. This would format
        // 43.68 as "$43.68", and -23.67 as "-$23.67"
        setText(item == null ? "" : NumberFormat.getCurrencyInstance().format(item));

        // change the text fill based on whether it is positive (green)
        // or negative (red). If the cell is selected, the text will 
        // always be white (so that it can be read against the blue 
        // background), and if the value is zero, we'll make it black.
        if ( item != null ) {
            double value = item;
            setTextFill(isSelected() ? Color.WHITE
                    : value == 0 ? Color.BLACK
                    : value < 0 ? Color.RED : Color.GREEN);
        }
    }

}
