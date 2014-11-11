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
package eu.ggnet.dwoss.receipt;

import eu.ggnet.dwoss.stock.entity.StockUnit;
import eu.ggnet.dwoss.stock.entity.Shipment;
import eu.ggnet.dwoss.stock.entity.StockTransaction;

import java.io.Serializable;

import javax.ejb.Local;
import javax.ejb.Remote;

import eu.ggnet.dwoss.rules.ReceiptOperation;
import eu.ggnet.dwoss.uniqueunit.entity.Product;
import eu.ggnet.dwoss.uniqueunit.entity.UniqueUnit;

import eu.ggnet.dwoss.util.UserInfoException;

import lombok.Data;

/**
 *
 * @author oliver.guenther
 */
@Remote
@Local
public interface UnitProcessor {

    @Data
    public static class ModifiedDossiers implements Serializable {

        /**
         * DossierId for which a remove has happened.
         */
        private final Long removedFromId;

        /**
         * DossierId to which an ad has happened.
         */
        private final Long addedToId;
    }

    @Data
    public static class EditableUnit implements Serializable {

        private final UniqueUnit uniqueUnit;

        private final StockUnit stockUnit;

        private final ReceiptOperation operation;

        private final String partNo;
    }

    /**
     * Receipts a new Unit.
     * Multiphase Process:
     * <ol>
     * <li>Validation and throw IllegalArgumentException if
     * <ul>
     * <li>Supplied UniqueUnit is already persistent</li>
     * <li>RefurbishedId is already taken [UniqueUnit &amp; Sopo]</li>
     * <li>Serial is already taken and Unit is in Stock [UniqueUnit &amp; Stock]</li>
     * </ul>
     * </li>
     * <li>If serial is taken update existing Unit else persist Unit[UniqueUnit]<br />
     * (At this point it implies that the Unit is not in Stock)</li>
     * <li>Create, weak reference StockUnit and prepare for rollIn [Stock]</li>
     * <li>Create and weak reference SopoUnit [Sopo]</li>
     * <li>Execute Operation [Sopo]:
     * <ul>
     * <li>If Operation == Sales, nothing more to do</li>
     * <li>Else findByTypeAndStatus the KundenId from Contractor and Operation -> create or reuse a Auftrag and add the SopoUnit</li>
     * </ul>
     * </li>
     * </ol>
     * <p/>
     * @param shipment         the shipment
     * @param product
     * @param recieptUnit      the UniqueUnit to be receipt, must not be null
     * @param transaction
     * @param operation        the Operation to do
     * @param arranger
     * @param operationComment
     * @throws IllegalArgumentException if validation fails
     */
    void receipt(UniqueUnit recieptUnit, Product product, Shipment shipment, StockTransaction transaction,
                 ReceiptOperation operation, String operationComment, String arranger) throws IllegalArgumentException;

    /**
     * Updates the UniqueUnit.
     * Multiphase Process.
     * <ol>
     * <li>Validate all input data.</li>
     * <li>Merge UniqueUnit and set Product</li>
     * <li>Overwrite StockUnit</li>
     * </ol>
     *
     * @param uniqueUnit       the UniqueUnit, must not be null
     * @param product          the Product, must not be null
     * @param updateOperation  the operation, must not be null
     * @param operationComment the comment of the operation, may be null
     * @param arranger
     * @throws IllegalArgumentException
     */
    void update(UniqueUnit uniqueUnit, Product product, ReceiptOperation updateOperation, String operationComment, String arranger) throws IllegalArgumentException;

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
    EditableUnit findEditableUnit(String refurbishedIdOrSerial) throws UserInfoException;

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
    UniqueUnit transfer(UniqueUnit uniqueUnit, int stockId, String arranger);
}
