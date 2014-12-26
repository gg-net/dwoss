/*
 * Copyright (C) 2014 GG-Net GmbH
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
package eu.ggnet.saft.sample.search;

import eu.ggnet.saft.api.ui.*;
import eu.ggnet.saft.core.Ui;

/**
 *
 * @author oliver.guenther
 */
@DefaultAction
@Title("Show Unit Details")
public class UnitDetailViewAction implements DependendAction<MicroUnit>{

    @Override
    public void run(MicroUnit t) {
        Ui.call(()-> t).openFx(UnitDetailView.class).exec();
    }
    
}
