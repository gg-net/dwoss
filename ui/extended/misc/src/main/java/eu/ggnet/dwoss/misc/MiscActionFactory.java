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
package eu.ggnet.dwoss.misc;

import java.util.*;
import java.util.stream.Collectors;

import org.openide.util.lookup.ServiceProvider;

import eu.ggnet.dwoss.mandator.api.service.ListingActionConfiguration;
import eu.ggnet.dwoss.mandator.api.service.ListingActionConfiguration.Location;
import eu.ggnet.dwoss.mandator.api.service.ListingActionConfiguration.Type;
import eu.ggnet.dwoss.mandator.api.service.ListingService;
import eu.ggnet.dwoss.misc.action.*;
import eu.ggnet.dwoss.misc.action.imageid.ExportImageIdsAction;
import eu.ggnet.dwoss.misc.action.imageid.ImportImageIdsAction;
import eu.ggnet.dwoss.misc.action.listings.AllSalesListingAction;
import eu.ggnet.dwoss.misc.action.listings.SalesListingCreateAction;
import eu.ggnet.dwoss.misc.action.movement.MovementAction;
import eu.ggnet.dwoss.misc.help.AboutAction;
import eu.ggnet.dwoss.misc.help.ShowMandatorAction;
import eu.ggnet.dwoss.misc.op.movement.MovementListingProducer;
import eu.ggnet.dwoss.misc.op.movement.MovementListingProducer.ListType;
import eu.ggnet.dwoss.misc.saleschannel.OpenSalesChannelManagerAction;
import eu.ggnet.dwoss.rules.SalesChannel;
import eu.ggnet.dwoss.stock.StockAgent;
import eu.ggnet.dwoss.stock.entity.Stock;
import eu.ggnet.saft.core.ActionFactory;

import static eu.ggnet.saft.core.Client.hasFound;
import static eu.ggnet.saft.core.Client.lookup;

/**
 *
 * @author oliver.guenther
 */
@ServiceProvider(service = ActionFactory.class)
public class MiscActionFactory implements ActionFactory {

    @Override
    public List<MetaAction> createMetaActions() {
        List<MetaAction> actions = new ArrayList<>();
        actions.add(new MetaAction("System", "Datenbank", new ProductSpecExportAction()));
        actions.add(new MetaAction("System", "Datenbank", new DatabaseValidationAction()));

        String s = "Listings";
        actions.add(new MetaAction(s, new AllSalesListingAction()));

        if ( hasFound(ListingService.class) ) {
            Map<ListingActionConfiguration.Location, List<ListingActionConfiguration>> actionConfigs = lookup(ListingService.class).listingActionConfigurations().stream()
                    .collect(Collectors.groupingBy(ListingActionConfiguration::getLocation));
            if ( actionConfigs != null && !actionConfigs.isEmpty() ) {
                for (List<ListingActionConfiguration> listingActionConfigurations : actionConfigs.values()) {
                    actions.add(new MetaAction(s, null));
                    for (ListingActionConfiguration listingActionConfiguration : listingActionConfigurations) {
                        actions.add(new MetaAction(s, new SalesListingCreateAction(listingActionConfiguration)));
                    }
                }
            }
        } else {
            actions.add(new MetaAction(s, new SalesListingCreateAction(new ListingActionConfiguration(Type.XLS, Location.LOCAL, SalesChannel.RETAILER, "XLS für Händler"))));
            actions.add(new MetaAction(s, new SalesListingCreateAction(new ListingActionConfiguration(Type.XLS, Location.LOCAL, SalesChannel.CUSTOMER, "XLS für Endkunden"))));
            actions.add(new MetaAction(s, new SalesListingCreateAction(new ListingActionConfiguration(Type.PDF, Location.LOCAL, SalesChannel.RETAILER, "PDF für Händler"))));
            actions.add(new MetaAction(s, new SalesListingCreateAction(new ListingActionConfiguration(Type.PDF, Location.LOCAL, SalesChannel.CUSTOMER, "PDF für Endkunden"))));
        }

        List<Stock> allStocks = lookup(StockAgent.class).findAll(Stock.class);
        for (Stock stock : allStocks) {
            for (ListType listType : MovementListingProducer.ListType.values()) {
                actions.add(new MetaAction("Lager/Logistik", "Versand & Abholung", new MovementAction(listType, stock)));
            }
            actions.add(new MetaAction("Lager/Logistik", "Inventur", new StockTakingAction(stock)));
        }
        actions.add(new MetaAction("Lager/Logistik", "Inventur", new StockTakingAction()));
        actions.add(new MetaAction("Artikelstamm", "Bilder Ids", new NextImageIdAction()));
        actions.add(new MetaAction("Artikelstamm", "Bilder Ids", new ExportImageIdsAction()));
        actions.add(new MetaAction("Artikelstamm", "Bilder Ids", new ExportImageIdsAction(SalesChannel.CUSTOMER)));
        actions.add(new MetaAction("Artikelstamm", "Bilder Ids", new ImportImageIdsAction()));
        actions.add(new MetaAction("Geschäftsführung", "Allgemeine Reporte", new UnitQualityReportAction()));
        actions.add(new MetaAction("Geschäftsführung", "Allgemeine Reporte", new ExportInputReportAction()));
        actions.add(new MetaAction("Geschäftsführung", new OpenSalesChannelManagerAction()));
        actions.add(new MetaAction("Geschäftsführung", "Abschluss Reporte", new ResolveRepaymentAction()));
        actions.add(new MetaAction("Hilfe", new AboutAction()));
        actions.add(new MetaAction("Hilfe", new ShowMandatorAction()));
        actions.add(new MetaAction("Hilfe", new JndiTreeInspectAction()));

        return actions;
    }
}
