/*
 * Copyright (C) 2014 GG-Net GmbH - Oliver Günther
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
package eu.ggnet.dwoss.customer.ui.cap;

import java.awt.event.ActionEvent;

import jakarta.inject.Inject;

import javax.swing.AbstractAction;

import eu.ggnet.dwoss.core.widget.Progressor;
import eu.ggnet.dwoss.core.widget.dl.RemoteDl;
import eu.ggnet.dwoss.customer.ee.CustomerIndexManager;
import eu.ggnet.saft.core.Saft;

import jakarta.enterprise.context.Dependent;

/**
 *
 * @author pascal.perau
 */
@Dependent
public class RecreateSearchIndex extends AbstractAction {

    @Inject
    private Saft saft;

    @Inject
    private Progressor progressor;

    @Inject
    private RemoteDl remote;

    public RecreateSearchIndex() {
        super("Customer Suchindex neu erzeugen.");
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        saft.exec(() -> {
            progressor.run("Suchindex", () -> remote.lookup(CustomerIndexManager.class).reindexSearch());
            saft.build().alert("Suchindex wurde neu erzeugt");
        });
    }
}
