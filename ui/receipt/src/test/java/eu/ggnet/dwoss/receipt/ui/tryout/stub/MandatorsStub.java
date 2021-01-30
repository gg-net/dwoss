/*
 * Copyright (C) 2018 GG-Net GmbH
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
package eu.ggnet.dwoss.receipt.ui.tryout.stub;

import java.util.*;

import eu.ggnet.dwoss.core.common.values.ReceiptOperation;
import eu.ggnet.dwoss.core.common.values.tradename.TradeName;
import eu.ggnet.dwoss.mandator.api.value.ReceiptCustomers.Key;
import eu.ggnet.dwoss.mandator.api.value.*;
import eu.ggnet.dwoss.mandator.spi.CachedMandators;

import static eu.ggnet.dwoss.core.common.values.tradename.TradeName.*;

/**
 *
 * @author oliver.guenther
 */
public class MandatorsStub implements CachedMandators {

    private final static Set<TradeName> CONTRACTORS = EnumSet.of(AMAZON, EBAY, ACER, APPLE, INGRAM_MICRO);

    private final static Set<TradeName> BRANDS = EnumSet.of(APPLE, ACER, PACKARD_BELL, LENOVO, HP);

    private final Map<Key, Long> RECEIPT_CUSTOMERS;

    {
        RECEIPT_CUSTOMERS = new HashMap<>();
        long i = 0;
        for (TradeName contractor : CONTRACTORS) {
            for (ReceiptOperation ro : ReceiptOperation.values()) {
                RECEIPT_CUSTOMERS.put(Key.of(contractor, ro), i);
                i++;
            }
        }
    }

    @Override
    public Mandator loadMandator() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public DefaultCustomerSalesdata loadSalesdata() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ReceiptCustomers loadReceiptCustomers() {
        return new ReceiptCustomers(RECEIPT_CUSTOMERS);
    }

    @Override
    public SpecialSystemCustomers loadSystemCustomers() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Contractors loadContractors() {
        return new Contractors(CONTRACTORS, BRANDS);
    }

    @Override
    public PostLedger loadPostLedger() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
