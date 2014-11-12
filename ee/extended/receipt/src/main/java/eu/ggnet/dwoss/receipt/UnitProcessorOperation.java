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
package eu.ggnet.dwoss.receipt;

import eu.ggnet.dwoss.rules.ReceiptOperation;
import eu.ggnet.dwoss.rules.PositionType;
import eu.ggnet.dwoss.redtape.entity.Dossier;
import eu.ggnet.dwoss.redtape.entity.Position;
import eu.ggnet.dwoss.redtape.entity.PositionBuilder;
import eu.ggnet.dwoss.redtape.entity.Document;
import eu.ggnet.dwoss.stock.entity.StockTransactionStatus;
import eu.ggnet.dwoss.stock.entity.StockUnit;
import eu.ggnet.dwoss.stock.eao.StockTransactionEao;
import eu.ggnet.dwoss.stock.eao.LogicTransactionEao;
import eu.ggnet.dwoss.stock.entity.StockTransactionParticipationType;
import eu.ggnet.dwoss.stock.entity.StockTransactionType;
import eu.ggnet.dwoss.stock.entity.Stock;
import eu.ggnet.dwoss.stock.emo.StockLocationDiscoverer;
import eu.ggnet.dwoss.stock.emo.LogicTransactionEmo;
import eu.ggnet.dwoss.stock.eao.StockUnitEao;
import eu.ggnet.dwoss.stock.entity.StockTransactionStatusType;
import eu.ggnet.dwoss.stock.entity.Shipment;
import eu.ggnet.dwoss.stock.entity.StockTransaction;
import eu.ggnet.dwoss.stock.entity.StockTransactionPosition;
import eu.ggnet.dwoss.stock.entity.LogicTransaction;
import eu.ggnet.dwoss.stock.entity.StockTransactionParticipation;
import eu.ggnet.dwoss.uniqueunit.eao.UniqueUnitEao;
import eu.ggnet.dwoss.uniqueunit.entity.Product;
import eu.ggnet.dwoss.uniqueunit.format.UniqueUnitFormater;
import eu.ggnet.dwoss.uniqueunit.entity.UniqueUnitHistory;
import eu.ggnet.dwoss.uniqueunit.eao.ProductEao;
import eu.ggnet.dwoss.uniqueunit.entity.UniqueUnit;
import eu.ggnet.dwoss.uniqueunit.format.ProductFormater;

import java.sql.*;
import java.util.*;
import java.util.Date;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.*;
import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.*;

import eu.ggnet.dwoss.mandator.api.value.PostLedger;
import eu.ggnet.dwoss.mandator.api.value.ReceiptCustomers;
import eu.ggnet.dwoss.redtape.assist.RedTapes;
import eu.ggnet.dwoss.redtape.eao.DossierEao;
import eu.ggnet.dwoss.redtape.emo.DossierEmo;


import eu.ggnet.dwoss.stock.assist.Stocks;
import eu.ggnet.dwoss.uniqueunit.assist.UniqueUnits;
import eu.ggnet.dwoss.uniqueunit.entity.UniqueUnit.Identifier;

import eu.ggnet.dwoss.util.UserInfoException;
import eu.ggnet.dwoss.util.persistence.eao.DefaultEao;

import static eu.ggnet.dwoss.uniqueunit.entity.UniqueUnit.Identifier.*;

/**
 * Receipt Operation for Units.
 *
 * @author oliver.guenther
 */
@Stateless
public class UnitProcessorOperation implements UnitProcessor {

    private final static Logger L = LoggerFactory.getLogger(UnitProcessorOperation.class);

    @Inject
    @UniqueUnits
    private EntityManager uuEm;

    @Inject
    @Stocks
    private EntityManager stockEm;

    @Inject
    @RedTapes
    private DataSource redTapeSource;

    @Inject
    @RedTapes
    private EntityManager redTapeEm;

    @Inject
    private ReceiptCustomers receiptCustomers;

    @Inject
    private PostLedger postLedger;

