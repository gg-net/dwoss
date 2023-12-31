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
package eu.ggnet.dwoss.uniqueunit.ui.cap;

import jakarta.inject.Inject;

import eu.ggnet.dwoss.core.widget.AccessableMenuItem;
import eu.ggnet.dwoss.core.widget.Progressor;
import eu.ggnet.dwoss.core.widget.auth.Guardian;
import eu.ggnet.dwoss.core.widget.dl.RemoteDl;
import eu.ggnet.dwoss.uniqueunit.api.UniqueUnitApi;
import eu.ggnet.dwoss.uniqueunit.ui.HistoryView;
import eu.ggnet.saft.core.Saft;

import jakarta.enterprise.context.Dependent;

import static eu.ggnet.dwoss.rights.api.AtomicRight.CREATE_COMMENT_UNIQUE_UNIT_HISTORY;

/**
 *
 * @author oliver.guenther
 */
@Dependent
public class AddHistoryToUnitMenuItem extends AccessableMenuItem {

    @Inject
    private Saft saft;

    @Inject
    private RemoteDl remote;

    @Inject
    private Guardian guardian;

    @Inject
    private Progressor progressor;

    public AddHistoryToUnitMenuItem() {
        super(CREATE_COMMENT_UNIQUE_UNIT_HISTORY);
        setOnAction(e -> {
            saft.build().swing().eval(HistoryView.class).cf()
                    .thenAccept(r -> progressor.run("Komentar zu " + r.refurbishId() + " hinzufügen", () -> remote.lookup(UniqueUnitApi.class).addHistoryByRefurbishId(r.refurbishId(), r.comment(), guardian.getUsername())))
                    .handle(saft.handler());
        });
    }

}
