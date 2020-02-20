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
package eu.ggnet.dwoss.assembly.client.support;

import java.util.concurrent.ExecutorService;

import javax.inject.Inject;

import javafx.scene.control.MenuItem;

import eu.ggnet.saft.core.Ui;

/**
 *
 * @author oliver.guenther
 */
public class LocalProgressSimulatorMenuItem extends MenuItem {

    @Inject @Executor
    public ExecutorService es;
    
    public LocalProgressSimulatorMenuItem() {
        super("Lokale Hintergrundaktivität simulieren");
        setOnAction((e) -> es.submit(() -> Ui.progress().call(() -> {
                    Thread.sleep(3000);
                    System.out.println("done");
                    return null;
                })));
    }
    
    
    
}
