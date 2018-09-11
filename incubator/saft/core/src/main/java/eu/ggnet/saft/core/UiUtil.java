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
package eu.ggnet.saft.core;

import java.io.IOException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;

import eu.ggnet.saft.core.ui.FxController;
import eu.ggnet.saft.core.ui.FxSaft;

import static eu.ggnet.saft.core.ui.FxSaft.loadView;

/**
 * Static utility classes for global useage.
 *
 * @author oliver.guenther
 */
public class UiUtil {

    /**
     * Constructs (loads) an FXML and controller pair.
     *
     * @param <T>
     * @param <R>
     * @param controllerClazz the controller class.
     * @return a loaded loader.
     * @throws IllegalArgumentException see {@link FxSaft#loadView(java.lang.Class) }
     * @throws IllegalStateException see {@link FxSaft#loadView(java.lang.Class) }
     * @throws NullPointerException see {@link FxSaft#loadView(java.lang.Class) }
     * @throws RuntimeException wrapped IOException of {@link FXMLLoader#load() }.
     */
    public static <T, R extends FxController> FXMLLoader constructFxml(Class<R> controllerClazz) throws IllegalArgumentException, NullPointerException, IllegalStateException, RuntimeException {
        if ( !Platform.isFxApplicationThread() ) throw new IllegalStateException("Method constructFxml is not called from the JavaFx Ui Thread, illegal");
        FXMLLoader loader = new FXMLLoader(loadView(controllerClazz));
        try {
            loader.load();
            return loader;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

}
