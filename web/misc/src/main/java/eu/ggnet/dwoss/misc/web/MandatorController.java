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
package eu.ggnet.dwoss.misc.web;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.core.common.values.DocumentType;
import eu.ggnet.dwoss.core.common.values.ShippingCondition;
import eu.ggnet.dwoss.core.common.values.tradename.TradeName;
import eu.ggnet.dwoss.mandator.api.value.ReceiptCustomers.Key;
import eu.ggnet.dwoss.mandator.api.value.*;

/**
 *
 * @author jacob.weinhold
 */
@Named
@ViewScoped
public class MandatorController implements Serializable {

    private final static Logger LOG = LoggerFactory.getLogger(MandatorController.class);

    @Inject
    private Mandator mandator;

    @Inject
    private Contractors contractors;

    @Inject
    private SpecialSystemCustomers specialSystemCustomers;

    @Inject
    private ReceiptCustomers receiptCustomers;

    @Inject
    private PostLedger postLedger;

    public List<TradeName> getAllowedBrands() {
        return contractors.allowedBrands().stream().collect(Collectors.toList());
    }

    public List<TradeName> getAllContractors() {
        return contractors.all().stream().collect(Collectors.toList());
    }

    // TODO: also show the default of mandator (DefaultSalesData)
    public List<ShippingCondition> getShippingConditions() {
        return Arrays.asList(ShippingCondition.values());
    }

    public List<Map.Entry<Long, DocumentType>> getSpecialSystemCustomers() {
        return new ArrayList<>(specialSystemCustomers.getSpecialCustomers().entrySet());

    }

    public List<Map.Entry<Key, Long>> getReceiptCustomers() {
        return new ArrayList<>(receiptCustomers.getReceiptCustomers().entrySet());
    }

    public int sortReceiptCustomers(Map.Entry<Key, Long> obj, Map.Entry<Key, Long> other) {
        return obj.getValue().compareTo(other.getValue());

    }

    public PostLedger getPostLedger() {
        return postLedger;
    }

    public Mandator getMandator() {
        return mandator;
    }

}