    /**
     * Receipts a new Unit.
     * Multiphase Process:
     * <ol>
     * <li>Validation and throw IllegalArgumentException if
     * <ul>
     * <li>Supplied UniqueUnit is already persistent</li>
     * <li>RefurbishedId is already taken</li>
     * <li>Serial is already taken and Unit is in Stock [UniqueUnit &amp; Stock]</li>
     * </ul>
     * </li>
     * <li>If serial is taken update existing Unit else persist Unit[UniqueUnit]<br />
     * (At this point it implies that the Unit is not in Stock)</li>
     * <li>Create, weak reference StockUnit and prepare for rollIn [Stock]</li>
     * </ol>
     * <p/>
     * @param shipment         the shipment
     * @param recieptUnit      the UniqueUnit to be receipt, must not be null
     * @param operation        the Operation to do
     * @param transaction
     * @param arranger
     * @param operationComment
     * @throws IllegalArgumentException if validation fails
     */
    @Override
    public void receipt(UniqueUnit recieptUnit, Product product, Shipment shipment,
                        StockTransaction transaction, ReceiptOperation operation, String operationComment, String arranger) throws IllegalArgumentException {
        L.info("Receiping Unit(refurbishId={},name={}) on StockTransaction(id={}) with {} by {}",
                recieptUnit.getRefurbishId(), ProductFormater.toNameWithPartNo(product), transaction.getId(), operation, arranger);
        validateReceipt(recieptUnit);
        UniqueUnit uniqueUnit = receiptUniqueUnit(recieptUnit, Objects.requireNonNull(product, "Product == null, not allowed"), shipment);
        StockUnit stockUnit = receiptAndAddStockUnit(uniqueUnit, transaction);
        if ( operation == ReceiptOperation.SALEABLE ) return; // Nothing to do
        executeOperation(uniqueUnit, stockUnit, operation, operationComment, arranger);
    }

    /**
     * Updates the UniqueUnit.
     * Multiphase Process.
     * <ol>
     * <li>Validate all input data.</li>
     * <li>Merge UniqueUnit and set Product</li>
     * <li>Overwrite StockUnit</li>
     * <li>Overwrite SopoUnit</li>
     * <li>If SopoUnit is on "Process equivalent Customer", remove it there.</li>
     * <li>Execute Operation [Sopo]:
     * <ul>
     * <li>If Operation == Sales, nothing more to do</li>
     * <li>Else findByTypeAndStatus the KundenId from Contractor and Operation -> create or reuse a Auftrag and add the SopoUnit</li>
     * </ul>
     * </li>
     * </ol>
     *
     * @param uniqueUnit       the UniqueUnit, must not be null
     * @param product          the Product, must not be null
     * @param updateOperation  the operation, must not be null
     * @param operationComment the comment of the operation, may be null
     * @param arranger
     */
    @Override
    public void update(UniqueUnit uniqueUnit, Product product, ReceiptOperation updateOperation, String operationComment, String arranger) throws IllegalArgumentException {
        L.info("Updateing UniqueUnit(id={},refurbishId={},name={}) with {} by {}", uniqueUnit.getId(),
                uniqueUnit.getRefurbishId(), ProductFormater.toNameWithPartNo(product), updateOperation, arranger);
        uniqueUnit = updateUniqueUnit(uniqueUnit, product);
        StockUnit stockUnit = optionalUpdateStockUnit(uniqueUnit);
        // These two operations define, that nothing in LT/RedTape/Sopo may be changed.
        if ( updateOperation == ReceiptOperation.IN_SALE || stockUnit == null ) return;
        boolean executeNextOperation = cleanUpOldOperation(uniqueUnit, stockUnit, updateOperation, operationComment, arranger);
        if ( executeNextOperation ) executeOperation(uniqueUnit, stockUnit, updateOperation, operationComment, arranger);
    }

    /**
     * Transfers a UniqueUnits StockUnit to the supplied Stock.
     *
     * <ul>
     * <li>Validate, if a StockUnit for the UniqueUnit exists, and this StockUnit is in Stock</li>
     * <li>Transfer StockUnit via {@link StockTransactionType#EXTERNAL_TRANSFER}</li>
     * <li>Update the SopoUnit</li>
     * </ul>
     * <p/>
     * @param uniqueUnit the uniqueUnit
     * @param stockId    the stockId
     * @param arranger
     * @return
     */
    // TODO: Use StockTransactionEmo.resquestExternalTransfer and completeExternalTransfer
    @Override
    public UniqueUnit transfer(UniqueUnit uniqueUnit, int stockId, String arranger) {
        StockUnit stockUnit = new StockUnitEao(stockEm).findByUniqueUnitId(uniqueUnit.getId());
        if ( stockUnit == null ) throw new IllegalArgumentException("No StockUnit for " + uniqueUnit);
        if ( stockUnit.isInTransaction() ) throw new IllegalArgumentException(stockUnit + " is on Transaction");
        Stock destination = new DefaultEao<>(Stock.class, stockEm).findById(stockId);
        StockTransaction st = new StockTransaction(StockTransactionType.EXTERNAL_TRANSFER);
        StockTransactionStatus status = new StockTransactionStatus(StockTransactionStatusType.COMPLETED, new Date());
        status.addParticipation(new StockTransactionParticipation(StockTransactionParticipationType.ARRANGER, arranger));
        st.addStatus(status);
        st.setSource(stockUnit.getStock());
        st.setDestination(destination);
        st.addPosition(new StockTransactionPosition(stockUnit));
        stockEm.persist(st);
        stockUnit.setPosition(null);
        new StockLocationDiscoverer(stockEm).discoverAndSetLocation(stockUnit, destination);
        uniqueUnit = new UniqueUnitEao(uuEm).findById(uniqueUnit.getId());
        uniqueUnit.addHistory("External Stock change from " + stockUnit.getStock() + " to " + destination + " by " + arranger);
        uniqueUnit.fetchEager();
        return uniqueUnit;
    }

