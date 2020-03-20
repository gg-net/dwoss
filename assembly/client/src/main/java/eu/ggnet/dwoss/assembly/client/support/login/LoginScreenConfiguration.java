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
package eu.ggnet.dwoss.assembly.client.support.login;

import java.util.Optional;
import java.util.function.Consumer;

import javafx.scene.layout.Pane;

import org.inferred.freebuilder.FreeBuilder;

import eu.ggnet.saft.experimental.auth.Guardian;

/**
 * Configuration for the LoginScreen.
 * Combines success, cancel on optional guardian configuration.
 *
 * @author oliver.guenther
 */
@FreeBuilder
public interface LoginScreenConfiguration {

    class Builder extends LoginScreenConfiguration_Builder {
    }

    /**
     * Consumer for successfull authentication.
     *
     * @return Consumer for successfull authentication.
     */
    Consumer<Pane> onSuccess();

    /**
     * Runnable for cancel operation.
     *
     * @return Runnable for cancel operation.
     */
    Runnable onCancel();

    /**
     * Optional guardian for authentication.
     * It's optional here, as the login screen can be used in a lazy mode.
     *
     * @return Guardian for authentication.
     */
    Optional<Guardian> guardian();
}
