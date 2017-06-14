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
package eu.ggnet.dwoss.redtape.gen;

import java.util.*;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.configuration.GlobalConfig;
import eu.ggnet.dwoss.customer.api.CustomerMetaData;
import eu.ggnet.dwoss.customer.op.CustomerServiceBean;
import eu.ggnet.dwoss.mandator.api.value.PostLedger;
import eu.ggnet.dwoss.progress.MonitorFactory;
import eu.ggnet.dwoss.progress.SubMonitor;
import eu.ggnet.dwoss.redtape.RedTapeWorker;
import eu.ggnet.dwoss.redtape.entity.*;
import eu.ggnet.dwoss.redtape.state.CustomerDocument;
import eu.ggnet.dwoss.redtape.state.RedTapeStateTransition;
import eu.ggnet.dwoss.rules.DocumentType;
import eu.ggnet.dwoss.rules.PositionType;
import eu.ggnet.dwoss.stock.StockAgent;
import eu.ggnet.dwoss.stock.entity.StockUnit;
import eu.ggnet.dwoss.uniqueunit.UniqueUnitAgent;
import eu.ggnet.dwoss.uniqueunit.entity.*;
import eu.ggnet.dwoss.uniqueunit.format.UniqueUnitFormater;
import eu.ggnet.dwoss.util.MathUtil;
import eu.ggnet.dwoss.util.validation.ValidationUtil;
import eu.ggnet.statemachine.StateTransition;

import static eu.ggnet.dwoss.rules.CustomerFlag.SYSTEM_CUSTOMER;
import static eu.ggnet.dwoss.uniqueunit.entity.UniqueUnit.Identifier.REFURBISHED_ID;
import static java.util.stream.Collectors.toList;

/**
 *
 * @author oliver.guenther
 */
@Stateless
public class RedTapeGeneratorOperation {

    private final static Random R = new Random();

    private final static Logger LOG = LoggerFactory.getLogger(RedTapeGeneratorOperation.class);

    @EJB
    private RedTapeWorker redTapeWorker;

    @EJB
    private UniqueUnitAgent uniqueUnitAgent;

    @EJB
    private StockAgent stockAgent;

    @Inject
    private MonitorFactory monitorFactory;

    @Inject
    private CustomerServiceBean customerService;

    @Inject
    private PostLedger postLedger;

