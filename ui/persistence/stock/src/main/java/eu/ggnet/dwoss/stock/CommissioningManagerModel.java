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
package eu.ggnet.dwoss.stock;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.VetoableChangeListener;
import java.beans.VetoableChangeSupport;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.AbstractListModel;
import javax.swing.ListModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.stock.entity.StockTransaction;
import eu.ggnet.dwoss.stock.entity.StockTransactionStatusType;
import eu.ggnet.dwoss.stock.entity.StockUnit;

import eu.ggnet.dwoss.util.UserInfoException;

public class CommissioningManagerModel {

    private static class FireAbleListModel extends AbstractListModel {

        private List<?> elements;

        public FireAbleListModel(List<?> elements) {
            this.elements = elements;
        }

        public void fireChange() {
            fireContentsChanged(this, 0, elements.size());
        }

        @Override
        public int getSize() {
            return elements.size();
        }

        @Override
        public Object getElementAt(int index) {
            return elements.get(index);
        }
    };

    private static final Logger L = LoggerFactory.getLogger(CommissioningManagerModel.class);

    public static final String PROP_STATUSMESSAGE = "statusMessage";

    public static final String PROP_COMPLETE = "complete";

    public static final String PROP_PARTICIPANT_ONE = "participantOne";

    public static final String PROP_PARTICIPANT_TWO = "participantTwo";

    public static final String PROP_PARTICIPANT_ONE_AUTHENTICATED = "participantOneAuthenticated";

    public static final String PROP_PARTICIPANT_TWO_AUTHENTICATED = "participantTwoAuthenticated";

    public static final String PROP_FULL = "full";

    public static final String PROP_PARTICIPANT_ONE_NAME = "participantOneName";

    public static final String PROP_PARTICIPANT_TWO_NAME = "participantTwoName";

    public static final String PROP_COMPLETEABLE = "completeAble";

    private Set<StockUnit> missingStockUnits = new HashSet<>();

    private List<StockUnit> stockUnits = new ArrayList<>();

    private List<StockTransaction> stockTransactions = new ArrayList<>();

    private FireAbleListModel unitModel = new FireAbleListModel(stockUnits);

    private FireAbleListModel transactionModel = new FireAbleListModel(stockTransactions);

    private String statusMessage = "";

    private VetoableChangeSupport vetoableChangeSupport = new VetoableChangeSupport(this);

    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    private boolean complete = false;

    private String participantOne = "Person 1";

    private String participantTwo = "Person 2";

    private boolean participantOneAuthenticated = false;

    private boolean participantTwoAuthenticated = false;

    private boolean full = false;

    private String participantOneName = "";

    private String participantTwoName = "";

    private boolean completeAble = false;

    private StockTransaction referenceTransaction;

    void addUnit(StockUnit su) throws UserInfoException {
        if ( stockTransactions.isEmpty() ) {
            referenceTransaction = su.getTransaction();
            stockTransactions.add(referenceTransaction);
            missingStockUnits.addAll(referenceTransaction.getUnits());
            if ( su.getTransaction().getStatus().getType() == StockTransactionStatusType.PREPARED ) {
                setParticipantOne("Kommisionierer");
                setParticipantTwo("Transporter");
            } else if ( su.getTransaction().getStatus().getType() == StockTransactionStatusType.IN_TRANSFER ) {
                setParticipantOne("Transporter");
                setParticipantTwo("Warenannehmer");
            }
        } else if ( !stockTransactions.contains(su.getTransaction()) ) {
            String msg = StockTransactionUtil.equalStateMessage(referenceTransaction, su.getTransaction());
            if ( msg != null ) {
                setStatusMessage(msg);
                throw new UserInfoException(msg);
            }
            stockTransactions.add(su.getTransaction());
            missingStockUnits.addAll(su.getTransaction().getUnits());
        }
        setStatusMessage("SopoNr " + su.getRefurbishId() + " hinzugefügt");
        missingStockUnits.remove(su);
        stockUnits.add(su);
        unitModel.fireChange();
        transactionModel.fireChange();

        boolean c = true;
        for (StockTransaction st : stockTransactions) {
            if ( !stockUnits.containsAll(st.getUnits()) ) {
                c = false;
            }
        }
        if ( c ) {
            setFull(true);
        } else {
            reset(); // To be sure, that everything is reseted
        }
    }

