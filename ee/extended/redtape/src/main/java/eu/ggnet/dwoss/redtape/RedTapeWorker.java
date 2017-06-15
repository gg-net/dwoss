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

import java.io.Serializable;
import java.util.List;

import javax.ejb.Remote;
import javax.enterprise.event.Observes;

import eu.ggnet.dwoss.event.AddressChange;
import eu.ggnet.dwoss.redtape.entity.*;
import eu.ggnet.dwoss.redtape.state.CustomerDocument;
import eu.ggnet.dwoss.redtape.workflow.RedTapeCreateDossierWorkflow;
import eu.ggnet.dwoss.rules.DocumentType;
import eu.ggnet.dwoss.util.UserInfoException;
import eu.ggnet.statemachine.StateTransition;

import lombok.Value;

/**
 * RedTapeWorker.
 * <p/>
 * @author oliver.guenther
 */
@Remote
public interface RedTapeWorker {

    @Value
    public static class Addresses implements Serializable {

        private final Address invoice;

        private final Address shipping;

    }

    /**
     * Creates a new, valid Dossier containing a document of the order type.
     * <p>
     * See {@link RedTapeCreateDossierWorkflow} for implementation.
     *
     * @param customerId The customer associated to the new Dossier.
     * @param dispatch   The dispatch state of the Dossier.
     * @param arranger   The arranger of the new Dossier.
     * @return A new, valid, persisted Dossier.
     */
    Dossier create(long customerId, boolean dispatch, String arranger);

    /**
     * Deletes a {@link Dossier}, cleaning up the Stock.
     * <p/>
     * @param dos the Dossier to be deleted.
     */
    void delete(Dossier dos);

    /**
     * Create a HTML formated String representing the detailed information from a {@link Dossier} and the Sopo data belonging to the Dossier.
     * <p/>
     * @param dossierId The Dossier
     * @return a HTML formated String representing the detailed information from a {@link Dossier} and the Sopo data belonging to the Dossier.
     */
    String toDetailedHtml(long dossierId);

    List<StateTransition<CustomerDocument>> getPossibleTransitions(CustomerDocument cdoc);

    /**
     * Gives either an address out of the db or persist a new one if nothing is found.
     * <p/>
     * @param description
     * @return the found or new persisted Address
     */
    Address requestAddressByDescription(String description);

    /**
     * Gives a pair of {@link Address}es found by a customer.
     * <p/>
     * If no Address could be found, new persisted entities are created.
     * <p/>
     * @param customerId the customerId as source
     * @return A addresses container
     */
    Addresses requestAdressesByCustomer(long customerId);

    /**
     * This method handles necessary cleanups if creation or update is canceld.
     * <p/>
     * If stock.LogicTransaction differs form Dossier.Document.Positions &rArr; change LogicTransaction.
     * <ul><li>Only LogicTransaction > Dossier.Document.Positions should be possible</li></ul>
     * If Dossier.isEmpty &rarr; delete.
     * If stock.LogicTransaction is empty &rarr; delete.
     * <p>
     * @param detached a detached document
     * @return the removed instance
     * @throws UserInfoException if a revertCreate is not appropriated
     */
    Document revertCreate(Document detached) throws UserInfoException;

    /**
     * Changes the State, of a Customer and Document based on the transition.
     * <p/>
     * @param cdoc       the Document and Customer to take the change.
     * @param transition the transition to do.
     * @param arranger   the arranger
     * @return the Document in the new state.
     */
    Document stateChange(CustomerDocument cdoc, StateTransition<CustomerDocument> transition, String arranger);

    /**
     * Update changes from a Document by looking up the original from the database.
     * <p/>
     * A document is not equal if {@link Document#equalsContent(de.dw.redtape.entity.Document) } is false
     * or Document.getDossier.paymentMethod or Document.getDossier.dispatch are different.
     * Every Document manipulation is done by this method and handling all necessary manipulations in the SopoSoft system as well.
     * <p/>
     * <u>Dossier Handling</u>
     * <ul>
     * <li>Changes to {@link Dossier#paymentMethod} and {@link Dossier#dispatch} are persisted</li>
     * </ul>
     * <u>Document Handling</u>
     * <ul>
     * <li>If the given Document has no changes it is returned right away</li>
     * <li>If unequal a {@link Document#partialClone() } is used and detached Entities are attached (Dossier, Addresses)</li>
     * <li>If the {@link Document#getType() } is INVOICE while the previous version is not, a new Invoice Identifier is set to the new Document.</li>
     * </ul>
     * <u>SopoAuftrag Handling</u>
     * <ul>
     * <li>If the Document is updated, the SopoAuftrag will be updated as well.<br />
     * This is done by clearing all Positions and refill the whole SopoAuftrag.</li>
     * <li>If no SopoAuftrag exist, a new one is created.</li>
     * </ul>
     * <u>Stock Handling</u>
     * <ul>
     * <li>In any update process, the LogicTransaction will be cleared from its StockUnits</li>
     * <li>Does the new Document contain no Position of Position.Type.UNIT the LogicTransaction will be deleted.</li>
     * <li>Should there be any clash of StockUnit Transaction information, a Exception is thrown</li>
     * </ul>
     * <p/>
     *
     * @param doc         The Document that will be equalised against the original
     * @param destination In the case of CreditMemo, the destination for the units.
     * @param arranger    The recent user
     * @return A new persisted Document or the given if equal
     */
    Document update(final Document doc, Integer destination, final String arranger);

    /**
     * Changes the {@link Address} of all active {@link Document} of {@link DocumentType#ORDER} and no Invoices or CreditMemos found from every {@link Dossier}
     * containing a specific customer.
     * <p/>
     * If the address does not exist, it will be created.
     * <p/>
     * @param event the address change occured
     */
    void updateAllDocumentAdresses(@Observes AddressChange event);

    /**
     * Update Comments of the Dossier.
     * <p/>
     * @param dossier the dossier to update
     * @param comment the comment
     * @return returns the dossier
     * @throws UserInfoException if something is not ok.
     */
    Dossier updateComment(Dossier dossier, String comment) throws UserInfoException;

    /**
     * Creates a new SalesProduct for the PartNo.
     * <p/>
     * @param partNo the partNo to use.
     * @return the new SalesProduct.
     * @throws UserInfoException if the PartNo does not exist.
     */
    SalesProduct createSalesProduct(String partNo) throws UserInfoException;
}
