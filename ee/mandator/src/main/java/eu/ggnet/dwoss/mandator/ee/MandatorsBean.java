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
package eu.ggnet.dwoss.mandator.ee;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import eu.ggnet.dwoss.core.common.FileJacket;
import eu.ggnet.dwoss.mandator.api.Mandators;
import eu.ggnet.dwoss.mandator.api.value.*;
import eu.ggnet.dwoss.mandator.api.value.qualifier.CustomerAndOrdersIcon;
import eu.ggnet.dwoss.mandator.api.value.qualifier.DeutscheWarenwirtschaftIcon;

/**
 * Support for the Mandator.
 * <p>
 * @author oliver.guenther
 */
@Named
@Stateless
public class MandatorsBean implements Mandators {

    @Inject
    private Mandator mandator;

    @Inject
    private DefaultCustomerSalesdata salesdata;

    @Inject
    private ReceiptCustomers receiptCustomers;

    @Inject
    private SpecialSystemCustomers specialSystemCustomers;

    @Inject
    private Contractors contractors;

    @Inject
    private PostLedger postLedger;

    @Inject
    @DeutscheWarenwirtschaftIcon
    private FileJacket dwIcon;

    @Inject
    @CustomerAndOrdersIcon
    private FileJacket caoIcon;

    @Override
    public Mandator loadMandator() {
        return mandator;
    }

    @Override
    public DefaultCustomerSalesdata loadSalesdata() {
        return salesdata;
    }

    @Override
    public ReceiptCustomers loadReceiptCustomers() {
        return receiptCustomers;
    }

    @Override
    public SpecialSystemCustomers loadSystemCustomers() {
        return specialSystemCustomers;
    }

    @Override
    public Contractors loadContractors() {
        return contractors;
    }

    @Override
    public PostLedger loadPostLedger() {
        return postLedger;
    }

    @Override
    public FileJacket loadDwIcon() {
        return dwIcon;
    }

    @Override
    public FileJacket loadCaoIcon() {
        return caoIcon;
    }

}
