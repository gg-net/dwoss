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
package eu.ggnet.dwoss.stock.ui.cap;

import eu.ggnet.dwoss.stock.ui.cap.OpenStockTransactionManager;
import eu.ggnet.dwoss.stock.ui.cap.OpenCommissioningManager;

import java.util.Arrays;
import java.util.List;

import org.openide.util.lookup.ServiceProvider;

import eu.ggnet.saft.experimental.ops.ActionFactory;
import eu.ggnet.saft.experimental.ops.DescriptiveConsumerFactory;

/**
 * ActionFactory for Stock.
 * <p/>
 * @author oliver.guenther
 */
@ServiceProvider(service = ActionFactory.class)
public class StockActionFactory implements ActionFactory {

    @Override
    public List<MetaAction> createMetaActions() {
        return Arrays.asList(
                new MetaAction("Lager/Logistik", null),
                new MetaAction("Lager/Logistik", new CreateSimpleAction()),
                new MetaAction("Lager/Logistik", new RemoveUnitFromTransactionAction()),
                new MetaAction("Lager/Logistik", new OpenStockTransactionManager()),
                new MetaAction("Lager/Logistik", new OpenCommissioningManager()));
    }

    @Override
    public List<DescriptiveConsumerFactory<?>> createDependentActionFactories() {
        return Arrays.asList(new ConsumerFactoryOfStockTransactions());
    }

}