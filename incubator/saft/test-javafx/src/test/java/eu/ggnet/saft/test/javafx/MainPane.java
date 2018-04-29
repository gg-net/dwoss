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
package eu.ggnet.saft.test.javafx;

import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;

import eu.ggnet.saft.core.Ui;

/**
 *
 * @author oliver.guenther
 */
public class MainPane extends FlowPane {

    @SuppressWarnings("OverridableMethodCallInConstructor")
    public MainPane() {
        Button showPane = new Button("ShowPane");
        showPane.setId("showPane");
        showPane.setOnAction(e -> Ui.build(this).fx().show(() -> new APane()));
        Button showJPanel = new Button("ShowJPanel");
        showJPanel.setOnAction(e -> Ui.build(this).swing().show(() -> new AJPanel()));
        showJPanel.setId("showJPanel");
        getChildren().addAll(showPane, showJPanel);
    }

}
