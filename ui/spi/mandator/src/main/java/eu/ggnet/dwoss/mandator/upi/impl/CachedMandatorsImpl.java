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
package eu.ggnet.dwoss.mandator.upi.impl;

import eu.ggnet.dwoss.mandator.api.value.DefaultCustomerSalesdata;
import eu.ggnet.dwoss.mandator.api.value.PostLedger;
import eu.ggnet.dwoss.mandator.api.value.ReceiptCustomers;
import eu.ggnet.dwoss.mandator.api.value.Mandator;
import eu.ggnet.dwoss.mandator.api.value.Contractors;
import eu.ggnet.dwoss.mandator.api.value.SpecialSystemCustomers;

import org.openide.util.lookup.ServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.mandator.ee.Mandators;
import eu.ggnet.dwoss.mandator.upi.CachedMandators;
import eu.ggnet.saft.core.Dl;

/**
 * Implementation of the Mandators with Cache.
 *
 * @author oliver.guenther
 */
@ServiceProvider(service = CachedMandators.class)
public class CachedMandatorsImpl implements CachedMandators {

    private final static Logger L = LoggerFactory.getLogger(CachedMandatorsImpl.class);

    Mandators service = null;

    @Override
    public Mandator loadMandator() {
        return loadOnce().loadMandator();
    }

    @Override
    public DefaultCustomerSalesdata loadSalesdata() {
        return loadOnce().loadSalesdata();
    }

    @Override
    public ReceiptCustomers loadReceiptCustomers() {
        return loadOnce().loadReceiptCustomers();
    }

    @Override
    public SpecialSystemCustomers loadSystemCustomers() {
        return loadOnce().loadSystemCustomers();
    }

    @Override
    public Contractors loadContractors() {
        return loadOnce().loadContractors();
    }

    @Override
    public PostLedger loadPostLedger() {
        return loadOnce().loadPostLedger();
    }

    private Mandators loadOnce() {
        if ( service == null ) {
            service = CachedProxy.create(Mandators.class, Dl.remote().lookup(Mandators.class));
            L.info("Cache filled once");
        }
        return service;
    }

}
