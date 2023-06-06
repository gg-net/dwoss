/*
 * Copyright (C) 2023 GG-Net GmbH
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
package eu.ggnet.dwoss.report.ui.cap;

import java.util.concurrent.CompletableFuture;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import javafx.scene.control.MenuItem;

import eu.ggnet.dwoss.core.widget.Progressor;
import eu.ggnet.dwoss.core.widget.dl.RemoteDl;
import eu.ggnet.dwoss.report.ee.ReportAgent;
import eu.ggnet.saft.core.Saft;

/**
 *
 * @author oliver.guenther
 */
public class MigrateMenuItem extends MenuItem {
    @Inject
    private Saft saft;

    @Inject
    private RemoteDl remote;

    @Inject
    private Progressor progressor;

    public MigrateMenuItem() {
        super("Migrate Reports");
    }
    
    @PostConstruct
    public void init() {
        setOnAction(e -> CompletableFuture.runAsync(() -> remote.lookup(ReportAgent.class).migrate()));
    }

    
}