    /**
     * Returns a editable UniqueUnit.
     * An Exception is thrown if:
     * <ul>
     * <li>No UniqueUnit with refurbishedId</li>
     * <li>No StockUnit for UniqueUnit</li>
     * <li>StockUnit is on Transaction</li>
     * <li>No SopoUnit with refurbishedId</li>
     * <li>No SopoUnit UniqueUnit miss match</li>
     * </ul>
     * The Operation is discovert via:
     * <ul>
     * <li>If on an AlphaAcount, and operation is allowed, returns appropriated operation</li>
     * <li>If on no Auftrag, returns Sales</li>
     * <li>If on any other Auftrag, returns null</li>
     * </ul>
     *
     * @param refurbishedIdOrSerial the refurbishedId or the serial, both are tried
     * @return a EditableUnit with, the editable UniqueUnit, the refrencing StockUnit, the Operation it is in, and the PartNo
     * @throws UserInfoException if refurbishedId is not ok.
     */
    @Override
    public EditableUnit findEditableUnit(String refurbishedIdOrSerial) throws UserInfoException {
        if ( StringUtils.isBlank(refurbishedIdOrSerial) ) throw new UserInfoException("Keine SopoNr/Seriennummer eingegeben");
        UniqueUnitEao uniqueUnitEao = new UniqueUnitEao(uuEm);
        UniqueUnit uniqueUnit = uniqueUnitEao.findByIdentifier(REFURBISHED_ID, refurbishedIdOrSerial);
        if ( uniqueUnit == null ) uniqueUnit = uniqueUnitEao.findByIdentifier(SERIAL, refurbishedIdOrSerial);
        if ( uniqueUnit == null )
            throw new UserInfoException("Keine Gerät mit SopoNr/Seriennummer " + refurbishedIdOrSerial + " gefunden");
        StockUnit stockUnit;
        ReceiptOperation operation = ReceiptOperation.SALEABLE;
        stockUnit = new StockUnitEao(stockEm).findByUniqueUnitId(uniqueUnit.getId());
        if ( stockUnit == null )
            throw new UserInfoException("Keine Lagergerät für SopoNr " + uniqueUnit.getIdentifier(REFURBISHED_ID) + " gefunden, bearbeitung unzulässig");
        LogicTransaction lt = new LogicTransactionEao(stockEm).findByUniqueUnitId(uniqueUnit.getId());
        if ( lt != null ) {
            operation = receiptCustomers
                    .getOperation(new DossierEao(redTapeEm).findById(lt.getDossierId()).getCustomerId())
                    .orElse(ReceiptOperation.IN_SALE);
        }
        // Lazyinit
        uniqueUnit.fetchEager();
        return new EditableUnit(uniqueUnit, stockUnit, operation, uniqueUnit.getProduct() == null ? "" : uniqueUnit.getProduct().getPartNo());
    }

    private Position toPosition(UniqueUnit uniqueUnit, String operationComment) {
        return new PositionBuilder().setType(PositionType.UNIT).setBookingAccount(postLedger.get(PositionType.UNIT).orElse(-1))
                .setDescription(UniqueUnitFormater.toDetailedDiscriptionLine(uniqueUnit) + ", Aufnahme: " + operationComment)
                .setName(UniqueUnitFormater.toPositionName(uniqueUnit))
                .setUniqueUnitId(uniqueUnit.getId())
                .setUniqueUnitProductId(uniqueUnit.getProduct().getId()).createPosition();
    }

