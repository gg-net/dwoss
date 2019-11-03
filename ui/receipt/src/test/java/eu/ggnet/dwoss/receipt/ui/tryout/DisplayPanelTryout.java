/*
 * Copyright (C) 2018 GG-Net GmbH
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
package eu.ggnet.dwoss.receipt.ui.tryout;

import javax.swing.UIManager;

import eu.ggnet.dwoss.core.common.values.ProductGroup;
import eu.ggnet.dwoss.core.widget.swing.CloseType;
import eu.ggnet.dwoss.core.widget.swing.OkCancelDialog;
import eu.ggnet.dwoss.receipt.ui.product.DisplayPanel;
import eu.ggnet.dwoss.spec.ee.entity.piece.Display;

/**
 *
 * @author oliver.guenther
 */
public class DisplayPanelTryout {

    public static void main(String[] args) throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        // A full Display
//        Display display = new Display(Display.Size._15_4, Display.Resolution.HD,
//                Display.Type.CRYSTAL_BRIGHT, Display.Ration.SIXTEEN_TO_NINE);
//        display.setLed(true);
        Display display = new Display(null, null, null, null);
        System.out.println(display);
        DisplayPanel view = new DisplayPanel(ProductGroup.TABLET_SMARTPHONE);
        view.setDisplay(display);
        OkCancelDialog<DisplayPanel> create = new OkCancelDialog<>(" ", view);
        create.setVisible(true);
        if ( create.getCloseType() == CloseType.OK ) {
            System.out.println("Result :" + view.getDisplay());
        }
        System.exit(0);
    }

}
