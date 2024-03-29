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

import eu.ggnet.dwoss.core.common.values.PositionType;
import eu.ggnet.dwoss.mandator.api.value.Mandator;
import eu.ggnet.dwoss.mandator.api.value.RepaymentCustomers;
import eu.ggnet.dwoss.core.common.values.tradename.TradeName;
import eu.ggnet.dwoss.core.common.values.DocumentType;
import eu.ggnet.dwoss.redtape.ee.entity.DocumentHistory;
import eu.ggnet.dwoss.redtape.ee.entity.Position;
import eu.ggnet.dwoss.redtape.ee.entity.Dossier;
import eu.ggnet.dwoss.redtape.ee.entity.Document;

import java.util.*;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;

import eu.ggnet.dwoss.redtape.ee.eao.DocumentEao;
import eu.ggnet.dwoss.redtape.ee.format.DocumentFormater;
import eu.ggnet.dwoss.stock.ee.eao.StockUnitEao;
import eu.ggnet.dwoss.stock.ee.emo.LogicTransactionEmo;
import eu.ggnet.dwoss.stock.ee.emo.StockTransactionEmo;
import eu.ggnet.dwoss.stock.ee.entity.StockTransaction;
import eu.ggnet.dwoss.stock.ee.entity.StockUnit;
import eu.ggnet.dwoss.uniqueunit.ee.eao.UniqueUnitEao;
import eu.ggnet.dwoss.uniqueunit.ee.entity.UniqueUnit;

/**
 * The CreditMemo Workflow.
 *
 * @author oliver.guenther
 */
public class RedTapeUpdateRepaymentWorkflow extends RedTapeWorkflow {

    private final RedTapeCreateDossierWorkflow createWorkflow;

    private final RepaymentCustomers repaymentCustomers;

    private final Document alteredDocument;

    private final Integer destinationId;

    public RedTapeUpdateRepaymentWorkflow(EntityManager redTapeEm, EntityManager uniqueUnitEm, EntityManager stockEm, String arranger, Mandator mandator, RedTapeCreateDossierWorkflow createWorkflow, RepaymentCustomers repaymentCustomers, Document alteredDocument, Integer destinationId) {
        super(redTapeEm, uniqueUnitEm, stockEm, arranger, mandator);
        this.createWorkflow = createWorkflow;
        this.repaymentCustomers = repaymentCustomers;
        this.alteredDocument = alteredDocument;
        this.destinationId = destinationId;
    }

    protected void validate(Document altered, Document previous, Integer destinationId) {
        super.validate(altered, previous);
        if ( altered.getType() == DocumentType.ANNULATION_INVOICE ) {
            for (Position position : altered.getPositions().values()) {
                if ( position.getType() == PositionType.COMMENT ) continue;
                if ( position.getPrice() > 0 ) throw new IllegalArgumentException("Position has a price higher than 0: " + position);
            }
        }
        if ( altered.getType() != previous.getType()
                && altered.containsPositionType(PositionType.UNIT)
                && destinationId == null ) {
            throw new NullPointerException("DestinationId must not be null");
        }
    }

    /**
     * If referenced Units are still in Stock, remove them form the LogicTransaction and add them to the instance variable (stockUnits).
     *
     * @return the list of removed StockUnits
     */
    List<StockUnit> optionalRemoveFromLogicTransaction(Document document) {
        List<StockUnit> result = new LogicTransactionEmo(stockEm).optionalRemoveUnits(
                document.getDossier().getId(), document.getPositionsUniqueUnitIds());
        if ( !result.isEmpty() ) L.info("Removed from LogicTransaction: {}", toIds(result));
        return result;
    }

    /**
     * If referenced Units are not in Stock, roll them in and append them to the instance variable (stockUnits).
     *
     * @return the list of rolled in StockUnits
     */
    List<StockUnit> rollInMissingStockUnits(String dossierIdentifier, Collection<Position> positions, int destinationId) {
        List<UniqueUnit> uniqueUnits = new ArrayList<>();
        StockUnitEao stockUnitEao = new StockUnitEao(stockEm);
        UniqueUnitEao uniqueUnitEao = new UniqueUnitEao(uniqueUnitEm);
        for (Position position : positions) {
            if ( stockUnitEao.findByUniqueUnitId(position.getUniqueUnitId()) == null ) {
                uniqueUnits.add(uniqueUnitEao.findById(position.getUniqueUnitId()));
            }
        }
        if ( uniqueUnits.isEmpty() ) return Collections.EMPTY_LIST;
        StockTransactionEmo transactionEmo = new StockTransactionEmo(stockEm);
        StockTransaction rollInTransaction = transactionEmo.requestRollInPrepared(destinationId, arranger, "RollIn durch Gutschrift " + dossierIdentifier);
        for (UniqueUnit uu : uniqueUnits) {
            StockUnit stockUnit = new StockUnit(uu.getIdentifier(UniqueUnit.Identifier.REFURBISHED_ID), uu.getProduct().getName(), uu.getId());
            rollInTransaction.addUnit(stockUnit);
            stockEm.persist(stockUnit);
        }
        List<StockUnit> rolledInUnits = transactionEmo.completeRollIn(arranger, Arrays.asList(rollInTransaction));
        L.info("Missing Units rolled In: {}", toIds(rolledInUnits));
        return rolledInUnits;
    }

