/*
 * Copyright (C) 2023 GG-Net GmbH
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
package eu.ggnet.dwoss.stock.ee.assist.gen;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

import static eu.ggnet.dwoss.stock.ee.entity.QLogicTransaction.logicTransaction;
import static eu.ggnet.dwoss.stock.ee.entity.QShipment.shipment;
import static eu.ggnet.dwoss.stock.ee.entity.QStock.stock;
import static eu.ggnet.dwoss.stock.ee.entity.QStockTransaction.stockTransaction;
import static eu.ggnet.dwoss.stock.ee.entity.QStockTransactionParticipation.stockTransactionParticipation;
import static eu.ggnet.dwoss.stock.ee.entity.QStockTransactionPosition.stockTransactionPosition;
import static eu.ggnet.dwoss.stock.ee.entity.QStockTransactionStatus.stockTransactionStatus;
import static eu.ggnet.dwoss.stock.ee.entity.QStockUnit.stockUnit;

/**
 * Utility class to clear the database.
 *
 * @author oliver.guenther
 */
public class StockDeleteUtils {

    private StockDeleteUtils() {
    }

    public static void deleteAll(EntityManager em) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        queryFactory.delete(shipment).execute();
        queryFactory.delete(stockTransactionParticipation).execute();
        queryFactory.delete(stockTransactionPosition).execute();
        queryFactory.delete(stockTransaction).execute();
        queryFactory.delete(stockTransactionStatus).execute();
        queryFactory.delete(stockUnit).execute();
        queryFactory.delete(stock).execute();
        queryFactory.delete(logicTransaction).execute();
        em.flush();
    }

    /**
     * If the database is empty, the result is null, otherwise a message with more details.
     *
     * @param em the custmoer emtitymanager
     * @return null if empty, otherwise a message with details.
     */
    public static String validateEmpty(EntityManager em) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        if ( !queryFactory.from(logicTransaction).fetch().isEmpty() ) return "stock.LogicTransaction is not empty";
        if ( !queryFactory.from(shipment).fetch().isEmpty() ) return "stock.Shipment is not empty";
        if ( !queryFactory.from(stock).fetch().isEmpty() ) return "stock.Stock is not empty";
        if ( !queryFactory.from(stockTransaction).fetch().isEmpty() ) return "stock.StockTransactionUniqueUnit is not empty";
        if ( !queryFactory.from(stockTransactionParticipation).fetch().isEmpty() ) return "stock.StockTransactionParticipation is not empty";
        if ( !queryFactory.from(stockTransactionPosition).fetch().isEmpty() ) return "stock.StockTransactionPosition is not empty";
        if ( !queryFactory.from(stockTransactionStatus).fetch().isEmpty() ) return "stock.StockTransactionStatus is not empty";
        if ( !queryFactory.from(stockUnit).fetch().isEmpty() ) return "stock.StockUnit is not empty";
        return null;
    }

}
