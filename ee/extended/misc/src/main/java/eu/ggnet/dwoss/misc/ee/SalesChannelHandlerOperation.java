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
package eu.ggnet.dwoss.misc.ee;

import java.util.Map.Entry;
import java.util.*;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.progress.MonitorFactory;
import eu.ggnet.dwoss.progress.SubMonitor;
import eu.ggnet.dwoss.stock.ee.assist.Stocks;
import eu.ggnet.dwoss.stock.ee.eao.StockUnitEao;
import eu.ggnet.dwoss.stock.ee.emo.StockTransactionEmo;
import eu.ggnet.dwoss.stock.ee.emo.Transfer;
import eu.ggnet.dwoss.stock.ee.entity.Stock;
import eu.ggnet.dwoss.stock.ee.entity.StockUnit;
import eu.ggnet.dwoss.stock.ee.model.SalesChannelLine;
import eu.ggnet.dwoss.uniqueunit.ee.assist.UniqueUnits;
import eu.ggnet.dwoss.uniqueunit.ee.eao.UniqueUnitEao;
import eu.ggnet.dwoss.uniqueunit.ee.entity.UniqueUnit;
import eu.ggnet.dwoss.uniqueunit.ee.entity.UniqueUnit.Equipment;
import eu.ggnet.dwoss.uniqueunit.ee.entity.UniqueUnit.Identifier;
import eu.ggnet.dwoss.uniqueunit.ee.format.ProductFormater;
import eu.ggnet.dwoss.core.common.UserInfoException;

import static eu.ggnet.dwoss.uniqueunit.ee.entity.PriceType.CUSTOMER;
import static eu.ggnet.dwoss.uniqueunit.ee.entity.PriceType.RETAILER;

@Stateless
public class SalesChannelHandlerOperation implements SalesChannelHandler {

    public static class LastCharsRefurbishIdSorter implements Comparator<SalesChannelLine> {
        // Sorts by last Character

        @Override
        public int compare(SalesChannelLine s1, SalesChannelLine s2) {
            String o1 = s1.getRefurbishedId();
            String o2 = s2.getRefurbishedId();
            if ( o1.length() < 3 || o2.length() < 3 ) return o1.compareTo(o2);
            return o1.substring(o1.length() - 2).compareTo(o2.substring(o2.length() - 2));
        }
    }

    private final Logger L = LoggerFactory.getLogger(SalesChannelHandlerOperation.class);

    @Inject
    @Stocks
    private EntityManager stockEm;

    @Inject
    @UniqueUnits
    private EntityManager uuEm;

    @Inject
    private MonitorFactory monitorFactory;

    /**
     * Returns all units, which are in a stock. Units which are on a transaction, are not displayed.
     * <p>
     * TODO: Turn it around. Fist take all StockUnits, which are not in a transaction. Then filter them against the SopoUnit information
     * <p/>
     * @return all units, which are in a stock
     */
    @Override
    public List<SalesChannelLine> findAvailableUnits() {
        SubMonitor m = monitorFactory.newSubMonitor("Verkaufskanalmanager vorbereiten", 100);
        m.setLogger(L);
        m.start();
        final UniqueUnitEao uniqueUnitService = new UniqueUnitEao(uuEm);
        List<SalesChannelLine> lines = new ArrayList<>();
        m.message("Loading all available units.");
        List<StockUnit> stockUnits = new StockUnitEao(stockEm).findByNoTransaction();
        m.worked(10);
        m.setWorkRemaining(stockUnits.size() + 5);
        for (StockUnit stockUnit : stockUnits) {
            m.worked(1, "Handling SopoNr " + stockUnit.getRefurbishId());
            UniqueUnit uniqueUnit = uniqueUnitService.findById(stockUnit.getUniqueUnitId());
            if ( uniqueUnit == null ) throw new RuntimeException(
                        "StockUnit(id=" + stockUnit.getId() + ",uniqueUnitId=" + stockUnit.getUniqueUnitId() + ") has no uniqueUnit");
            if ( uniqueUnit.getProduct() == null ) L.warn("UniqueUnit(id=" + uniqueUnit.getId() + ").product==null");
            lines.add(
                    new SalesChannelLine(
                            stockUnit.getId(),
                            uniqueUnit.getRefurbishId(),
                            ProductFormater.toName(uniqueUnit.getProduct()),
                            stockUnit.getStock().getName(),
                            uniqueUnit.getPrice(RETAILER),
                            uniqueUnit.getPrice(CUSTOMER),
                            uniqueUnit.getSalesChannel(),
                            uniqueUnit.getSalesChannel(),
                            stockUnit.getStock().getId(),
                            null,
                            "" + ((uniqueUnit.getEquipments().contains(Equipment.ORIGINAL_BOXED) ? "Originalkarton, " : "") + (uniqueUnit.getCondition().getNote()))
            ));
            m.worked(1);
        }
        m.finish();
        return lines;
    }

    /**
     * Updates the salesChanel of all supplied units
     * <p/>
     * @param lines              a list of salesChannelLines, must not be null.
     * @param arranger
     * @param transactionComment
     * @return true if something was changed.
     * @throws UserInfoException
     */
    @Override
    public boolean update(final List<SalesChannelLine> lines, String arranger, String transactionComment) throws UserInfoException {
        SubMonitor m = monitorFactory.newSubMonitor("Import der Verkaufskanäle", 100);
        m.start();
        Map<Stock, List<Integer>> destinationsWithStockUnitIds = lines
                .stream()
                .filter(l -> l.getDestination() != null) // No Destination change
                .sorted(new LastCharsRefurbishIdSorter()) // Sort
                .collect(Collectors.groupingBy(SalesChannelLine::getDestination,
                        Collectors.mapping(SalesChannelLine::getUnitId, Collectors.toList())));
        StockTransactionEmo emo = new StockTransactionEmo(stockEm);

        SortedMap<Integer, String> histories = new TreeMap<>();
        for (Entry<Stock, List<Integer>> entry : destinationsWithStockUnitIds.entrySet()) {
            histories.putAll(emo.prepare(new Transfer.Builder()
                    .destinationStockId(entry.getKey().getId())
                    .addAllStockUnitIds(entry.getValue())
                    .arranger(arranger)
                    .comment(transactionComment)
                    .maxTransactionSize(10)
                    .build(),
                    m));
        }
        m.setWorkRemaining(lines.size());
        UniqueUnitEao uniqueUnitEao = new UniqueUnitEao(uuEm);
        boolean hasChanged = false;

        for (SalesChannelLine line : lines) {
            m.worked(1, "verarbeite " + line.getRefurbishedId());
            if ( !line.hasChanged() ) continue;
            hasChanged = true;
            UniqueUnit uu = uniqueUnitEao.findByIdentifier(Identifier.REFURBISHED_ID, line.getRefurbishedId());
            uu.setSalesChannel(line.getSalesChannel());
            uu.addHistory("SalesChannel set to " + line.getSalesChannel() + " by " + arranger);
            if ( histories.containsKey(uu.getId()) ) uu.addHistory(histories.get(uu.getId()));
        }
        m.finish();
        return hasChanged;
    }
}