    /**
     * Validate if all StockUnits are on the destination Stock, and if not transfer them.
     *
     * @param stockUnits the stockUnits to validate
     */
    void optionalTransferToDestination(List<StockUnit> stockUnits, int destinationId) {
        List<StockUnit> onWrongStock = new ArrayList<>();
        for (StockUnit stockUnit : stockUnits) {
            if ( stockUnit.isInTransaction() ) continue; // We are ignoring everything on a transaction.
            if ( stockUnit.getStock().getId() != destinationId ) onWrongStock.add(stockUnit);
        }
        if ( onWrongStock.isEmpty() ) return;
        StockTransactionEmo transactionEmo = new StockTransactionEmo(stockEm);
        StockTransaction transfer = transactionEmo.requestExternalTransferPrepare(onWrongStock.get(0).getStock().getId(), destinationId, arranger, "Transfer durch Gutschrift");
        for (StockUnit stockUnit : onWrongStock) transfer.addUnit(stockUnit);
        List<StockUnit> transferdUnits = transactionEmo.completeExternalTransfer(arranger, Arrays.asList(transfer));
        L.info("Destination {} for units on wrong stock used: {}", transferdUnits);
    }

    /**
     * Map the Positions of the altered Document by Contractors of the referencing UniqueUnits
     *
     * @return the mapped association.
     */
    Map<TradeName, List<Position>> mapPositionsToContrator(Collection<Position> positions) {
        Map<TradeName, List<Position>> result = new HashMap<>();
        UniqueUnitEao uniqueUnitEao = new UniqueUnitEao(uniqueUnitEm);
        for (Position pos : positions) {
            TradeName contractor = uniqueUnitEao.findById(pos.getUniqueUnitId()).getContractor();
            if ( !result.containsKey(contractor) ) result.put(contractor, new ArrayList<>());
            result.get(contractor).add(pos.partialClone());
        }
        return result;
    }

    /**
     * Create the Mirror Dossier on the SystemCustomer.
     *
     * @param customerId the customerId of the SystemCusotmer
     * @return the created Dossier
     */
    Dossier createMirrorDossier(long customerId) {
        return createWorkflow.execute(customerId, false, arranger);
    }

    void updateMirrorPositions(String dossierIdentifier, Document mirror, Collection<Position> positions) {
        mirror.appendAll(positions);
        mirror.append(Position.builder()
                .amount(1)
                .type(PositionType.COMMENT)
                .name("Gutschrift von Dossier " + dossierIdentifier)
                .description("Gutschrift von Dossier " + dossierIdentifier + " durch " + arranger)
                .build());
    }

    @Override
    public Document execute() {
        // TODO: don't do this in stateless, works for now.
        Document previousDoc = new DocumentEao(redTapeEm).findById(alteredDocument.getId(), LockModeType.PESSIMISTIC_WRITE);
        validate(alteredDocument, previousDoc, destinationId);
        if ( alteredDocument.equalsContent(previousDoc) ) return alteredDocument;
        L.info("Workflow on {} by {}", DocumentFormater.toSimpleLine(alteredDocument), arranger);
        Document newDocument = refreshAndPrepare(alteredDocument, previousDoc);
        if ( alteredDocument.getType() != previousDoc.getType() ) {
            // A Complaint is now complete.
            if ( previousDoc.getType() == DocumentType.COMPLAINT ) previousDoc.setDirective(Document.Directive.NONE);
            generateIdentifier(newDocument);
        }
        if ( !alteredDocument.isStillExactlyBriefed(previousDoc) ) newDocument.remove(Document.Flag.CUSTOMER_EXACTLY_BRIEFED);
        newDocument.setHistory(new DocumentHistory(arranger, "Update durch " + getClass().getSimpleName()));
        redTapeEm.persist(newDocument);
        redTapeEm.flush(); // Writing new document an gennerating the id;
        L.debug("Returning {} with {}", newDocument, newDocument.getDossier());
        validateAfter(newDocument.getDossier());
        if ( alteredDocument.getType() == previousDoc.getType() ) return newDocument; // Is this a first time change
        if ( !alteredDocument.containsPositionType(PositionType.UNIT) ) return newDocument; // Is it a full CreditMemo on a Unit
        List<StockUnit> stockUnits = optionalRemoveFromLogicTransaction(newDocument);
        List<StockUnit> stockUnits2 = rollInMissingStockUnits(newDocument.getDossier().getIdentifier(), newDocument.getPositions(PositionType.UNIT).values(), destinationId);
        optionalTransferToDestination(stockUnits, destinationId);
        Map<TradeName, List<Position>> contractorPositions = mapPositionsToContrator(newDocument.getPositions(PositionType.UNIT).values());
        for (TradeName contractor : contractorPositions.keySet()) {
            Document mirror = createMirrorDossier(repaymentCustomers.get(contractor).get()).getActiveDocuments().get(0);
            updateMirrorPositions(newDocument.getDossier().getIdentifier(), mirror, contractorPositions.get(contractor));
            equilibrateLogicTransaction(mirror);
        }
        return newDocument;
    }

    private List<String> toIds(List<StockUnit> result) {
        List<String> units = new ArrayList<>();
        for (StockUnit stockUnit : result) {
            units.add("StockUnit(id=" + stockUnit.getId() + ",refurbishedId=" + stockUnit.getRefurbishId() + ")");
        }
        return units;
    }
}
