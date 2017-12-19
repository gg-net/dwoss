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
package eu.ggnet.dwoss.misc.action.listings;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import eu.ggnet.dwoss.configuration.GlobalConfig;
import eu.ggnet.dwoss.mandator.api.service.ListingActionConfiguration;
import eu.ggnet.dwoss.misc.op.listings.SalesListingProducer;
import eu.ggnet.saft.Ui;

import static eu.ggnet.saft.Client.lookup;

/**
 *
 * @author oliver.guenther
 */
public class SalesListingCreateAction extends AbstractAction {

    ListingActionConfiguration config;

    public SalesListingCreateAction(ListingActionConfiguration config) {
        super(config.getName());
        this.config = config;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Ui.exec(() -> {
            Ui.progress().title(config.getName()).call(() -> {
                lookup(SalesListingProducer.class).generateListings(config).forEach(fj -> fj.toFile(GlobalConfig.APPLICATION_PATH_OUTPUT));
                return null;
            });
        });

    }
}
