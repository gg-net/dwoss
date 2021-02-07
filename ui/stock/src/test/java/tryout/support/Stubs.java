/*
 * Copyright (C) 2021 GG-Net GmbH
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
package tryout.support;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import eu.ggnet.dwoss.stock.api.StockApi;
import eu.ggnet.dwoss.stock.ee.StockAgent;
import eu.ggnet.dwoss.stock.ee.StockTransactionProcessor;
import eu.ggnet.dwoss.stock.ee.entity.*;

/**
 *
 * @author oliver.guenther
 */
public class Stubs {

    private final static Random R = new Random();

    private final Stock luebeck = new Stock(1, "Lübeck");

    private final Stock hamburg = new Stock(2, "Hamburg");

    private final List<StockUnit> stockUnits;

    public Stubs() {
        stockUnits = IntStream.range(1, 20).mapToObj(i -> new StockUnit(R.nextInt(10000) + "", "Gerät " + i, i))
                .map((StockUnit su) -> {
                    su.setStock(R.nextBoolean() == true ? luebeck : hamburg);
                    su.setLogicTransaction(R.nextInt(10) >= 7 ? null : new LogicTransaction());
                    return su;
                })
                .collect(Collectors.toList());
    }

    public StockAgent stockAgent() {
        return new StockAgentStub(Arrays.asList(luebeck, hamburg), stockUnits);
    }

    public StockApi stockApi() {
        return new StockApiStub(Arrays.asList(luebeck, hamburg), stockUnits);
    }

    public StockTransactionProcessor stockTransactionProcessor() {
        return new StockTransactionProcessorStub();
    }

}
