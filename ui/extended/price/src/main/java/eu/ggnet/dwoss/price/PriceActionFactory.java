/* 
 * Copyright (C) 2014 pascal.perau
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
package eu.ggnet.dwoss.price;

import java.util.*;

import org.openide.util.lookup.ServiceProvider;

import eu.ggnet.saft.core.ActionFactory;

import eu.ggnet.dwoss.mandator.MandatorSupporter;

import eu.ggnet.dwoss.price.imex.ContractorExportAction;
import eu.ggnet.dwoss.price.imex.ContractorImportAction;

import eu.ggnet.dwoss.rules.TradeName;

import static eu.ggnet.saft.core.Client.lookup;

/**
 * ActionFactory for Prices.
 * <p/>
 * @author oliver.guenther
 */
@ServiceProvider(service = ActionFactory.class)
public class PriceActionFactory implements ActionFactory {

    @Override
    public List<MetaAction> createMetaActions() {
        List<MetaAction> actions = new ArrayList<>();

        for (TradeName contractor : lookup(MandatorSupporter.class).loadContractors().all()) {
            actions.add(new MetaAction("Geschäftsführung", "Im-/Export", new ContractorExportAction(contractor)));
            actions.add(new MetaAction("Geschäftsführung", "Im-/Export", new ContractorImportAction(contractor)));
        }

        actions.add(new MetaAction("Geschäftsführung", "Preise", new PriceExportAction()));
        actions.add(new MetaAction("Geschäftsführung", "Preise", new PriceImportAction()));
        actions.add(new MetaAction("Geschäftsführung", "Preise", new PriceBlockerAction()));
        actions.add(new MetaAction("Geschäftsführung", "Preise", new GenerateOnePriceAction()));
        actions.add(new MetaAction("Geschäftsführung", "Preise", new PriceExportImportAction()));
        actions.add(new MetaAction("Geschäftsführung", "Preise", new PriceByInputFileAction()));

        return actions;
    }
}