    /**
     * Get the value of completeAble
     *
     * @return the value of completeAble
     */
    public boolean isCompleteAble() {
        return completeAble;
    }

    /**
     * Set the value of completeAble
     *
     * @param completeAble new value of completeAble
     */
    public void setCompleteAble(boolean completeAble) {
        boolean oldCompleteAble = this.completeAble;
        this.completeAble = completeAble;
        propertyChangeSupport.firePropertyChange(PROP_COMPLETEABLE, oldCompleteAble, completeAble);
    }

    /**
     * Get the value of participantTwoName
     *
     * @return the value of participantTwoName
     */
    public String getParticipantTwoName() {
        return participantTwoName;
    }

    /**
     * Set the value of participantTwoName
     *
     * @param participantTwoName new value of participantTwoName
     */
    public void setParticipantTwoName(String participantTwoName) {
        String oldParticipantTwoName = this.participantTwoName;
        this.participantTwoName = participantTwoName;
        propertyChangeSupport.firePropertyChange(PROP_PARTICIPANT_TWO_NAME, oldParticipantTwoName, participantTwoName);
    }

    /**
     * Get the value of participantOneName
     *
     * @return the value of participantOneName
     */
    public String getParticipantOneName() {
        return participantOneName;
    }

    /**
     * Set the value of participantOneName
     *
     * @param participantOneName new value of participantOneName
     */
    public void setParticipantOneName(String participantOneName) {
        String oldParticipantOneName = this.participantOneName;
        this.participantOneName = participantOneName;
        propertyChangeSupport.firePropertyChange(PROP_PARTICIPANT_ONE_NAME, oldParticipantOneName, participantOneName);
    }

    /**
     * Get the value of full
     *
     * @return the value of full
     */
    public boolean isFull() {
        return full;
    }

    /**
     * Set the value of full
     *
     * @param full new value of full
     */
    public void setFull(boolean full) {
        boolean oldFull = this.full;
        this.full = full;
        propertyChangeSupport.firePropertyChange(PROP_FULL, oldFull, full);
    }

    private void reset() {
        setFull(false);
        setParticipantOneAuthenticated(false);
        setParticipantTwoAuthenticated(false);
        setComplete(false);
        setCompleteAble(false);
    }

    /**
     * Get the value of participantOneAuthenticated
     *
     * @return the value of participantOneAuthenticated
     */
    public boolean isParticipantOneAuthenticated() {
        return participantOneAuthenticated;
    }

    /**
     * Set the value of participantOneAuthenticated
     *
     * @param participantOneAuthenticated new value of participantOneAuthenticated
     */
    public void setParticipantOneAuthenticated(boolean participantOneAuthenticated) {
        boolean oldParticipantOneAuthenticated = this.participantOneAuthenticated;
        this.participantOneAuthenticated = participantOneAuthenticated;
        propertyChangeSupport.firePropertyChange(PROP_PARTICIPANT_ONE_AUTHENTICATED, oldParticipantOneAuthenticated, participantOneAuthenticated);
        if ( participantOneAuthenticated && participantTwoAuthenticated ) {
            setCompleteAble(true);
        }
        if ( participantOneAuthenticated ) {
            setStatusMessage(participantOne + " authentifiziert.");
        }
    }

    /**
     * Get the value of participantTwoAuthenticated
     *
     * @return the value of participantTwoAuthenticated
     */
    public boolean isParticipantTwoAuthenticated() {
        return participantTwoAuthenticated;
    }

