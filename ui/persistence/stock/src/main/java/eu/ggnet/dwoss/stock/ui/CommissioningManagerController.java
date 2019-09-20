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
package eu.ggnet.dwoss.stock.ui;

import java.awt.EventQueue;
import java.util.concurrent.ExecutionException;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import eu.ggnet.dwoss.rights.ee.op.Authentication;
import eu.ggnet.dwoss.stock.ee.StockAgent;
import eu.ggnet.dwoss.stock.ee.StockTransactionProcessor;
import eu.ggnet.dwoss.stock.ee.entity.*;
import eu.ggnet.dwoss.util.UserInfoException;
import eu.ggnet.saft.core.Dl;
import eu.ggnet.saft.core.Ui;

/**
 *
 * @author oliver.guenther
 */
public class CommissioningManagerController {

    private final StockAgent stockAgent;

    private final StockTransactionProcessor stp;

    private final Authentication authentication;

    private CommissioningManagerModel model;

    private CommissioningManagerView view;

    public CommissioningManagerController(StockAgent stockAgent, StockTransactionProcessor stp, Authentication authentication) {
        this.stockAgent = stockAgent;
        this.stp = stp;
        this.authentication = authentication;
    }

    public CommissioningManagerController() {
        this(Dl.remote().lookup(StockAgent.class), Dl.remote().lookup(StockTransactionProcessor.class), Dl.remote().lookup(Authentication.class));
    }

    
    
    public void setModel(CommissioningManagerModel model) {
        this.model = model;
    }

    public void setView(CommissioningManagerView view) {
        this.view = view;
    }

    
    
    public boolean executeTransmutation() {
        if ( !model.isCompleteAble() ) return false;
        if ( model.getStockTransactions().get(0).getStatus().getType() == StockTransactionStatusType.PREPARED ) {
            stp.commission(model.getStockTransactions(), model.getParticipantOneName(), model.getParticipantTwoName());
        } else if ( model.getStockTransactions().get(0).getStatus().getType() == StockTransactionStatusType.IN_TRANSFER ) {
            stp.receive(model.getStockTransactions(), model.getParticipantOneName(), model.getParticipantTwoName());
        } else {
            throw new RuntimeException("Status of first Transaction does not make sense : " + model.getStockTransactions().get(0).getStatus().getType());
        }
        return true;
    }

    void authenticateUserOne() {
        UserPassViewCask authDialog = new UserPassViewCask(view);
        authDialog.setVisible(true);
        if ( !authDialog.isOk() ) return;
        try {
            authentication.login(authDialog.getUsername(), authDialog.getPassword());
            model.setParticipantOneName(authDialog.getUsername());
            model.setParticipantOneAuthenticated(true);
            view.done1Button.setEnabled(false);
        } catch (UserInfoException ex) {
            showError("Username oder Passwort falsch : " + ex.getMessage());
        }
    }

    void authenticateUserTwo() {
        UserPassViewCask authDialog = new UserPassViewCask(view);
        authDialog.setVisible(true);
        if ( !authDialog.isOk() ) return;
        try {
            authentication.login(authDialog.getUsername(), authDialog.getPassword());
            model.setParticipantTwoName(authDialog.getUsername());
            model.setParticipantTwoAuthenticated(true);
            view.done2Button.setEnabled(false);
        } catch (UserInfoException ex) {
            showError("Username oder Passwort falsch : " + ex.getMessage());
        }
    }

    public void addUnit(final String refurbishId) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                view.unitIdTextField.setText(refurbishId + " <- suche...");
                view.unitIdTextField.setEnabled(false);
            }
        });
        new SwingWorker<StockUnit, Object>() {
            @Override
            protected StockUnit doInBackground() throws Exception {
                return stockAgent.findStockUnitByRefurbishIdEager(refurbishId);
            }

            @Override
            protected void done() {
                try {
                    StockUnit su = get();
                    if ( !valid(su, refurbishId) ) return;
                    model.addUnit(su);
                } catch (UserInfoException | ExecutionException | InterruptedException ex) {
                    Ui.handle(ex);
                } finally {
                    view.unitIdTextField.setEnabled(true);
                    view.unitIdTextField.setText("");
                    view.unitIdTextField.requestFocus();
                }
            }
        }.execute();
    }

    private boolean valid(StockUnit su, String refurbishId) {
        if ( su == null ) return showError("SopoNr " + refurbishId + " ist nicht vorhanden.");
        if ( model.getStockUnits().contains(su) ) return showWarning("SopoNr: " + refurbishId + " allready in List");
        if ( !su.isInTransaction() ) return showError("SopoNr: " + refurbishId + " ist in keiner Transaktion. \n\nDetails:" + su);
        if ( su.getTransaction().getType() != StockTransactionType.TRANSFER ) {
            return showError("SopoNr: " + refurbishId + " ist auf einer Transaktion vom falschen Typ (nicht Transfer).\n\n"
                    + "Details: TransactionType=" + su.getTransaction().getType());
        }
        return true;
    }

    private boolean showError(String error) {
        model.setStatusMessage(error);
        JOptionPane.showMessageDialog(view, error, "Fehler", JOptionPane.ERROR_MESSAGE);
        return false;
    }

    private boolean showWarning(String warn) {
        model.setStatusMessage(warn);
        return false;
    }
}
