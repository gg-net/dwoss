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

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import javafx.fxml.FXMLLoader;
import javafx.util.Callback;

/**
 *
 * @author oliver.guenther
 */
public class FxmlLoaderInitializer {

    private final Callback<Class<?>, Object> factory;

    public FxmlLoaderInitializer(Callback<Class<?>, Object> factory) {
        this.factory = Objects.requireNonNull(factory, "factory must not be null");
    }

    public FXMLLoader createLoader(URL fxml) {
        return new FXMLLoader(Objects.requireNonNull(fxml, "fxml must not be null"), null, null, factory, StandardCharsets.UTF_8);
    }

}
