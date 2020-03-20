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
package eu.ggnet.dwoss.stock.ui.transactions;

import java.util.ArrayList;
import java.util.List;

import eu.ggnet.dwoss.stock.ee.entity.Stock;
import eu.ggnet.dwoss.stock.ee.entity.StockUnit;

/**
 * The CreateQuestionModel. This class assumes, that all data is valid.
 * <p>
 * @author oliver.guenther
 */
public class CreateQuestionModel {

    public final Stock source;

    public final Stock destination;

    public final String comment;

    public final List<StockUnit> stockUnits = new ArrayList<>();

    public CreateQuestionModel(StockUnit su, Stock destination, String comment) {
        this.source = su.getStock();
        this.destination = destination;
        this.comment = comment;
        this.stockUnits.add(su);
    }

    public CreateQuestionModel(Stock source, Stock destination, List<StockUnit> sus, String comment) {
        this.source = source;
        this.destination = destination;
        stockUnits.addAll(sus);
        this.comment = comment;
    }

}
