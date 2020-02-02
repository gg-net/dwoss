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
package eu.ggnet.dwoss.assembly.remote.cdi;

import java.io.IOException;

import javax.inject.Inject;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import eu.ggnet.dwoss.misc.ui.AboutController;
import eu.ggnet.dwoss.misc.ui.AboutController.In;

/**
 * Main CDI Class. will be started by the container.
 *
 * @author oliver.guenther
 */
public class CdiClient {

    @Inject
    private Try ty;

    @Inject
    private FxmlLoaderInitializer loaderInitialzer;

    public void main() {
        System.out.println("Main Ty:" + ty);
    }

    public Parent root() {
        try {
            FXMLLoader loader = loaderInitialzer.createLoader(AboutController.class.getResource("AboutView.fxml"));
            Parent root = loader.load();
            AboutController controller = loader.getController();
            controller.accept(new In("InfoText", "DebugText"));
            return root;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

}
