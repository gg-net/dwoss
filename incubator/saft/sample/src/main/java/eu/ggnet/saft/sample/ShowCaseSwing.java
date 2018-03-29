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
package eu.ggnet.saft.sample;

import javax.swing.*;

import eu.ggnet.saft.UiCore;
import eu.ggnet.saft.sample.support.MainPanelAddButtons;

/**
 *
 * @author oliver.guenther
 */
public class ShowCaseSwing extends ShowCaseUniversal {

    public JPanel build() {
        MainPanelAddButtons main = new MainPanelAddButtons();
        for (Smenu smenu : MENUS) {
            JMenu submenu = new JMenu(smenu.getName());
            for (Sitem item : smenu.getItems()) {
                JMenuItem menuItem = new JMenuItem(item.getKey());
                menuItem.addActionListener((e) -> item.getValue().run());
                submenu.add(menuItem);
            }
            main.getMenuBar().add(submenu);
        }
        return main;
    }

    public static void main(String[] args) {
        UiCore.startSwing(() -> new ShowCaseSwing().build());
    }

}
