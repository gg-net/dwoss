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
package eu.ggnet.dwoss.search.ui.cap;

import java.util.ArrayList;
import java.util.List;

import org.openide.util.lookup.ServiceProvider;

import eu.ggnet.saft.experimental.ops.ActionFactory;

/**
 *
 * @author oliver.guenther
 */
@ServiceProvider(service = ActionFactory.class)
public class SearchActionFactory implements ActionFactory {

    @Override
    public List<MetaAction> createMetaActions() {
        List<MetaAction> actions = new ArrayList<>();
        actions.add(new MetaAction("Kunden und Aufträge", new OpenSearchAction()));
        return actions;
    }
}
