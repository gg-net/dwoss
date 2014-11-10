package eu.ggnet.dwoss.stock;

import java.awt.EventQueue;
import java.util.concurrent.ExecutionException;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import eu.ggnet.dwoss.stock.StockAgent;
import eu.ggnet.dwoss.stock.StockTransactionProcessor;
import eu.ggnet.dwoss.stock.entity.StockTransactionStatusType;
import eu.ggnet.dwoss.stock.entity.StockTransactionType;
import eu.ggnet.dwoss.stock.entity.StockUnit;

import eu.ggnet.dwoss.util.UserInfoException;

import eu.ggnet.dwoss.common.ExceptionUtil;

import eu.ggnet.dwoss.rights.op.Authentication;

import lombok.RequiredArgsConstructor;
import lombok.Setter;

import static eu.ggnet.saft.core.Client.lookup;

/**
 *
 * @author oliver.guenther
 */
@RequiredArgsConstructor
public class CommissioningManagerController {

    private final StockAgent stockAgent;

    private final StockTransactionProcessor stockRotation;

    private final Authentication authentication;

    @Setter
    private CommissioningManagerModel model;

    @Setter
    private CommissioningManagerView view;

    public CommissioningManagerController() {
        this(lookup(StockAgent.class), lookup(StockTransactionProcessor.class), lookup(Authentication.class));
    }

    public boolean executeTransmutation() {
        if ( !model.isCompleteAble() ) return false;
        if ( model.getStockTransactions().get(0).getStatus().getType() == StockTransactionStatusType.PREPARED ) {
            stockRotation.commission(model.getStockTransactions(), model.getParticipantOneName(), model.getParticipantTwoName());
        } else if ( model.getStockTransactions().get(0).getStatus().getType() == StockTransactionStatusType.IN_TRANSFER ) {
            stockRotation.receive(model.getStockTransactions(), model.getParticipantOneName(), model.getParticipantTwoName());
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
                } catch (UserInfoException | ExecutionException | InterruptedException e) {
                    ExceptionUtil.show(view, e);
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
