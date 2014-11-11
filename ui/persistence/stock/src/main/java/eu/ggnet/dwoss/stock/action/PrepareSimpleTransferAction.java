/* 
 * Copyright (C) 2014 pascal.perau
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
package eu.ggnet.dwoss.stock.action;

import eu.ggnet.dwoss.stock.entity.StockUnit;
import eu.ggnet.dwoss.stock.entity.Stock;
import eu.ggnet.saft.core.authorisation.AccessableAction;
import eu.ggnet.saft.core.authorisation.Guardian;

import java.awt.event.ActionEvent;
import java.util.*;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import org.openide.util.Lookup;

import eu.ggnet.dwoss.stock.StockAgent;
import eu.ggnet.dwoss.stock.StockTransactionProcessor;
import eu.ggnet.saft.core.Workspace;


import eu.ggnet.dwoss.stock.StockTransactionCreatorViewCask;
import eu.ggnet.dwoss.common.ExceptionUtil;

import eu.ggnet.dwoss.util.dialog.Alert;

import static eu.ggnet.saft.core.Client.lookup;
import static eu.ggnet.dwoss.rights.api.AtomicRight.CREATE_TRANSACTION_FOR_SINGLE_UNIT;
import static javax.swing.JOptionPane.*;

/**
 * Creates or uses a Transfer Transaction to move a Unit from one stock to another.
 * <p/>
 * @author oliver.guenther
 */
public class PrepareSimpleTransferAction extends AccessableAction {

    public PrepareSimpleTransferAction() {
        super(CREATE_TRANSACTION_FOR_SINGLE_UNIT);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final StockTransactionCreatorViewCask dialog = new StockTransactionCreatorViewCask(lookup(Workspace.class).getMainFrame());
        dialog.setLocationRelativeTo(lookup(Workspace.class).getMainFrame());
        dialog.setVisible(true);
        if ( !dialog.isOk() ) return;
        String rawRefurbishIds = dialog.getRefurbishIds();
        if ( rawRefurbishIds == null || rawRefurbishIds.trim().equals("") ) return;
        List<String> refurbishIds = new ArrayList<>();
        try (Scanner scanner = new Scanner(rawRefurbishIds)) {
            scanner.useDelimiter("( |,)+");
            while (scanner.hasNextInt()) {
                refurbishIds.add(Integer.toString(scanner.nextInt()));
            }
        }
        if ( refurbishIds.isEmpty() ) return;

        // Find StockUnits by RefurbishIdsEager
        final List<StockUnit> stockUnits = lookup(StockAgent.class).findStockUnitsByRefurbishIdEager(refurbishIds);
        if ( stockUnits.isEmpty() ) {
            showMessageDialog(lookup(Workspace.class).getMainFrame(),
                    "Keine Geräte mit SopoNrn :" + refurbishIds + " gefunden", "Fehler", ERROR_MESSAGE);
            return;
        }
        String msg = "";
        Stock source = null;
        for (StockUnit stockUnit : stockUnits) {
            if ( stockUnit.isInTransaction() ) {
                showMessageDialog(lookup(Workspace.class).getMainFrame(),
                        "SopoNr :" + stockUnit.getRefurbishId() + " ist schon auf einer Transaktion", "Fehler", ERROR_MESSAGE);
                return;
            }
            if ( source == null ) source = stockUnit.getStock();
            if ( !source.equals(stockUnit.getStock()) ) {
                Alert.builder()
                        .parent(lookup(Workspace.class).getMainFrame())
                        .title("Fehler")
                        .body("SopoNr: " + stockUnit.getRefurbishId() + " nicht auf selber Quelle wie alle anderen : " + source)
                        .build().showAsError();
            }
            msg += "SopoNr " + stockUnit.getRefurbishId() + " - " + stockUnit.getName() + " von " + stockUnit.getStock().getName() + "\n";
        }
        if ( YES_OPTION != showConfirmDialog(lookup(Workspace.class).getMainFrame(), msg, "Umfuhr(en) auslösen ?", YES_NO_OPTION, QUESTION_MESSAGE) )
            return;

        new SwingWorker<Void, Object>() {
            @Override
            protected Void doInBackground() throws Exception {
                lookup(StockTransactionProcessor.class).perpareTransfer(stockUnits, dialog.getDestination().getId(), Lookup.getDefault().lookup(Guardian.class).getUsername(),
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
    }
}
