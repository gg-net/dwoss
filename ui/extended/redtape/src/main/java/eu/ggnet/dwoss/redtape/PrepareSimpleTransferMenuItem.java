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
package eu.ggnet.dwoss.redtape;

import java.util.*;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import org.openide.util.Lookup;

import static javax.swing.JOptionPane.*;

import eu.ggnet.saft.core.Workspace;
import eu.ggnet.saft.core.authorisation.Guardian;

import eu.ggnet.saft.api.Accessable;

import eu.ggnet.dwoss.redtape.entity.Position;

import eu.ggnet.dwoss.rights.api.AtomicRight;

import eu.ggnet.dwoss.stock.StockAgent;
import eu.ggnet.dwoss.stock.StockTransactionProcessor;
import eu.ggnet.dwoss.stock.entity.Stock;
import eu.ggnet.dwoss.stock.entity.StockUnit;

import eu.ggnet.dwoss.util.UserInfoException;

import eu.ggnet.dwoss.common.ExceptionUtil;

import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionModel;

import static eu.ggnet.saft.core.Client.lookup;
import static eu.ggnet.dwoss.rights.api.AtomicRight.CREATE_TRANSACTION_FOR_SINGLE_UNIT;
import static eu.ggnet.dwoss.rules.PositionType.UNIT;

/**
 *
 * @author Bastian Venz <bastian.venz at gg-net.de>
 */
public class PrepareSimpleTransferMenuItem extends MenuItem implements Accessable {

    public PrepareSimpleTransferMenuItem(Stock stock, SelectionModel<Position> selectionModel) {
        super("Umfuhr nach " + stock.getName());
        selectionModel.selectedIndexProperty().addListener((o) -> {
            if ( selectionModel.getSelectedItem() == null ) return;
            if ( selectionModel.getSelectedItem().getType() == UNIT ) this.setDisable(false);
            else this.setDisable(true);
        });

        setOnAction((e) -> {
            try {
                final StockUnit stockUnit = lookup(StockAgent.class).findStockUnitByUniqueUnitIdEager(selectionModel.getSelectedItem().getUniqueUnitId());
                if ( stockUnit == null )
                    throw new UserInfoException("SopoNr existiert im Lager nicht");
                if ( stockUnit.isInTransaction() )
                    throw new UserInfoException("SopoNr ist auf einer Transaction");
                if ( stockUnit.getStock().equals(stock) )
                    throw new UserInfoException("Gerät ist schon in diesem Lager");
                final List<StockUnit> stockUnits = Arrays.asList(stockUnit);
                String msg = "";
                msg += "SopoNr " + stockUnit.getRefurbishId() + " - " + stockUnit.getName() + " von " + stockUnit.getStock().getName() + "\n";
                if ( YES_OPTION != showConfirmDialog(lookup(Workspace.class).getMainFrame(), msg, "Umfuhr(en) auslösen ?", YES_NO_OPTION, QUESTION_MESSAGE) )
                    return;

                new SwingWorker<Void, Object>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        lookup(StockTransactionProcessor.class).perpareTransfer(stockUnits, stock.getId(), Lookup.getDefault().lookup(Guardian.class).getUsername(),
                                "Umfuhr direkt durch Nutzer erzeugt");
                        return null;
                    }

                    @Override
                    protected void done() {
                        try {
                            get();
                        } catch (InterruptedException | ExecutionException ex) {
                            ExceptionUtil.show(lookup(Workspace.class).getMainFrame(), ex);
                        }
                    }
                }.execute();
            } catch (UserInfoException ex) {
                ExceptionUtil.show(lookup(Workspace.class).getMainFrame(), ex);
            }
        });
    }

    @Override
    public void setEnabled(boolean enable) {
        this.setDisable(!enable);
    }

    @Override
    public AtomicRight getNeededRight() {
        return CREATE_TRANSACTION_FOR_SINGLE_UNIT;
    }

    /**
     * Util Factory for a Fx Menu.
     * <p>
     * @param selectionModel
     * @param allStocks
     * @return
     */
    public static List<MenuItem> asFxMenuItems(final SelectionModel<Position> selectionModel, List<Stock> allStocks) {
        List<MenuItem> items = new ArrayList<>();
        Guardian accessCos = lookup(Guardian.class);
        for (Stock allStock : allStocks) {
            PrepareSimpleTransferMenuItem item = new PrepareSimpleTransferMenuItem(allStock, selectionModel);
            accessCos.add(item);
            items.add(item);
        }
        return items;
    }

}
