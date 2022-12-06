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
package eu.ggnet.dwoss.redtapext.ee.workflow;

import java.util.ArrayList;
import java.util.Date;

import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.core.common.values.DocumentType;
import eu.ggnet.dwoss.core.common.values.PositionType;
import eu.ggnet.dwoss.mandator.api.value.Mandator;
import eu.ggnet.dwoss.mandator.api.value.partial.DocumentIdentifierGeneratorConfiguration;
import eu.ggnet.dwoss.redtape.ee.eao.AddressEao;
import eu.ggnet.dwoss.redtape.ee.emo.RedTapeCounterEmo;
import eu.ggnet.dwoss.redtape.ee.entity.*;
import eu.ggnet.dwoss.redtape.ee.entity.util.DocumentEquals;
import eu.ggnet.dwoss.stock.ee.emo.EquilibrationResult;
import eu.ggnet.dwoss.stock.ee.emo.LogicTransactionEmo;
import eu.ggnet.dwoss.uniqueunit.ee.entity.UniqueUnit;

import static eu.ggnet.dwoss.redtape.ee.entity.util.DocumentEquals.Property.*;

/**
 * Workflow Superclass.
 * It is mearly a form of Util class but allows to be used extended.
 *
 * @author oliver.guenther
 */
public abstract class RedTapeWorkflow {

    protected final Logger L = LoggerFactory.getLogger(this.getClass());

    protected EntityManager redTapeEm;

    protected EntityManager uniqueUnitEm;

    protected EntityManager stockEm;

    // TODO: push down.
    protected String arranger;

    protected Mandator mandator;

    public RedTapeWorkflow() {
    }

    public RedTapeWorkflow(EntityManager redTapeEm, EntityManager uniqueUnitEm, EntityManager stockEm, Mandator mandator) {
        this.redTapeEm = redTapeEm;
        this.uniqueUnitEm = uniqueUnitEm;
        this.stockEm = stockEm;
        this.mandator = mandator;
    }

    public RedTapeWorkflow(EntityManager redTapeEm, EntityManager uniqueUnitEm, EntityManager stockEm, String arranger, Mandator mandator) {
        this.redTapeEm = redTapeEm;
        this.uniqueUnitEm = uniqueUnitEm;
        this.stockEm = stockEm;
        this.arranger = arranger;
        this.mandator = mandator;
    }

    public Document execute() {
        throw new UnsupportedOperationException("Not Yet Implemented");
    }

    protected void removeLogicTransaction(Document document) {
        equilibrateOrRemoveLogicTransaction(document, true);
    }

    protected void equilibrateLogicTransaction(Document document) {
        equilibrateOrRemoveLogicTransaction(document, false);
    }

    /**
     * Equilibrates the LogicTransaction to the Document and added History to all UniqueUnits.
     * <p>
     * @param document
     * @param remove   if true everything is removed.
     */
    protected void equilibrateOrRemoveLogicTransaction(Document document, boolean remove) {
        LogicTransactionEmo ltEmo = new LogicTransactionEmo(stockEm);
        EquilibrationResult equilibrate = ltEmo.equilibrate(document.getDossier().getId(), remove ? new ArrayList<>() : document.getPositionsUniqueUnitIds());
        if ( equilibrate == null ) return;
        L.debug("Equilibrated Stock LogicTransaction: {}", equilibrate);
        for (Integer uuid : equilibrate.added()) {
            uniqueUnitEm.find(UniqueUnit.class, uuid).addHistory(
                    "Added to Dossier " + document.getDossier().getIdentifier() + " of Customer " + document.getDossier().getCustomerId() + " by " + arranger);
        }
        for (Integer uuid : equilibrate.removed()) {
            uniqueUnitEm.find(UniqueUnit.class, uuid).addHistory(
                    "Removed from Dossier " + document.getDossier().getIdentifier() + " of Customer " + document.getDossier().getCustomerId() + " by " + arranger);
        }
    }

    /**
     * Clones the new Document from the altered Document, sets all detached Entities and updates the active status.
     * <p>
     * @param alteredDetachedDocument
     * @param previousDocument
     * @return
     */
    protected Document refreshAndPrepare(Document alteredDetachedDocument, Document previousDocument) {
        //Partiall Clone our next Document
        Document newDocument = alteredDetachedDocument.partialClone();
        // Replace detached entities
        AddressEao addEao = new AddressEao(redTapeEm);
        Address invoiceAddress = addEao.findById(alteredDetachedDocument.getInvoiceAddress().getId());
        Address shippingAddress = addEao.findById(alteredDetachedDocument.getShippingAddress().getId());
        newDocument.setInvoiceAddress(invoiceAddress);
        newDocument.setShippingAddress(shippingAddress);
        newDocument.setHistory(new DocumentHistory(arranger, "Update durch Workflow"));
        // Set automatic Information
        newDocument.setDossier(previousDocument.getDossier());
        newDocument.setPredecessor(previousDocument);
        newDocument.setActive(true);
        if ( previousDocument.getType() == newDocument.getType() ) {
            previousDocument.setActive(false);
            // A Complaint gets reopend on condition change.
            if ( newDocument.getType() == DocumentType.COMPLAINT && !previousDocument.getConditions().equals(newDocument.getConditions()) ) {
                newDocument.setClosed(false);
                newDocument.getDossier().setClosed(false);
            }
        } else {// On Document Type Change, the dossier gets reopened and some cleanup is happening.
            newDocument.getDossier().setClosed(false);
            newDocument.setClosed(false);
            newDocument.setIdentifier(null);
            newDocument.setActual(new Date());
            newDocument.remove(Document.Flag.CUSTOMER_BRIEFED);
            newDocument.remove(Document.Flag.CUSTOMER_EXACTLY_BRIEFED);
        }
        L.debug("Prepared {}", newDocument);
        return newDocument;
    }

