/*
 * Copyright (C) 2014 GG-Net GmbH
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
package eu.ggnet.dwoss.stock.transactions;

import java.util.*;
import java.util.stream.Collectors;

import eu.ggnet.dwoss.stock.StockAgent;
import eu.ggnet.dwoss.stock.StockTransactionProcessor;
import eu.ggnet.dwoss.stock.entity.Stock;
import eu.ggnet.dwoss.stock.entity.StockUnit;
import eu.ggnet.dwoss.uniqueunit.api.PicoUnit;
import eu.ggnet.saft.core.Alert;
import eu.ggnet.saft.core.Ui;
import eu.ggnet.saft.core.all.DescriptiveConsumer;
import eu.ggnet.saft.core.all.DescriptiveConsumerFactory;
import eu.ggnet.saft.core.authorisation.Guardian;

import static eu.ggnet.dwoss.rights.api.AtomicRight.CREATE_TRANSACTION_FOR_SINGLE_UNIT;
import static eu.ggnet.saft.core.Client.lookup;

/**
 * Supplies dependent actions of Stock.
 * <p>
 * @author oliver.guenther
 */
public class ConsumerFactoryOfStockTransactions implements DescriptiveConsumerFactory<PicoUnit> {

    @Override
    public List<DescriptiveConsumer<PicoUnit>> of(PicoUnit t) {
        StockAgent stockAgent = lookup(StockAgent.class);
        StockUnit su = stockAgent.findStockUnitByUniqueUnitIdEager(t.uniqueUnitId);
        if ( su == null || su.isInTransaction() ) return Collections.EMPTY_LIST;
        Guardian guardian = lookup(Guardian.class);
        if ( !guardian.hasRight(CREATE_TRANSACTION_FOR_SINGLE_UNIT) ) return Collections.EMPTY_LIST;
        return stockAgent.findAll(Stock.class)
                .stream()
                .filter(s -> !s.equals(su.getStock()))
                .map(s -> {
                    return new DescriptiveConsumer<PicoUnit>("Umfuhr von " + su.getStock().getName() + " nach " + s.getName(), (PicoUnit t1) -> {
                        Ui.call(() -> new CreateQuestionModel(su, s, "Umfuhr direkt durch Nutzer erzeugt"))
                        .choiceFx(CreateQuestionView.class)
                        .onOk(v -> {
                            lookup(StockTransactionProcessor.class).perpareTransfer(
                                    v.model().stockUnits,
                                    v.model().destination.getId(),
                                    guardian.getUsername(),
                                    v.model().comment);
                            Alert.show("Umfuhr anglegt.");
                            return null;
                        }).exec();
                    });
                })
                .collect(Collectors.toList());
    }

}
