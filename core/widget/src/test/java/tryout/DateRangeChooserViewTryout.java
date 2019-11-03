/*
 * Copyright (C) 2017 GG-Net GmbH
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
package tryout;

import javax.swing.JLabel;

import eu.ggnet.dwoss.core.widget.DateRangeChooserView;
import eu.ggnet.saft.core.Ui;
import eu.ggnet.saft.core.UiCore;

/**
 *
 * @author oliver.guenther
 */
public class DateRangeChooserViewTryout {

    public static void main(String[] args) {
        Ui.exec(() -> {
            UiCore.startSwing(() -> new JLabel("Main Applikation"));
            Ui.build().fx().eval(() -> new DateRangeChooserView()).opt().ifPresent(System.out::println);
        });
    }

}
