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
package eu.ggnet.dwoss.stock.transactions;

import java.awt.event.ActionEvent;
import java.util.*;

import org.apache.commons.lang3.StringUtils;

import eu.ggnet.dwoss.stock.*;
import eu.ggnet.dwoss.stock.entity.Stock;
import eu.ggnet.dwoss.stock.entity.StockUnit;
import eu.ggnet.dwoss.util.UserInfoException;
import eu.ggnet.saft.core.Ui;
import eu.ggnet.saft.core.authorisation.AccessableAction;
import eu.ggnet.saft.core.authorisation.Guardian;

import static eu.ggnet.dwoss.rights.api.AtomicRight.CREATE_TRANSACTION_FOR_SINGLE_UNIT;
import static eu.ggnet.saft.core.Client.lookup;

/**
 * Creates or uses a Transfer Transaction to move a Unit from one stock to another.
 * <p/>
 * @author oliver.guenther
 */
public class CreateSimpleAction extends AccessableAction {

    public CreateSimpleAction() {
        super(CREATE_TRANSACTION_FOR_SINGLE_UNIT);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Ui.choiceFxml(CreateSelectionController.class)
                .onOk(c -> {
                    if ( StringUtils.isBlank(c.refurbishIds()) ) throw new UserInfoException("Keine SopoNr eingeben");
                    if ( StringUtils.isBlank(c.comment()) ) throw new UserInfoException("Keine Kommentar eingegeben");
                    if ( c.target() == null ) throw new UserInfoException("Kein Ziellager ausgewählt");
                    List<String> refurbishIds = parseRefurbishIds(c.refurbishIds());
                    if ( refurbishIds.isEmpty() ) throw new UserInfoException("Keine SopoNr in " + c.refurbishIds() + " erkannt");
                    List<StockUnit> stockUnits = lookup(StockAgent.class).findStockUnitsByRefurbishIdEager(refurbishIds);
                    if ( stockUnits.isEmpty() ) throw new UserInfoException("Keine der SopoNr(n) " + refurbishIds + " ist im Lager");
                    if ( stockUnits.stream().anyMatch(s -> s.isInTransaction()) )
                        throw new UserInfoException("Mindestens eine SopoNr ist auf einer Transaktion");
                    if ( unmatchingSourceAndDestination(stockUnits) )
                        throw new UserInfoException("Mindestens eine SopoNr ist nicht auf dem selben Ausgangslager");
                    return new CreateQuestionModel(stockUnits.get(0).getStock(), c.target(), stockUnits, c.comment());
                })
                .choiceFx(CreateQuestionView.class)
                .onOk(v -> {
                    lookup(StockTransactionProcessor.class).perpareTransfer(
                            v.model().stockUnits,
                            v.model().destination.getId(),
                            lookup(Guardian.class).getUsername(),
                            v.model().comment);
                    eu.ggnet.saft.core.Alert.show("Umfuhr anglegt.");
                    return null;
                }).exec();
    }

    private List<String> parseRefurbishIds(String rawRefurbishIds) {
        List<String> refurbishIds = new ArrayList<>();
        try (Scanner scanner = new Scanner(rawRefurbishIds)) {
            scanner.useDelimiter("( |,)+");
            while (scanner.hasNext()) {
                refurbishIds.add(scanner.next());
            }
        }
        return refurbishIds;
    }

    private boolean unmatchingSourceAndDestination(List<StockUnit> stockUnits) {
        Stock source = null;
        for (StockUnit stockUnit : stockUnits) {
            if ( source == null ) source = stockUnit.getStock();
            if ( !source.equals(stockUnit.getStock()) ) return true;
        }
        return false;
    }

}