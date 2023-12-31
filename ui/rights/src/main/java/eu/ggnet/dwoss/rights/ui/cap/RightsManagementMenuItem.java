/*
 * Copyright (C) 2021 GG-Net GmbH
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
package eu.ggnet.dwoss.rights.ui.cap;

import jakarta.inject.Inject;

import jakarta.annotation.PostConstruct;

import javafx.scene.control.MenuItem;

import eu.ggnet.dwoss.core.widget.AccessableMenuItem;
import eu.ggnet.dwoss.rights.ui.RightsManagementController;
import eu.ggnet.saft.core.Saft;

import jakarta.enterprise.context.Dependent;

import static eu.ggnet.dwoss.rights.api.AtomicRight.CREATE_UPDATE_RIGHTS;

/**
 * {@link MenuItem} to allow the creation and modification of {@link User}<code>s</code> and {@link Group}<code>s</code>.
 *
 * @author mirko.schulze
 */
@Dependent
public class RightsManagementMenuItem extends AccessableMenuItem {

    @Inject
    private Saft saft;

    public RightsManagementMenuItem() {
        super(CREATE_UPDATE_RIGHTS);
    }

    @PostConstruct
    private void init() {
        setOnAction(e -> saft.build().fxml().show(RightsManagementController.class));
    }

}
