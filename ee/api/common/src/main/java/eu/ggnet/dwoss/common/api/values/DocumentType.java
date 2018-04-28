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
package eu.ggnet.dwoss.common.api.values;

import java.util.EnumSet;

import lombok.ToString;

/**
 * The possible Types of Documents.
 * Hint on notation: A short notation is used. A CreditMemo can only exist if a Invoice exits means a Dossier can only have an active Document of type
 * CreditMemo if
 * the dossier has an active Document of type Invoice.
 * Hint: Only on this component to have type safty in the Mandator.
 */
@ToString
public enum DocumentType {

    /**
     * Represents an order.
     * <p/>
     * Predecessors:
     * <ul>
     * <li>None</li>
     * </ul>
     * Successors:
     * <ul>
     * <li>Invoice</li>
     * <li>None</li>
     * </ul>
     * Constraints:
     * <ul>
     * <li>There may exist only one active Order.</li>
     * </ul>
     * Workflow on Create/Update if a Position of type Unit exists:
     * <ul>
     * <li>Request a referencing stock.LogicTransaction</li>
     * <li>Check if the stock.LogicTransaction has the StockUnit:
     * <ul>
     * <li>If it doesn't, check if the StockUnit is available:
     * <ul>
     * <li>If not, <b>FAIL</b></li>
     * <li>Else add it to the stock.LogicTransaction</li>
     * </ul>
     * </li>
     * </ul>
     * </li>
     * </ul>
     */
    ORDER("Bestellung"), /**
     * Represents an invoice.
     * <p/>
     * Predecessors:
     * <ul>
     * <li>Order</li>
     * <li>None</li>
     * </ul>
     * Successors:
     * <ul>
     * <li>CreditMemo</li>
     * <li>None</li>
     * </ul>
     * Constraints:
     * <ul>
     * <li>There may exist only one active Invoice.</li>
     * <li>After the creation of an Invoice the Order must not be changed.</li>
     * <li>If Document.isClosed, only changes of Document.changesAllowed are allowed</li>
     * </ul>
     * Workflow on Create/Update if a Position of type Unit exists: see {@link PositionType#ORDER}
     * <p/>
     * Workflow on Rollout of StockUnits (close of last week):
     * <ul>
     * <li>Set Document.closed to true</li>
     * </ul>
     */
    INVOICE("Rechnung", "RS"), /**
     * Represents a complaint.
     * <p/>
     * Predecessors:
     * <ul>
     * <li>Invoice</li>
     * </ul>
     * Successors:
     * <ul>
     * <li>annulation Invoice</li>
     * <li>CreditMemo</li>
     * </ul>
     * Constraints:
     * <ul>
     * <li>A complaint can only exist if a Invoice exits.</li>
     * <li>The weight of all complaint can only be as much as the weight of the Invoice.</li>
     * <li>After the creation of a complaint the Invoice must not be changed.</li>
     * <li>If the Dossier, was closed, reopen it.</li>
     * <li>A once added Position of any Type except {@link PositionType#COMMENT} must not be removed or changed</li>
     * </ul>
     * Workflow on Create/Update: No Changes on Stock<br />
     * Hint: On the closing Operation, if the Invoice is still open, keep it that way and do not report.
     */
    COMPLAINT("Reklamation"),
    /**
     * Represents a annulation invoice.
     * <p/>
     * Predecessors:
     * <ul>
     * <li>Invoice</li>
     * <li>Complaint</li>
     * </ul>
     * Successors:
     * <ul>
     * <li>None</li>
     * </ul>
     * Constraints:
     * <ul>
     * <li>The weight of all AnulationInvoice can only be as much as the negative weight of the Invoice.</li>
     * <li>After the creation of a AnulationInvoice the Invoice must not be changed.</li>
     * <li>If the Dossier, was closed, reopen it.</li>
     * <li>A once added Position of any Type except {@link PositionType#COMMENT} must not be removed or changed</li>
     * </ul>
     * Workflow on Create/Update if a Position of type Unit exists:
     * <ul>
     * <li>Validate if a Position of the Type Unit exists on the Invoice</li>
     * <li>Check existence of a referenced stock.LogicTransaction and containing the referenced StockUnit:
     * <ul>
     * <li>If exists and has the StockUnit &rArr; Remove StockUnit from stock.LogicTransaction</li>
     * <li>Else &rArr; RollIn a StockUnit</li>
     * </ul>
     * </li>
     * <li>Create a new Dossier with Document of {@link DocumentType#BLOCK} in the StockUnit &rarr; UniqueUnit.contractor referencing creditMemoCustomer and
     * add the Unit</li>
     * <li>Add a Comment to this Document, containing the arranger, the cause and the Identifier of the CreditMemo</li>
     * <li>Add a Comment to the UniqueUnit, containing the arranger, the cause and the Identifier of the CreditMemo</li>
     * </ul>
     */
    ANNULATION_INVOICE("Stornorechnung", "SR"), /**
     * Represents a partial or full credit memo to an invoice.
     * <p/>
     * Predecessors:
     * <ul>
     * <li>Invoice</li>
     * <li>Complaint</li>
     * </ul>
     * Successors:
     * <ul>
     * <li>None</li>
     * </ul>
     * Constraints:
     * <ul>
     * <li>The weight of all CreditMemos can only be as much as the weight of the Invoice.</li>
     * <li>After the creation of a CreditMemo the Invoice must not be changed.</li>
     * <li>If the Document, was closed, reopen it.</li>
     * <li>A once added Position of any Type except {@link PositionType#COMMENT} must not be removed or changed</li>
     * </ul>
     * Workflow on Create/Update if a Position of type Unit exists:
     * <ul>
     * <li>Validate if a Position of the Type Unit exists on the Invoice</li>
     * <li>Check existence of a referenced stock.LogicTransaction and containing the referenced StockUnit:
     * <ul>
     * <li>If exists and has the StockUnit &rArr; Remove StockUnit from stock.LogicTransaction</li>
     * <li>Else &rArr; RollIn a StockUnit</li>
     * </ul>
     * </li>
     * <li>Create a new Dossier with Document of {@link DocumentType#BLOCK} in the StockUnit &rarr; UniqueUnit.contractor referencing creditMemoCustomer and
     * add the Unit</li>
     * <li>Add a Comment to this Document, containing the arranger, the cause and the Identifier of the CreditMemo</li>
     * <li>Add a Comment to the UniqueUnit, containing the arranger, the cause and the Identifier of the CreditMemo</li>
     * </ul>
     */
    CREDIT_MEMO("Gutschrift", "GS"), /**
     * Represents a Blocker, that cannot change any state.
     * <p/>
     * Predecessors:
     * <ul>
     * <li>None</li>
     * </ul>
     * Successors:
     * <ul>
     * <li>None</li>
     * </ul>
     * Constraints:
     * <ul>
     * <li>A Blocker can only exist on a SystemCustomer.</li>
     * </ul>
     */
    BLOCK("Blocker"), /**
     * Represents an Returns to a commission contractor.
     * <p/>
     * Predecessors:
     * <ul>
     * <li>None</li>
     * </ul>
     * Successors:
     * <ul>
     * <li>None</li>
     * </ul>
     * Constraints:
     * <ul>
     * <li>There may exist only one active Returns.</li>
     * <li>The Contractor of the Unit must be equivalent to the Customer.</li>
     * <li>Allowed Position Types are Unit, Comment</li>
     * </ul>
     * Allowed Constraint Values: CANCELED and PICKED_UP.
     * <p />
     * Workflow on Create or Update if a Position of type Unit exists:
     * <ul>
     * <li>Request a referencing stock.LogicTransaction</li>
     * <li>Check if the stock.LogicTransaction has the StockUnit:
     * <ul>
     * <li>If it doesn't, check if the StockUnit is available:
     * <ul>
     * <li>If not, <b>FAIL</b></li>
     * <li>Else add it to the stock.LogicTransaction</li>
     * </ul>
     * </li>
     * </ul>
     * </li>
     * </ul>
     */
    RETURNS("Rückläufer"), /**
     * Represents a Capital Asset for GG-Net.
     * <p/>
     * Predecessors:
     * <ul>
     * <li>None</li>
     * </ul>
     * Successors:
     * <ul>
     * <li>None</li>
     * </ul>
     * Constraints:
     * <ul>
     * <li>There may exist only one active Capital Asset.</li>
     * <li>It can only exist on the Company Customer (KID=34)</li>
     * <li>Allowed Position Types are Unit, Comment</li>
     * </ul>
     * Allowed Constraint Values: CANCELED and PICKED_UP.
     * <p />
     * Workflow on Create or Update if a Position of type Unit exists:
     * <ul>
     * <li>Request a referencing stock.LogicTransaction</li>
     * <li>Check if the stock.LogicTransaction has the StockUnit:
     * <ul>
     * <li>If it doesn't, check if the StockUnit is available:
     * <ul>
     * <li>If not, <b>FAIL</b></li>
     * <li>Else add it to the stock.LogicTransaction</li>
     * </ul>
     * </li>
     * </ul>
     * </li>
     * </ul>
     */
    CAPITAL_ASSET("Anlagevermögen", "AN");

    private final String name;

    /**
     * the MatchCode (eg Invoice = SR, CreditMemo = GS).
     */
    private final String matchCode;

    private DocumentType(String name, String matchCode) {
        this.name = name;
        this.matchCode = matchCode;
    }

    private DocumentType(String name) {
        this(name, null);
    }

    public String getName() {
        return name;
    }

    /**
     * Returns the MatchCode (e.g. Invoice = SR, CreditMemo = GS)
     *
     * @return the MatchCode, may be null.
     */
    public String getMatchCode() {
        return matchCode;
    }

    /**
     * Returns true if type is repayment.
     * <p>
     * @param type the type to check
     * @return true if type is repayment.
     */
    public static boolean isRepayment(DocumentType type) {
        return EnumSet.of(CREDIT_MEMO, ANNULATION_INVOICE).contains(type);
    }

}
