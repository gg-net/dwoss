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

import eu.ggnet.dwoss.core.widget.*;
import eu.ggnet.dwoss.core.widget.dl.RemoteDl;
import eu.ggnet.dwoss.rights.api.AtomicRight;
import static eu.ggnet.dwoss.rights.api.AtomicRight.CHANGE_TAX;
import eu.ggnet.dwoss.uniqueunit.api.UniqueUnitApi;
import eu.ggnet.dwoss.uniqueunit.ui.ProductHistoryController;
import eu.ggnet.saft.core.Saft;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 * MenuItem to export information of a product to a xls file.
 *
 * @author mirko.schulze
 */
//TODO: welches right?
public class ProductHistoryMenuItem extends AccessableMenuItem {

    @Inject
    private Saft saft;

    @Inject
    private RemoteDl remote;

    @Inject
    private Progressor progressor;

    public ProductHistoryMenuItem(AtomicRight right) {
        super(CHANGE_TAX);
    }

    @PostConstruct
    private void init() {
        saft.build().fxml()
                .eval(ProductHistoryController.class)
                .cf()
                .thenApply(partNo -> progressor.run("Historie für Artikel " + partNo + " erstellen.",
                () -> remote.lookup(UniqueUnitApi.class).toUnitsOfPartNoAsXls(partNo).toTemporaryFile()))
                .thenAccept(f -> FileUtil.osOpen(f))
                .handle(saft.handler());
    }

}
