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
package eu.ggnet.dwoss.redtapext.ui.cao.document.position;

import java.util.function.Consumer;

import eu.ggnet.dwoss.redtape.ee.entity.Position;
import eu.ggnet.saft.core.Ui;
import eu.ggnet.saft.experimental.ops.DefaultAction;
import eu.ggnet.saft.core.ui.Title;

/**
 *
 * @author oliver.guenther
 */
@DefaultAction
@Title("Positions Details anzeigen")
public class PositionViewAction implements Consumer<Position> {

    @Override
    public void accept(Position pos) {
        Ui.exec(() -> {
            Ui.build().fx().show(() -> pos, () -> new PositionViewCask());
        });
    }

}