    /**
     * Generates a random amount of dossiers in a random valid state using already persisted elements like available units and product batches.
     * <p/>
     * @param amount
     * @return the list of generated dossiers.
     */
    // TODO: Some usefull repayments would be nice.
    public List<Dossier> makeSalesDossiers(int amount) {
        SubMonitor m = monitorFactory.newSubMonitor("Erzeuge " + amount + " Dossiers", amount);
        m.start();
        if ( amount < 1 ) return Collections.EMPTY_LIST;

        List<CustomerMetaData> customers = customerService.allAsCustomerMetaData()
                .stream().filter(c -> !c.getFlags().contains(SYSTEM_CUSTOMER)).collect(toList());
        if ( customers.isEmpty() ) throw new RuntimeException("No Customers found, obviously there are non in the database");

        List<UniqueUnit> freeUniqueUnits = uniqueUnitAgent.findAllEager(UniqueUnit.class);
        List<Product> products = uniqueUnitAgent.findAllEager(Product.class);

        List<Dossier> dossiers = new ArrayList<>();
        for (int i = 0; i <= amount; i++) {
            CustomerMetaData customer = customers.get(R.nextInt(customers.size()));
            // Create a dossier on a random customer.
            Dossier dos = redTapeWorker.create(customer.getId(), R.nextBoolean(), "Generated by RedTapeGeneratorOperation.makeSalesDossiers()");
            Document doc = dos.getActiveDocuments(DocumentType.ORDER).get(0);
            int noOfPositions = R.nextInt(10) + 2; // At least two positions.
            Set<Long> productIds = new HashSet<>();
            for (int j = 0; j < noOfPositions; j++) {
                // Add Some units, but make sure, not only units are added.
                if ( j < (noOfPositions - 2) && !freeUniqueUnits.isEmpty() ) {
                    UniqueUnit uu = null;
                    while (uu == null && !freeUniqueUnits.isEmpty()) {
                        uu = freeUniqueUnits.remove(0);
                        StockUnit su = stockAgent.findStockUnitByUniqueUnitIdEager(uu.getId());
                        if ( su == null || su.getLogicTransaction() != null ) uu = null; // Saftynet, so no unit is set double.
                    }
                    if ( uu == null ) continue;
                    double price = uu.getPrice(PriceType.CUSTOMER);
                    if ( price < 0.001 ) price = uu.getPrice(PriceType.RETAILER);
                    if ( price < 0.001 ) price = 1111.11;
                    Position pos = Position.builder()
                            .amount(1)
                            .type(PositionType.UNIT)
                            .uniqueUnitId(uu.getId())
                            .uniqueUnitProductId(uu.getProduct().getId())
                            .price(price)
                            .tax(GlobalConfig.TAX)
                            .afterTaxPrice(MathUtil.roundedApply(price, GlobalConfig.TAX, 0.))
                            .description(UniqueUnitFormater.toDetailedDiscriptionLine(uu))
                            .name(UniqueUnitFormater.toPositionName(uu))
                            .bookingAccount(-1)
                            .refurbishedId(uu.getIdentifier(REFURBISHED_ID))
                            .build();
                    doc.append(pos);
                    continue;
                }
                double price = (R.nextInt(100000) + 100) / 100;
                switch (R.nextInt(3)) { // Add a random position
                    case 0: // Add a Product Batch
                        Product p;
                        int k = 0;
                        do {
                            p = products.get(R.nextInt(products.size()));
                            k++;
                            if ( k > 10 )
                                throw new RuntimeException("Could find a alternative product : p.size=" + products.size() + ", pids.size=" + productIds.size());
                        } while (productIds.contains(p.getId()));
                        productIds.add(p.getId());
                        doc.append(Position.builder()
                                .amount(R.nextInt(10) + 1)
                                .type(PositionType.PRODUCT_BATCH)
                                .uniqueUnitProductId(p.getId())
                                .price(price)
                                .tax(GlobalConfig.TAX)
                                .afterTaxPrice(MathUtil.roundedApply(price, GlobalConfig.TAX, 0.))
                                .name(p.getName())
                                .description(p.getDescription())
                                .bookingAccount(postLedger.get(PositionType.PRODUCT_BATCH).orElse(-1))
                                .build());
                        break;
                    case 1: // Add a Service
                        doc.append(Position.builder()
                                .amount((R.nextInt(100) + 1) / 4.0)
                                .type(PositionType.SERVICE)
                                .price(price)
                                .tax(GlobalConfig.TAX)
                                .afterTaxPrice(MathUtil.roundedApply(price, GlobalConfig.TAX, 0.))
                                .name("Service")
                                .description("Service")
                                .bookingAccount(postLedger.get(PositionType.SERVICE).orElse(-1))
                                .build());
                        break;
                    case 2: // Add a comment
                        doc.append(Position.builder()
                                .amount(1)
                                .type(PositionType.COMMENT)
                                .name("Comment")
                                .description("Comment")
                                .bookingAccount(postLedger.get(PositionType.COMMENT).orElse(-1))
                                .build());
                        break;
                }
            }

            if ( dos.isDispatch() ) { // add the shipping costs.
                double price = (R.nextInt(10) + 1) * 10;
                doc.append(Position.builder()
                        .amount(1)
                        .type(PositionType.SHIPPING_COST)
                        .price(price)
                        .tax(GlobalConfig.TAX)
                        .afterTaxPrice(MathUtil.roundedApply(price, GlobalConfig.TAX, 0.))
                        .name("Versandkosten")
                        .description("Versandkosten")
                        .bookingAccount(postLedger.get(PositionType.SHIPPING_COST).orElse(-1))
                        .build());
            }
            // Break, if what we build is wrong.
            ValidationUtil.validate(doc);
            LOG.info("Preupdate document.id={}", doc.getId());
            doc = redTapeWorker.update(doc, null, "JUnit");

            for (int j = 0; j <= R.nextInt(4); j++) {
                CustomerDocument cd = new CustomerDocument(customer.getFlags(), doc, customer.getShippingCondition(), customer.getPaymentMethod());
                List<StateTransition<CustomerDocument>> transitions = redTapeWorker.getPossibleTransitions(cd);
                if ( transitions.isEmpty() ) break;
                RedTapeStateTransition transition = (RedTapeStateTransition)transitions.get(R.nextInt(transitions.size()));
                if ( transition.getHints().contains(RedTapeStateTransition.Hint.CREATES_ANNULATION_INVOICE)
                        || transition.getHints().contains(RedTapeStateTransition.Hint.CREATES_CREDIT_MEMO) ) break;
                doc = redTapeWorker.stateChange(cd, transition, "JUnit");
            }
            dossiers.add(doc.getDossier());
            m.worked(1, doc.getDossier().getIdentifier());
        }
        m.finish();
        return dossiers;
    }
}