    /**
     * Validates afterwards: Check if still needed.
     *
     * @param dossier
     */
    protected void validateAfter(Dossier dossier) {
        // TODO: Check if still needed.
        Long oneOrder = null;
        Long oneInvoice = null;
        for (Document d : dossier.getActiveDocuments()) {
            switch (d.getType()) {
                case ORDER:
                    if ( oneOrder != null ) throw new RuntimeException("More than one Order Acitve: " + d.getId() + " and " + oneOrder + ", " + dossier);
                    else oneOrder = d.getId();
                    break;
                case INVOICE:
                    if ( oneInvoice != null ) throw new RuntimeException("More than one Invoice Acitve: " + d.getId() + " and " + oneInvoice + ", " + dossier);
                    else oneInvoice = d.getId();
                    break;
                default:
            }
        }
    }

    /**
     * Optionally generates and sets the identifier for the document.
     * The Identifier is only generated if the mandator has a configuration for the document type.
     * <p>
     * @param document
     * @return
     */
    protected String generateIdentifier(Document document) {
        // Verify if a config exist.
        DocumentIdentifierGeneratorConfiguration digc = mandator.documentIdentifierGeneratorConfigurations().get(document.getType());
        if ( digc == null ) return null;

        RedTapeCounter counter = new RedTapeCounterEmo(redTapeEm).requestNext(document.getType(), digc.prefixType().generate(), digc.initialValue());
        String identifier = digc
                .pattern()
                .replace(DocumentIdentifierGeneratorConfiguration.VAR_PREFIX, counter.getPrefix())
                .replace(DocumentIdentifierGeneratorConfiguration.VAR_COUNTER, digc.counterFormat().format(counter.getValue()));

        document.setIdentifier(identifier);
        L.debug("Generated Identifier: {}", document.getIdentifier());
        return identifier;
    }

    protected void validate(Document altered, Document previous) {
        // Check that the in db is correct
        Dossier dos = previous.getDossier();
        if ( altered.getOptLock() != previous.getOptLock() )
            throw new IllegalStateException("The Previous Document has a different optLock, so it was changed since the last read. Please try cancel, refresh the dossier and that the change.");
        if ( previous.getConditions().contains(Document.Condition.CANCELED) ) throw new IllegalStateException(
                    "The Previous Document is canceled.\nAltered: " + altered + "\nPrevious: " + previous);
        for (DocumentType type : DocumentType.values()) {
            if ( type == DocumentType.CREDIT_MEMO
                    || type == DocumentType.COMPLAINT
                    || type == DocumentType.ANNULATION_INVOICE ) continue;
            if ( dos.getActiveDocuments(type).size() > 1 ) throw new IllegalStateException(
                        "The Dossier(id=" + dos.getId() + ") has more than one active Document of Type " + type
                        + "\n" + dos + "\n" + dos.getActiveDocuments(type));
        }
        if ( !previous.isActive() ) throw new IllegalStateException("The Previous Document is not active.\nAltered: " + altered + "\nPrevious: " + previous);
        // A Change of Type may occour on closed documents
        // TODO: Check, that the Type Change is allowed (e.g. from RETURNS to CAPITAL_ASSET obviously not
        if ( altered.getType() != previous.getType() ) return;
        if ( !previous.isClosed() ) return;
        // A Complaint is the only Document, that may be reported twice, so a reopening is possible.
        if ( previous.getType() == DocumentType.COMPLAINT ) return;
        // Now to the restrictive handling of closed documents.
        if ( !dos.changesAllowed(altered.getDossier()) ) throw new IllegalStateException("The Dossier is clossed and supplied changes are not allowed."
                    + "\nAltered: " + altered.getDossier() + "\nPrevious: " + dos);

        DocumentEquals documentEquals = new DocumentEquals()
                .ignore(ID, ACTIVE, HISTORY, PREDECESSOR, DIRECTIVE, CONDITIONS, FLAGS, SETTLEMENTS)
                .ignoreAddresses()
                .igonrePositionOrder()
                .ignorePositions(PositionType.COMMENT);
        if ( !documentEquals.equals(previous, altered) ) throw new IllegalStateException("The Document is clossed and supplied changes are not allowed."
                    + documentEquals.equalsMessage(previous, altered));
    }
}
