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
package eu.ggnet.dwoss.misc.ui.cap;

import java.util.*;
import java.util.stream.Collectors;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import eu.ggnet.dwoss.core.common.values.SalesChannel;
import eu.ggnet.dwoss.mandator.api.service.ListingActionConfiguration;
import eu.ggnet.dwoss.mandator.api.service.ListingActionConfiguration.Location;
import eu.ggnet.dwoss.mandator.api.service.ListingActionConfiguration.Type;
import eu.ggnet.dwoss.mandator.api.service.ListingActionService;
import eu.ggnet.saft.core.Dl;

/**
 *
 * @author oliver.guenther
 */
public class SalesListingCreateMenuItemProducer {
    
    public static class SalesListingCreateMenus {

        public final List<SalesListingCreateMenuItem> items;

        public SalesListingCreateMenus(List<SalesListingCreateMenuItem> items) {
            this.items = Objects.requireNonNull(items,"items must not be null");
        }
         
    }

    @Inject
    private Instance<SalesListingCreateMenuItem> instances;

    @Produces
    public SalesListingCreateMenus createMenuItems() {
        List<SalesListingCreateMenuItem> items = new ArrayList<>();
        if ( Dl.remote().contains(ListingActionService.class) ) {
            Map<ListingActionConfiguration.Location, List<ListingActionConfiguration>> actionConfigs = Dl.remote().lookup(ListingActionService.class).listingActionConfigurations().stream()
                    .collect(Collectors.groupingBy(conf -> conf.location));
            if ( actionConfigs != null && !actionConfigs.isEmpty() ) {
                for (List<ListingActionConfiguration> listingActionConfigurations : actionConfigs.values()) {
                    for (ListingActionConfiguration listingActionConfiguration : listingActionConfigurations) {
                        SalesListingCreateMenuItem item = instances.select().get();
                        item.setConfig(listingActionConfiguration);
                        items.add(item);
                    }
                }
            }
        } else {
            SalesListingCreateMenuItem item = instances.select().get();
            item.setConfig(new ListingActionConfiguration(Type.XLS, Location.LOCAL, SalesChannel.RETAILER, "XLS für Händler"));
            items.add(item);
            item = instances.select().get();
            item.setConfig(new ListingActionConfiguration(Type.XLS, Location.LOCAL, SalesChannel.CUSTOMER, "XLS für Endkunden"));
            items.add(item);
            item = instances.select().get();
            item.setConfig(new ListingActionConfiguration(Type.PDF, Location.LOCAL, SalesChannel.RETAILER, "PDF für Händler"));
            items.add(item);
            item = instances.select().get();
            item.setConfig(new ListingActionConfiguration(Type.PDF, Location.LOCAL, SalesChannel.CUSTOMER, "PDF für Endkunden"));
            items.add(item);
        }
        return new SalesListingCreateMenus(items);
    }

}
