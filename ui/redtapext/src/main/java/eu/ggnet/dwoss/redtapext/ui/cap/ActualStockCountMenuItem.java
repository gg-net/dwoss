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
package eu.ggnet.dwoss.redtapext.ui.cap;

import java.util.concurrent.CompletableFuture;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import javafx.scene.control.MenuItem;

import eu.ggnet.dwoss.core.widget.FileUtil;
import eu.ggnet.dwoss.core.widget.dl.RemoteDl;
import eu.ggnet.dwoss.redtapext.ee.reporting.RedTapeCloserManual;
import eu.ggnet.saft.core.Saft;

/**
 * Count the actual stock.
 * 
 * @author oliver.guenther
 */
public class ActualStockCountMenuItem extends MenuItem {

    @Inject
    private Saft saft;

    @Inject
    private RemoteDl remote;

    public ActualStockCountMenuItem() {
        super("Aktueller Lagerbestand");
    }

    @PostConstruct
    private void init() {
        setOnAction(e -> CompletableFuture.supplyAsync(() -> remote.lookup(RedTapeCloserManual.class).countStockAsXls().toTemporaryFile())
                .thenAccept(f -> FileUtil.osOpen(f))
                .handle(saft.handler())
        );
    }

}