    /**
     * Set the value of participantTwoAuthenticated
     *
     * @param participantTwoAuthenticated new value of participantTwoAuthenticated
     */
    public void setParticipantTwoAuthenticated(boolean participantTwoAuthenticated) {
        boolean oldParticipantTwoAuthenticated = this.participantTwoAuthenticated;
        this.participantTwoAuthenticated = participantTwoAuthenticated;
        propertyChangeSupport.firePropertyChange(PROP_PARTICIPANT_TWO_AUTHENTICATED, oldParticipantTwoAuthenticated, participantTwoAuthenticated);
        if ( participantOneAuthenticated && participantTwoAuthenticated ) {
            setCompleteAble(true);
        }
        if ( participantTwoAuthenticated ) {
            setStatusMessage(participantTwo + " authentifiziert.");
        }
    }

    /**
     * Get the value of participantTwo
     *
     * @return the value of participantTwo
     */
    public String getParticipantTwo() {
        return participantTwo;
    }

    /**
     * Set the value of participantTwo
     *
     * @param participantTwo new value of participantTwo
     */
    public void setParticipantTwo(String participantTwo) {
        String oldParticipantTwo = this.participantTwo;
        this.participantTwo = participantTwo;
        propertyChangeSupport.firePropertyChange(PROP_PARTICIPANT_TWO, oldParticipantTwo, participantTwo);
    }

    /**
     * Get the value of participantOne
     *
     * @return the value of participantOne
     */
    public String getParticipantOne() {
        return participantOne;
    }

    /**
     * Set the value of participantOne
     *
     * @param participantOne new value of participantOne
     */
    public void setParticipantOne(String participantOne) {
        String oldParticipantOne = this.participantOne;
        this.participantOne = participantOne;
        propertyChangeSupport.firePropertyChange(PROP_PARTICIPANT_ONE, oldParticipantOne, participantOne);
    }

    /**
     * Get the value of complete
     *
     * @return the value of complete
     */
    public boolean isComplete() {
        return complete;
    }

    /**
     * Set the value of complete
     *
     * @param complete new value of complete
     */
    public void setComplete(boolean complete) {
        boolean oldComplete = this.complete;
        this.complete = complete;
        propertyChangeSupport.firePropertyChange(PROP_COMPLETE, oldComplete, complete);
    }

    /**
     * Get the value of statusMessage
     *
     * @return the value of statusMessage
     */
    public String getStatusMessage() {
        return statusMessage;
    }

    /**
     * Set the value of statusMessage
     *
     * @param statusMessage new value of statusMessage
     */
    public void setStatusMessage(String statusMessage) {
        String oldStatusMessage = this.statusMessage;
        this.statusMessage = statusMessage;
        propertyChangeSupport.firePropertyChange(PROP_STATUSMESSAGE, oldStatusMessage, statusMessage);
    }

    /**
     * Add PropertyChangeListener.
     *
     * @param listener
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Remove PropertyChangeListener.
     *
     * @param listener
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    /**
     * Add VetoableChangeListener.
     *
     * @param listener
     */
    public void addVetoableChangeListener(VetoableChangeListener listener) {
        vetoableChangeSupport.addVetoableChangeListener(listener);
    }

    /**
     * Remove VetoableChangeListener.
     *
     * @param listener
     */
    public void removeVetoableChangeListener(VetoableChangeListener listener) {
        vetoableChangeSupport.removeVetoableChangeListener(listener);
    }

    public List<StockTransaction> getStockTransactions() {
        return stockTransactions;
    }

    public ListModel getUnitModel() {
        return unitModel;
    }

    public ListModel getTransactionModel() {
        return transactionModel;
    }

    public List<StockUnit> getStockUnits() {
        return stockUnits;
    }

    /**
     * Returns a list of missing transaction informations.
     *
     * @return a list of missing transaction informations
     */
    public SortedSet<String> getMissingRefurbishedIds() {
        SortedSet<String> result = new TreeSet<>();
        if ( missingStockUnits.isEmpty() ) return result;
        for (StockUnit stockUnit : missingStockUnits) {
            result.add("[" + stockUnit.getRefurbishId() + "] " + stockUnit.getName());
        }
        return result;
    }
}