    private void validateReceipt(UniqueUnit receiptUnit) throws IllegalArgumentException {
        if ( receiptUnit.getId() > 0 ) throw new IllegalArgumentException("UniqueUnit has already been persisted " + receiptUnit);
        UniqueUnitEao uniqueUnitEao = new UniqueUnitEao(uuEm);
        UniqueUnit uniqueUnit = uniqueUnitEao.findByIdentifier(Identifier.REFURBISHED_ID, receiptUnit.getIdentifier(Identifier.REFURBISHED_ID));
        if ( uniqueUnit != null ) throw new IllegalArgumentException(
                    "UniqueUnit(refurbishedId=" + receiptUnit.getIdentifier(Identifier.REFURBISHED_ID) + ") exists: " + uniqueUnit);
        StockUnit stockUnit = new StockUnitEao(stockEm).findByRefurbishId(receiptUnit.getRefurbishId());
        if ( stockUnit != null ) throw new IllegalArgumentException(stockUnit + " exists");
    }

    private UniqueUnit receiptUniqueUnit(UniqueUnit recieptUnit, Product product, Shipment shipment) {
        UniqueUnit uniqueUnit = new UniqueUnitEao(uuEm).findByIdentifier(UniqueUnit.Identifier.SERIAL, recieptUnit.getIdentifier(UniqueUnit.Identifier.SERIAL));
        product = new ProductEao(uuEm).findById(product.getId());
        if ( uniqueUnit == null ) {
            recieptUnit.setProduct(product);
            L.debug("persisting {}", recieptUnit);
            // TODO: Update in UI.
            recieptUnit.setShipmentId(shipment.getId());
            recieptUnit.setShipmentLabel(shipment.getShipmentId());
            recieptUnit.setInputDate(new Date());
            uuEm.persist(recieptUnit);
            uniqueUnit = recieptUnit;
        } else {
            uniqueUnit.setIdentifier(UniqueUnit.Identifier.REFURBISHED_ID, recieptUnit.getIdentifier(UniqueUnit.Identifier.REFURBISHED_ID));
            uniqueUnit.setComment(recieptUnit.getComment());
            uniqueUnit.setComments(recieptUnit.getComments());
            uniqueUnit.setCondition(recieptUnit.getCondition());
            uniqueUnit.setInternalComment(recieptUnit.getInternalComment());
            uniqueUnit.setInternalComments(recieptUnit.getInternalComments());
            uniqueUnit.setEquipments(recieptUnit.getEquipments());
            uniqueUnit.setProduct(product);
            uniqueUnit.setContractor(shipment.getContractor());
            uniqueUnit.setShipmentId(shipment.getId());
            uniqueUnit.setShipmentLabel(shipment.getShipmentId());
            uniqueUnit.setWarranty(recieptUnit.getWarranty());
            uniqueUnit.setWarrentyValid(recieptUnit.getWarrentyValid());
            uniqueUnit.setInputDate(new Date()); // Allways set the InputDate on receipt.
            L.debug("updating {}", recieptUnit);
        }
        return uniqueUnit;
    }

    private UniqueUnit updateUniqueUnit(UniqueUnit uniqueUnit, Product product) {
        product = new ProductEao(uuEm).findById(product.getId());
        uniqueUnit = uuEm.merge(uniqueUnit);
        uniqueUnit.setProduct(product);
        L.debug("updating {}", uniqueUnit);
        return uniqueUnit;
    }

    private StockUnit receiptAndAddStockUnit(UniqueUnit uniqueUnit, StockTransaction transaction) {
        StockUnit stockUnit = new StockUnit();
        stockUnit.setUniqueUnitId(uniqueUnit.getId());
        stockUnit.setRefurbishId(uniqueUnit.getIdentifier(UniqueUnit.Identifier.REFURBISHED_ID));
        stockUnit.setName(uniqueUnit.getProduct().getTradeName().getName() + " " + uniqueUnit.getProduct().getName());
        transaction = new StockTransactionEao(stockEm).findById(transaction.getId());
        transaction.addPosition(new StockTransactionPosition(stockUnit));
        L.debug("adding {} to {}", stockUnit, transaction); // implies persist on transaction commit
        return stockUnit;
    }

    private StockUnit optionalUpdateStockUnit(UniqueUnit uniqueUnit) {
        StockUnit stockUnit = new StockUnitEao(stockEm).findByUniqueUnitId(uniqueUnit.getId());
        if ( stockUnit == null ) return null;
        stockUnit.setRefurbishId(uniqueUnit.getIdentifier(Identifier.REFURBISHED_ID));
        stockUnit.setName(uniqueUnit.getProduct().getName());
        L.debug("updating {}", stockUnit);
        return stockUnit;
    }

