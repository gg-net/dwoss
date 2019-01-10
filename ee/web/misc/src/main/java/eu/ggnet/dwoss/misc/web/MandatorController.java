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

import javax.annotation.PostConstruct;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.common.api.values.*;
import eu.ggnet.dwoss.mandator.api.value.*;
import eu.ggnet.dwoss.mandator.api.value.ReceiptCustomers.Key;

import lombok.Getter;

/**
 *
 * @author jacob.weinhold
 */
@Named
@ViewScoped
public class MandatorController implements Serializable {

    private final static Logger LOG = LoggerFactory.getLogger(MandatorController.class);

    @Getter
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

    @Getter
    private TreeNode root;

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

    @PostConstruct
    public void init() {
        root = new DefaultTreeNode("Root", null);

        Map<PositionType, TreeNode> levelOneNodes = new HashMap<>();
        Map<PositionType, Map<TaxType, TreeNode>> levelTwoNodes = new HashMap<>();

        for (PositionType posType : PositionType.values()) {
            for (TaxType taxType : TaxType.values()) {

                Optional<Ledger> opt = postLedger.get(posType, taxType);

                if ( opt.isPresent() ) {

                    LOG.debug("Tax" + taxType + "  Pos: "
                            + posType + "  Ledger " + opt.get());

                    if ( !levelOneNodes.containsKey(posType) )
                        levelOneNodes.put(posType, new DefaultTreeNode(posType.toString(), root));

                    if ( !levelTwoNodes.containsKey(posType) ) {
                        levelTwoNodes.put(posType, new EnumMap<>(TaxType.class));
                        levelTwoNodes.get(posType).put(taxType, new DefaultTreeNode(taxType, levelOneNodes.get(posType)));
                    } else if ( !levelTwoNodes.get(posType).containsKey(taxType) ) {
                        levelTwoNodes.get(posType).put(taxType, new DefaultTreeNode(taxType, levelOneNodes.get(posType)));
                    }

                    levelTwoNodes.get(posType).get(taxType).getChildren().add(new DefaultTreeNode(opt.get()));
                }

            }
        }
    }

}
