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
package eu.ggnet.dwoss.redtapext.ui.cao.common;

import java.util.SortedMap;

import org.apache.commons.lang3.StringUtils;

import eu.ggnet.dwoss.mandator.api.service.ShippingCostService;
import eu.ggnet.dwoss.mandator.spi.CachedMandators;
import eu.ggnet.dwoss.redtape.ee.entity.Document;
import eu.ggnet.dwoss.redtape.ee.entity.Position;
import eu.ggnet.dwoss.core.common.values.PositionType;
import eu.ggnet.dwoss.core.common.values.ShippingCondition;
import eu.ggnet.dwoss.core.widget.Dl;

/**
 * A helper class that provides methods to modify shipping costs.
 *
 * @author pascal.perau
 * @author bastian.venz
 */
public class ShippingCostHelper {

    /**
     * Method to update/refresh the shipping cost of a {@link Document}.
     * <p>
     * @param doc               the {@link Document} entity.
     * @param shippingCondition the condition on which the shipping cost is calculated
     */
    public static void modifyOrAddShippingCost(Document doc, ShippingCondition shippingCondition) {
        int amountOfPositions = doc.getPositions(PositionType.UNIT).size();
        for (Position position : doc.getPositions(PositionType.PRODUCT_BATCH).values()) {
            //just quick for the warranty extension. sucks a bit if we ever wouldhave a refurbished id on another product batch
            if ( !StringUtils.isBlank(position.getRefurbishedId()) ) continue;
            amountOfPositions += position.getAmount();
        }
        double costs = 0;
        if ( Dl.remote().contains(ShippingCostService.class) )
            costs = Dl.remote().lookup(ShippingCostService.class).calculate(amountOfPositions, doc.getDossier().getPaymentMethod(), shippingCondition);
        SortedMap<Integer, Position> positions = doc.getPositions(PositionType.SHIPPING_COST);
        if ( positions.isEmpty() ) {
            doc.append(Position.builder().type(PositionType.SHIPPING_COST)
                    .name("Versandkosten").description("Versandkosten zu Vorgang: " + doc.getDossier().getIdentifier())
                    .amount(1).price(costs).tax(doc.getTaxType().tax())
                    .bookingAccount(Dl.local().lookup(CachedMandators.class).loadPostLedger().get(PositionType.SHIPPING_COST, doc.getTaxType()).orElse(null))
                    .build());
        } else {
            Position next = positions.values().iterator().next();
            next.setPrice(costs);
        }
    }

    /**
     * Method to remove the shipping cost from a {@link Document}.
     * <p>
     * @param doc the {@link Document} entity.
     */
    public static void removeShippingCost(Document doc) {
        for (Position position : doc.getPositions(PositionType.SHIPPING_COST).values()) doc.remove(position);
    }
}