    private void executeOperation(UniqueUnit uniqueUnit, StockUnit stockUnit, ReceiptOperation operation, String operationComment, String arranger) {
        long customerId = receiptCustomers.getCustomerId(uniqueUnit.getContractor(), operation);
        Document doc = new DossierEmo(redTapeEm)
                .requestActiveDocumentBlock((int)customerId, "Blockaddresse KundenId " + customerId, "Erzeugung durch " + operation, arranger);
        redTapeEm.refresh(doc, LockModeType.PESSIMISTIC_FORCE_INCREMENT);
        if ( !doc.isActive() ) throw new RuntimeException("The Document(id={}) has changed to inactive while locking, this was very unlikely, inform Administrator");
        int directCount = countPositionsDirect(doc);
        if ( doc.getPositions().size() != directCount ) {
            L.warn("Using Workaround for UniqueUnit(id=" + uniqueUnit.getId() + ",refurbishId=" + uniqueUnit.getRefurbishId()
                    + " cause Document(id=" + doc.getId() + ").position.size=" + doc.getPositions().size() + " != directCount=" + directCount);
            doc.append(directCount + 1, toPosition(uniqueUnit, operationComment));
        } else {
            doc.append(toPosition(uniqueUnit, operationComment));
        }
        LogicTransaction lt = new LogicTransactionEmo(stockEm).request(doc.getDossier().getId(), LockModeType.PESSIMISTIC_FORCE_INCREMENT);
        lt.add(stockUnit); // Implicit removes it from an existing LogicTransaction
        L.debug("Executed Operation {} for uniqueUnit(id={},refurbishId={}), added to LogicTransaction({}) and Dossier({})",
                operation, uniqueUnit.getId(), uniqueUnit.getRefurbishId(), lt.getId(), doc.getDossier().getIdentifier());
        uniqueUnit.addHistory(UniqueUnitHistory.Type.STOCK, "RecepitOeration:" + operation + ", " + operationComment + " by " + arranger);
    }

    private boolean cleanUpOldOperation(UniqueUnit uniqueUnit, StockUnit stockUnit, ReceiptOperation updateOperation, String operationComment, String arranger) {
        LogicTransaction oldLogicTransaction = stockUnit.getLogicTransaction();
        if ( oldLogicTransaction != null ) {
            Dossier oldDossier = new DossierEao(redTapeEm).findById(oldLogicTransaction.getDossierId());
            ReceiptOperation oldOperation = receiptCustomers.getOperation(oldDossier.getCustomerId()).orElse(null);
            Document oldDocument = oldDossier.getActiveDocuments().get(0);
            redTapeEm.refresh(oldDocument, LockModeType.PESSIMISTIC_FORCE_INCREMENT);
            if ( !oldDocument.isActive() ) throw new RuntimeException(
                        "The Document(id={}) has changed to inactive while locking, this was very unlikely, inform Administrator");
            Position oldPosition = oldDocument.getPositionByUniqueUnitId(uniqueUnit.getId());
            if ( oldOperation == updateOperation ) {
                oldPosition.setDescription(oldPosition.getDescription() + ", Aufnahme: " + operationComment);
                L.debug("old operation and update operation are {}, nothing more to do", updateOperation);
                return false;
            }
            // cleanUp old Block and Auftrag
            convertToComment(oldPosition, updateOperation);
            L.debug("Old Operation cleanup, removed uniqueUnit(id={},refurbishId={}) from Dossier({})",
                    new Object[]{uniqueUnit.getId(), uniqueUnit.getRefurbishId(), oldDossier.getIdentifier()});
        }
        if ( updateOperation == ReceiptOperation.SALEABLE ) {
            if ( oldLogicTransaction != null ) oldLogicTransaction.remove(stockUnit);
            uniqueUnit.addHistory("Released for Sale by " + arranger);
            L.debug("update operation is {}, nothing more to do", updateOperation);
            return false;
        }
        return true;
    }

    private void convertToComment(Position position, ReceiptOperation operation) {
        position.setType(PositionType.COMMENT);
        position.setUniqueUnitId(0);
        position.setUniqueUnitProductId(0);
        position.setPrice(0);
        position.setAfterTaxPrice(0);
        position.setDescription("Entfernt durch " + operation + ", war: " + position.getName());
        position.setName("Entfernt durch " + operation);
    }

    /**
     * This is maximum crap.
     * We need to verify the very seldom case, that another transaction did in fact modify the Document
     * by adding positions in the same phase, this em was active.
     *
     * @param doc theDocument
     * @return the amount of Positions directly from the Databases.
     */
    // FIXME: Repair this asp, after we switched to the EJB Server.
    private int countPositionsDirect(Document doc) {
        try {
            try (Connection con = redTapeSource.getConnection();
                    Statement stm = con.createStatement();
                    ResultSet rs = stm.executeQuery("SELECT COUNT(id) FROM Position WHERE Document_id = " + doc.getId());) {
                rs.next();
                return rs.getInt(1);
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
}
