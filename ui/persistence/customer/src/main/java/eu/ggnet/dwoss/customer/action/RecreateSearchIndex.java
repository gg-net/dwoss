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
package eu.ggnet.dwoss.customer.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import eu.ggnet.dwoss.customer.priv.SearchSingleton;
import eu.ggnet.saft.Ui;
import eu.ggnet.saft.core.Alert;

import static eu.ggnet.saft.core.Client.lookup;

/**
 *
 * @author pascal.perau
 */
public class RecreateSearchIndex extends AbstractAction {

    public RecreateSearchIndex() {
        super("Customer Suchindex neu erzeugen.");
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        Ui.exec(() -> {
            Ui.progress().wrap(() -> lookup(SearchSingleton.class).reindexSearch()).run();
            Alert.show("Suchindex wurde neu erzeugt");
        });
    }
}
