/*
 * Copyright (C) 2017 GG-Net GmbH
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
package eu.ggnet.dwoss.misc.web.stub;

import eu.ggnet.dwoss.mandator.api.value.Ledger;
import eu.ggnet.dwoss.mandator.api.value.PostLedger;
import eu.ggnet.dwoss.mandator.api.value.ReceiptCustomers;
import eu.ggnet.dwoss.mandator.api.value.SpecialSystemCustomers;

import javax.annotation.ManagedBean;
import javax.enterprise.inject.Produces;

import eu.ggnet.dwoss.core.common.values.DocumentType;
import eu.ggnet.dwoss.core.system.util.MapBuilder;

import static eu.ggnet.dwoss.core.common.values.PositionType.*;
import static eu.ggnet.dwoss.core.common.values.ReceiptOperation.*;
import static eu.ggnet.dwoss.core.common.values.TaxType.*;
import static eu.ggnet.dwoss.core.common.values.tradename.TradeName.*;

/**
 *
 * @author oliver.guenther
 */
@ManagedBean
public class MandatorSupportStub {

    private final static Ledger L_8401_SONDERPOSTEN_UNIT = new Ledger(8401, "Sonderposten Geräte");

    public final static Ledger L_8415_HW_SW_STORE = new Ledger(8415, "Hardware/Software/Kleinteile Store");

    private final static Ledger L_8406_VERSANDKOSTEN = new Ledger(8406, "Versandkosten");

    public final static Ledger L_8418_ACER_TWO_YEAR_POST_LEDGER = new Ledger(8418, "Acer Garantieerweiterung");

    public final static Ledger L_8403_DL_TECHNIK = new Ledger(8403, "Dientsleistung Technik");

    public final static Ledger L_8404_ACER_REPARATUR = new Ledger(8404, "Acer Reparatur Abrechnung");

    @Produces
    public static ReceiptCustomers c = ReceiptCustomers.builder()
            .put(ACER, REPAIR, 3)
            .put(ACER, MISSING_PARTS, 4)
            .put(ACER, INTERNAL_REWORK, 5)
            .put(OTTO, REPAIR, 15)
            .put(OTTO, MISSING_PARTS, 17)
            .put(OTTO, INTERNAL_REWORK, 18)
            .put(ONESELF, REPAIR, 21)
            .put(ONESELF, MISSING_PARTS, 22)
            .put(ONESELF, INTERNAL_REWORK, 23)
            .build();

    @Produces
    public static SpecialSystemCustomers sc = new SpecialSystemCustomers(new MapBuilder<Long, DocumentType>()
            .put(34L, DocumentType.CAPITAL_ASSET).put(40L, DocumentType.RETURNS).toHashMap());

    @Produces
    public static PostLedger pl = new PostLedger(
            PostLedger.add()
                    .positionTypes(UNIT)
                    .taxTypes(GENERAL_SALES_TAX_DE_SINCE_2007)
                    .primaryLedger(L_8401_SONDERPOSTEN_UNIT),
            PostLedger.add()
                    .positionTypes(UNIT)
                    .taxTypes(UNTAXED)
                    .primaryLedger(8431, "GGNet Sonderposten Geräte ohne Ust."),
            PostLedger.add()
                    .positionTypes(UNIT, SERVICE, PRODUCT_BATCH, SHIPPING_COST, UNIT_ANNEX)
                    .taxTypes(REVERSE_CHARGE)
                    .primaryLedger(8337, "Reverse Charge"),
            PostLedger.add()
                    .positionTypes(SERVICE)
                    .taxTypes(GENERAL_SALES_TAX_DE_SINCE_2007)
                    .primaryLedger(8414, "Dienstleistung Store")
                    .alternativeLedger(L_8401_SONDERPOSTEN_UNIT)
                    .alternativeLedger(L_8403_DL_TECHNIK)
                    .alternativeLedger(L_8404_ACER_REPARATUR)
                    .alternativeLedger(L_8406_VERSANDKOSTEN)
                    .alternativeLedger(L_8415_HW_SW_STORE),
            PostLedger.add()
                    .positionTypes(PRODUCT_BATCH)
                    .taxTypes(GENERAL_SALES_TAX_DE_SINCE_2007)
                    .primaryLedger(L_8415_HW_SW_STORE)
                    .alternativeLedger(L_8418_ACER_TWO_YEAR_POST_LEDGER),
            PostLedger.add()
                    .positionTypes(SHIPPING_COST)
                    .taxTypes(GENERAL_SALES_TAX_DE_SINCE_2007)
                    .primaryLedger(L_8406_VERSANDKOSTEN)
    );

}
