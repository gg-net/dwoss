/*
 * Copyright (C) 2020 GG-Net GmbH
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
package eu.ggnet.dwoss.redtapext.ui.cap;

import java.awt.BorderLayout;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;

import org.openide.util.lookup.ServiceProvider;

import eu.ggnet.dwoss.core.widget.MainComponent;

/**
 *
 * @author oliver.guenther
 */
@ServiceProvider(service = MainComponent.class)
public class UnitAvilabilityPanel extends JFXPanel implements MainComponent {

    public UnitAvilabilityPanel() {
        Platform.runLater(() -> {
            this.setScene(new Scene(new UnitAvailabilityPane()));
        });
    }

    @Override
    public String getLayoutHint() {
        return BorderLayout.WEST;
    }

}
