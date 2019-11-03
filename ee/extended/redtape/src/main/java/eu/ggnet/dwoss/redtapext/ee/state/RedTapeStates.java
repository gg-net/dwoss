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
package eu.ggnet.dwoss.redtapext.ee.state;

import java.util.HashSet;
import java.util.Set;

import eu.ggnet.dwoss.redtape.ee.entity.Document.Condition;
import eu.ggnet.dwoss.redtape.ee.entity.Document.Directive;
import eu.ggnet.dwoss.redtapext.ee.state.RedTapeStateCharacteristic.Change;
import eu.ggnet.dwoss.redtapext.ee.state.RedTapeStateCharacteristic.Permutation;

import eu.ggnet.dwoss.core.common.values.DocumentType;
import eu.ggnet.dwoss.core.common.values.PaymentMethod;
import eu.ggnet.dwoss.core.common.values.CustomerFlag;

import eu.ggnet.statemachine.State;

import static eu.ggnet.statemachine.State.Type.*;
import static java.util.Arrays.asList;
import static java.util.EnumSet.noneOf;
import static java.util.EnumSet.of;

/**
 * All States for the StateMachine of Document(Type=Order).
 * Abbrev:
 * <ul>
 * <li>I - PICKUP, ADVANCE_PAYMENT</li>
 * <li>II - DISPATCH, ADVANCE_PAYMENT</li>
 * <li>III - DISPATCH, CASH_ON_DELIVERY</li>
 * <li>IV - DISPATCH, DIRECT_DEBIT/INVOICE</li>
 * <li>V - PICKUP, DIRECT_DEBIT/INVOICE</li>
 * </ul>
 * <ul>
 * <li>F000 - No Customer Flags</li>
 * <li>F010 - {@link Flag#CONFIRMED_CASH_ON_DELIVERY}</li>
 * <li>F001 - {@link Flag#CONFIRMS_DOSSIER}</li>
 * <li>F011 - {@link Flag#CONFIRMED_CASH_ON_DELIVERY}, {@link Flag#CONFIRMS_DOSSIER}</li>
 * </ul>
 * <p/>
 * @author oliver.guenther
 */
public class RedTapeStates {

    /**
     * PickUp {@link Type#BLOCK}, {@link PaymentMethod#ADVANCE_PAYMENT}, {@link Condition#CREATED}, {@link Directive#NONE}.
     */
    public static final State<CustomerDocument> CREATED_BLOCK = new State(START, "CREATED_BLOCK",
            new Permutation() {
                {
                    init = new RedTapeStateCharacteristic(DocumentType.BLOCK, PaymentMethod.ADVANCE_PAYMENT, noneOf(Condition.class), Directive.NONE, null, false);
                    flagss = asSet(noneOf(CustomerFlag.class), of(CustomerFlag.CONFIRMED_CASH_ON_DELIVERY), of(CustomerFlag.CONFIRMS_DOSSIER), of(CustomerFlag.CONFIRMED_CASH_ON_DELIVERY, CustomerFlag.CONFIRMS_DOSSIER));
                }
            }.build());

    public static final State<CustomerDocument> CREATED_RETURNS = new State<>(START, "CREATED_RETURNS",
            new RedTapeStateCharacteristic(DocumentType.RETURNS, PaymentMethod.INVOICE, noneOf(Condition.class), Directive.HAND_OVER_GOODS, of(CustomerFlag.SYSTEM_CUSTOMER), false));

    public static final State<CustomerDocument> CREATED_CAPITAL_ASSET = new State<>(START, "CREATED_CAPITAL_ASSET",
            new RedTapeStateCharacteristic(DocumentType.CAPITAL_ASSET, PaymentMethod.INVOICE, noneOf(Condition.class), Directive.HAND_OVER_GOODS, of(CustomerFlag.SYSTEM_CUSTOMER), false));

    public static final State<CustomerDocument> COMPLETED_RETURNS_CAPITAL_ASSETS = new State(END, "COMPLETED_RETURNS",
            new Change(CREATED_RETURNS, of(Condition.PICKED_UP), Directive.NONE).build(),
            new Change(CREATED_CAPITAL_ASSET, of(Condition.PICKED_UP), Directive.NONE).build());

    /**
     * PickUp {@link Type#ORDER}, {@link PaymentMethod#ADVANCE_PAYMENT}, {@link Condition#CREATED}, {@link Directive#WAIT_FOR_MONEY}.
     */
    public static final State<CustomerDocument> I_CREATED_F000 = new State<>(START, "I_CREATED_F000",
            new RedTapeStateCharacteristic(DocumentType.ORDER, PaymentMethod.ADVANCE_PAYMENT, noneOf(Condition.class), Directive.WAIT_FOR_MONEY, noneOf(CustomerFlag.class), false));

    /**
     * PickUp {@link Type#ORDER}, {@link PaymentMethod#ADVANCE_PAYMENT}, {@link Condition#CREATED}, {@link Directive#WAIT_FOR_MONEY}.
     */
    public static final State<CustomerDocument> I_CREATED_F001 = new State<>(START, "I_CREATED_F001",
            new RedTapeStateCharacteristic(DocumentType.ORDER, PaymentMethod.ADVANCE_PAYMENT, noneOf(Condition.class), Directive.WAIT_FOR_MONEY, of(CustomerFlag.CONFIRMS_DOSSIER), false));

    /**
     * PickUp {@link Type#ORDER}, {@link PaymentMethod#ADVANCE_PAYMENT}, {@link Condition#CREATED}, {@link Directive#WAIT_FOR_MONEY}.
     */
    public static final State<CustomerDocument> I_CREATED_F010 = new State<>(START, "I_CREATED_F010",
            new RedTapeStateCharacteristic(DocumentType.ORDER, PaymentMethod.ADVANCE_PAYMENT, noneOf(Condition.class), Directive.WAIT_FOR_MONEY, of(CustomerFlag.CONFIRMED_CASH_ON_DELIVERY), false));

    /**
     * PickUp {@link Type#ORDER}, {@link PaymentMethod#ADVANCE_PAYMENT}, {@link Condition#CREATED}, {@link Directive#WAIT_FOR_MONEY}.
     */
    public static final State<CustomerDocument> I_CREATED_F011 = new State<>(START, "I_CREATED_F011",
            new RedTapeStateCharacteristic(DocumentType.ORDER, PaymentMethod.ADVANCE_PAYMENT, noneOf(Condition.class), Directive.WAIT_FOR_MONEY, of(CustomerFlag.CONFIRMED_CASH_ON_DELIVERY, CustomerFlag.CONFIRMS_DOSSIER), false));

    /**
     * PickUp {@link Type#ORDER}, {@link PaymentMethod#ADVANCE_PAYMENT}, {@link Condition#CREATED} and {@link Condition#PAID}, {@link Directive#CREATE_INVOICE}.
     */
    public static final State<CustomerDocument> I_CREATE_INVOICE = new State("I_CREATE_INVOICE",
            new Permutation() {
                {
                    init = new RedTapeStateCharacteristic(DocumentType.ORDER, PaymentMethod.ADVANCE_PAYMENT, of(Condition.PAID), Directive.CREATE_INVOICE, null, false);
                    flagss = asSet(noneOf(CustomerFlag.class), of(CustomerFlag.CONFIRMED_CASH_ON_DELIVERY), of(CustomerFlag.CONFIRMS_DOSSIER), of(CustomerFlag.CONFIRMED_CASH_ON_DELIVERY, CustomerFlag.CONFIRMS_DOSSIER));
                }
            }.build());

    /**
     * PickUp {@link Type#INVOICE}, {@link PaymentMethod#ADVANCE_PAYMENT}, {@link Condition#CREATED} and
     * {@link Condition#PAID}, {@link Directive#HAND_OVER_GOODS}.
     */
    public static final State<CustomerDocument> I_HAND_OVER_GOODS = new State("I_HAND_OVER_GOODS", new Change(I_CREATE_INVOICE, DocumentType.INVOICE, Directive.HAND_OVER_GOODS).build());

    /**
     * Dispatch {@link Type#ORDER}, {@link PaymentMethod#ADVANCE_PAYMENT}, {@link Condition#CREATED}, {@link Directive#SEND_ORDER}.
     */
    public static final State<CustomerDocument> II_CREATED_F000 = new State<>(START, "II_CREATED_F000",
            new RedTapeStateCharacteristic(DocumentType.ORDER, PaymentMethod.ADVANCE_PAYMENT, noneOf(Condition.class), Directive.SEND_ORDER, noneOf(CustomerFlag.class), true));

    /**
     * Dispatch {@link Type#ORDER}, {@link PaymentMethod#ADVANCE_PAYMENT}, {@link Condition#CREATED}, {@link Directive#SEND_ORDER}.
     */
    public static final State<CustomerDocument> II_CREATED_F001 = new State<>(START, "II_CREATED_F001",
            new RedTapeStateCharacteristic(DocumentType.ORDER, PaymentMethod.ADVANCE_PAYMENT, noneOf(Condition.class), Directive.SEND_ORDER, of(CustomerFlag.CONFIRMS_DOSSIER), true));

    /**
     * Dispatch {@link Type#ORDER}, {@link PaymentMethod#ADVANCE_PAYMENT}, {@link Condition#CREATED}, {@link Directive#SEND_ORDER}.
     */
    public static final State<CustomerDocument> II_CREATED_F010 = new State<>(START, "II_CREATED_F010",
            new RedTapeStateCharacteristic(DocumentType.ORDER, PaymentMethod.ADVANCE_PAYMENT, noneOf(Condition.class), Directive.SEND_ORDER, of(CustomerFlag.CONFIRMED_CASH_ON_DELIVERY), true));

    /**
     * Dispatch {@link Type#ORDER}, {@link PaymentMethod#ADVANCE_PAYMENT}, {@link Condition#CREATED}, {@link Directive#SEND_ORDER}.
     */
    public static final State<CustomerDocument> II_CREATED_F011 = new State<>(START, "II_CREATED_F011",
            new RedTapeStateCharacteristic(DocumentType.ORDER, PaymentMethod.ADVANCE_PAYMENT, noneOf(Condition.class), Directive.SEND_ORDER, of(CustomerFlag.CONFIRMED_CASH_ON_DELIVERY, CustomerFlag.CONFIRMS_DOSSIER), true));

    /**
     * Dispatch {@link Type#ORDER}, {@link PaymentMethod#ADVANCE_PAYMENT}, {@link Condition#CREATED}, {@link Directive#WAIT_FOR_MONEY} or
     * {@link Directive#WAIT_FOR_MONEY_REMINDED}.
     */
    public static final State<CustomerDocument> II_WAIT_FOR_MONEY_F000 = new State("II_WAIT_FOR_MONEY_F000",
            new Change(II_CREATED_F000, Directive.WAIT_FOR_MONEY).build(),
            new Change(II_CREATED_F000, Directive.WAIT_FOR_MONEY_REMINDED).build());

    /**
     * Dispatch {@link Type#ORDER}, {@link PaymentMethod#ADVANCE_PAYMENT}, {@link Condition#CREATED}, {@link Directive#WAIT_FOR_MONEY} or
     * {@link Directive#WAIT_FOR_MONEY_REMINDED}.
     */
    public static final State<CustomerDocument> II_WAIT_FOR_MONEY_F010 = new State("II_WAIT_FOR_MONEY_F010",
            new Change(II_CREATED_F010, Directive.WAIT_FOR_MONEY).build(),
            new Change(II_CREATED_F010, Directive.WAIT_FOR_MONEY_REMINDED).build());

    /**
     * Dispatch {@link Type#ORDER}, {@link PaymentMethod#ADVANCE_PAYMENT}, {@link Condition#CREATED}, {@link Directive#WAIT_FOR_MONEY} or
     * {@link Directive#WAIT_FOR_MONEY_REMINDED}.
     */
    public static final State<CustomerDocument> II_WAIT_FOR_MONEY_F001 = new State("II_WAIT_FOR_MONEY_F001",
            new Change(II_CREATED_F001, Directive.WAIT_FOR_MONEY).build(),
            new Change(II_CREATED_F001, Directive.WAIT_FOR_MONEY_REMINDED).build());

    /**
     * Dispatch {@link Type#ORDER}, {@link PaymentMethod#ADVANCE_PAYMENT}, {@link Condition#CREATED}, {@link Directive#WAIT_FOR_MONEY} or
     * {@link Directive#WAIT_FOR_MONEY_REMINDED}.
     */
    public static final State<CustomerDocument> II_WAIT_FOR_MONEY_F011 = new State("II_WAIT_FOR_MONEY_F011",
            new Change(II_CREATED_F011, Directive.WAIT_FOR_MONEY).build(),
            new Change(II_CREATED_F011, Directive.WAIT_FOR_MONEY_REMINDED).build());

    /**
     * Dispatch {@link Type#ORDER}, {@link PaymentMethod#ADVANCE_PAYMENT}, {@link Condition#CREATED} and
     * {@link Condition#PAID}, {@link Directive#PREPARE_SHIPPING}.
     */
    public static final State<CustomerDocument> II_PREPARE_SHIPPING = new State("II_PREPARE_SHIPPING", new Permutation() {
        {
            init = new RedTapeStateCharacteristic(DocumentType.ORDER, PaymentMethod.ADVANCE_PAYMENT, of(Condition.PAID), Directive.PREPARE_SHIPPING, null, true);
            flagss = asSet(noneOf(CustomerFlag.class), of(CustomerFlag.CONFIRMED_CASH_ON_DELIVERY), of(CustomerFlag.CONFIRMS_DOSSIER), of(CustomerFlag.CONFIRMED_CASH_ON_DELIVERY, CustomerFlag.CONFIRMS_DOSSIER));
        }
    }.build());

    /**
     * Dispatch {@link Type#ORDER}, {@link PaymentMethod#ADVANCE_PAYMENT}, {@link Condition#CREATED} and {@link Condition#PAID} and
     * {@link Condition#SENT}, {@link Directive#CREATE_INVOICE}.
     */
    public static final State<CustomerDocument> II_CREATE_INVOICE = new State("II_CREATE_INVOICE",
            new Change(II_PREPARE_SHIPPING, of(Condition.SENT), Directive.CREATE_INVOICE).build());

    /**
     * Dispatch {@link Type#INVOICE}, {@link PaymentMethod#ADVANCE_PAYMENT}, {@link Condition#CREATED} and
     * {@link Condition#PAID}, {@link Directive#PREPARE_SHIPPING}.
     */
    public static final State<CustomerDocument> II_PREPARE_SHIPPING_INVOICED = new State("II_PREPARE_SHIPPING_INVOICED",
            new Change(II_PREPARE_SHIPPING, DocumentType.INVOICE, Directive.PREPARE_SHIPPING).build());

    /**
     * Dispatch {@link Type#ORDER}, {@link PaymentMethod#CASH_ON_DELIVERY}, {@link Condition#CREATED}, {@link Directive#SEND_CASH_ON_DELIVERY_CONTRACT}.
     */
    public static final State<CustomerDocument> III_CREATED_F001 = new State(START, "III_CREATED_F001",
            new RedTapeStateCharacteristic(DocumentType.ORDER, PaymentMethod.CASH_ON_DELIVERY,
                    noneOf(Condition.class), Directive.SEND_CASH_ON_DELIVERY_CONTRACT, of(CustomerFlag.CONFIRMS_DOSSIER), true));

    /**
     * Dispatch {@link Type#ORDER}, {@link PaymentMethod#CASH_ON_DELIVERY}, {@link Condition#CREATED}, {@link Directive#SEND_CASH_ON_DELIVERY_CONTRACT}.
     */
    public static final State<CustomerDocument> III_CREATED_F000 = new State(START, "III_CREATED_F000",
            new RedTapeStateCharacteristic(DocumentType.ORDER, PaymentMethod.CASH_ON_DELIVERY,
                    noneOf(Condition.class), Directive.SEND_CASH_ON_DELIVERY_CONTRACT, noneOf(CustomerFlag.class), true));

    /**
     * Dispatch {@link Type#ORDER}, {@link PaymentMethod#CASH_ON_DELIVERY}, {@link Condition#CREATED}, {@link Directive#SEND_ORDER}.
     */
    public static final State<CustomerDocument> III_CREATED_F011 = new State(START, "III_CREATED_F011",
            new RedTapeStateCharacteristic(DocumentType.ORDER, PaymentMethod.CASH_ON_DELIVERY,
                    noneOf(Condition.class), Directive.SEND_ORDER, of(CustomerFlag.CONFIRMED_CASH_ON_DELIVERY, CustomerFlag.CONFIRMS_DOSSIER), true));

    public static final State<CustomerDocument> III_PAYMENT_CONTRACT_SENT_F000 = new State("III_PAYMENT_CONTRACT_SENT_F000",
            new Change(III_CREATED_F000, Directive.WAIT_FOR_PAYMENT_CONTRACT_CONFIRMATION).build());

    public static final State<CustomerDocument> III_PAYMENT_CONTRACT_SENT_F001 = new State("III_PAYMENT_CONTRACT_SENT_F001",
            new Change(III_CREATED_F001, Directive.WAIT_FOR_PAYMENT_CONTRACT_CONFIRMATION).build());

    public static final State<CustomerDocument> III_WAIT_FOR_ORDER_CONFIRMATION_F011 = new State("III_WAIT_FOR_ORDER_CONFIRMATION_F011",
            new Change(III_CREATED_F011, Directive.WAIT_FOR_ORDER_CONFIRMATION).build());

    public static final State<CustomerDocument> III_CREATED_PREPARE_SHIPPING_F010 = new State(START, "III_CREATED_PREPARE_SHIPPING_F010",
            new RedTapeStateCharacteristic(DocumentType.ORDER, PaymentMethod.CASH_ON_DELIVERY, noneOf(Condition.class), Directive.PREPARE_SHIPPING, of(CustomerFlag.CONFIRMED_CASH_ON_DELIVERY), true));

    public static final State<CustomerDocument> III_PREPARE_SHIPPING_F011 = new State("III_PREPARE_SHIPPING_F01X",
            new RedTapeStateCharacteristic(DocumentType.ORDER, PaymentMethod.CASH_ON_DELIVERY, of(Condition.CONFIRMED), Directive.PREPARE_SHIPPING, of(CustomerFlag.CONFIRMED_CASH_ON_DELIVERY, CustomerFlag.CONFIRMS_DOSSIER), true));

    /**
     * Dispatch Invoice, {@link PaymentMethod#CASH_ON_DELIVERY}, Unconfirmed, prepare shipping.
     */
    public static final State<CustomerDocument> III_WAIT_FOR_MONEY = new State("III_SENT_INVOICED",
            new Change(III_CREATED_PREPARE_SHIPPING_F010, DocumentType.INVOICE, of(Condition.SENT), Directive.WAIT_FOR_MONEY).build(),
            new Change(III_PREPARE_SHIPPING_F011, DocumentType.INVOICE, of(Condition.SENT), Directive.WAIT_FOR_MONEY).build());

    public static final State<CustomerDocument> IV_CREATED_F001 = new State(START, "IV_CREATED_F001",
            new RedTapeStateCharacteristic(DocumentType.ORDER, PaymentMethod.DIRECT_DEBIT,
                    noneOf(Condition.class), Directive.SEND_ORDER, of(CustomerFlag.CONFIRMS_DOSSIER), true),
            new RedTapeStateCharacteristic(DocumentType.ORDER, PaymentMethod.INVOICE,
                    noneOf(Condition.class), Directive.SEND_ORDER, of(CustomerFlag.CONFIRMS_DOSSIER), true));

    public static final State<CustomerDocument> IV_CREATED_PREPARE_SHIPPING_F000 = new State(START, "IV_CREATED_PREPARE_SHIPPING_F000",
            new RedTapeStateCharacteristic(DocumentType.ORDER, PaymentMethod.DIRECT_DEBIT,
                    noneOf(Condition.class), Directive.PREPARE_SHIPPING, noneOf(CustomerFlag.class), true),
            new RedTapeStateCharacteristic(DocumentType.ORDER, PaymentMethod.DIRECT_DEBIT,
                    noneOf(Condition.class), Directive.PREPARE_SHIPPING, of(CustomerFlag.CONFIRMS_DOSSIER), true),
            new RedTapeStateCharacteristic(DocumentType.ORDER, PaymentMethod.INVOICE,
                    noneOf(Condition.class), Directive.PREPARE_SHIPPING, noneOf(CustomerFlag.class), true),
            new RedTapeStateCharacteristic(DocumentType.ORDER, PaymentMethod.INVOICE,
                    noneOf(Condition.class), Directive.PREPARE_SHIPPING, of(CustomerFlag.CONFIRMS_DOSSIER), true));

    public static final State<CustomerDocument> IV_CREATED_F011 = new State(START, "IV_CREATED_F011",
            new RedTapeStateCharacteristic(DocumentType.ORDER, PaymentMethod.DIRECT_DEBIT,
                    noneOf(Condition.class), Directive.SEND_ORDER, of(CustomerFlag.CONFIRMED_CASH_ON_DELIVERY, CustomerFlag.CONFIRMS_DOSSIER), true),
            new RedTapeStateCharacteristic(DocumentType.ORDER, PaymentMethod.INVOICE,
                    noneOf(Condition.class), Directive.SEND_ORDER, of(CustomerFlag.CONFIRMED_CASH_ON_DELIVERY, CustomerFlag.CONFIRMS_DOSSIER), true));

    public static final State<CustomerDocument> IV_CREATED_PREPARE_SHIPPING_F010 = new State(START, "IV_CREATED_PREPARE_SHIPPING_F010",
            new RedTapeStateCharacteristic(DocumentType.ORDER, PaymentMethod.DIRECT_DEBIT,
                    noneOf(Condition.class), Directive.PREPARE_SHIPPING, of(CustomerFlag.CONFIRMED_CASH_ON_DELIVERY), true),
            new RedTapeStateCharacteristic(DocumentType.ORDER, PaymentMethod.DIRECT_DEBIT,
                    noneOf(Condition.class), Directive.PREPARE_SHIPPING, of(CustomerFlag.CONFIRMED_CASH_ON_DELIVERY, CustomerFlag.CONFIRMS_DOSSIER), true),
            new RedTapeStateCharacteristic(DocumentType.ORDER, PaymentMethod.INVOICE,
                    noneOf(Condition.class), Directive.PREPARE_SHIPPING, of(CustomerFlag.CONFIRMED_CASH_ON_DELIVERY), true),
            new RedTapeStateCharacteristic(DocumentType.ORDER, PaymentMethod.INVOICE,
                    noneOf(Condition.class), Directive.PREPARE_SHIPPING, of(CustomerFlag.CONFIRMED_CASH_ON_DELIVERY, CustomerFlag.CONFIRMS_DOSSIER), true));

    public static final State<CustomerDocument> IV_WAIT_FOR_ORDER_CONFIRMATION_F011 = new State("IV_WAIT_FOR_ORDER_CONFIRMATION_F011",
            new Change(IV_CREATED_F011, Directive.WAIT_FOR_ORDER_CONFIRMATION).build());

    public static final State<CustomerDocument> IV_WAIT_FOR_ORDER_CONFIRMATION_F001 = new State("IV_WAIT_FOR_ORDER_CONFIRMATION_F001",
            new Change(IV_CREATED_F001, Directive.WAIT_FOR_ORDER_CONFIRMATION).build());

    public static final State<CustomerDocument> IV_PREPARE_SHIPPING_F0X1 = new State("IV_PREPARE_SHIPPING_F0X1",
            new Change(IV_WAIT_FOR_ORDER_CONFIRMATION_F001, of(Condition.CONFIRMED), Directive.PREPARE_SHIPPING).build(),
            new Change(IV_WAIT_FOR_ORDER_CONFIRMATION_F011, of(Condition.CONFIRMED), Directive.PREPARE_SHIPPING).build());

    public static final State<CustomerDocument> V_CREATED_WAIT_FOR_PICKUP_F001 = new State(START, "V_CREATED_WAIT_FOR_PICKUP_F001",
            new RedTapeStateCharacteristic(DocumentType.ORDER, PaymentMethod.DIRECT_DEBIT,
                    noneOf(Condition.class), Directive.HAND_OVER_GOODS, of(CustomerFlag.CONFIRMS_DOSSIER), false),
            new RedTapeStateCharacteristic(DocumentType.ORDER, PaymentMethod.INVOICE,
                    noneOf(Condition.class), Directive.HAND_OVER_GOODS, of(CustomerFlag.CONFIRMS_DOSSIER), false));

    public static final State<CustomerDocument> V_CREATED_WAIT_FOR_PICKUP_F000 = new State(START, "V_CREATED_WAIT_FOR_PICKUP_F000",
            new RedTapeStateCharacteristic(DocumentType.ORDER, PaymentMethod.DIRECT_DEBIT,
                    noneOf(Condition.class), Directive.HAND_OVER_GOODS, noneOf(CustomerFlag.class), false),
            new RedTapeStateCharacteristic(DocumentType.ORDER, PaymentMethod.INVOICE,
                    noneOf(Condition.class), Directive.HAND_OVER_GOODS, noneOf(CustomerFlag.class), false));

    public static final State<CustomerDocument> V_CREATED_WAIT_FOR_PICKUP_F011 = new State(START, "V_CREATED_WAIT_FOR_PICKUP_F011",
            new RedTapeStateCharacteristic(DocumentType.ORDER, PaymentMethod.DIRECT_DEBIT,
                    noneOf(Condition.class), Directive.HAND_OVER_GOODS, of(CustomerFlag.CONFIRMED_CASH_ON_DELIVERY, CustomerFlag.CONFIRMS_DOSSIER), false),
            new RedTapeStateCharacteristic(DocumentType.ORDER, PaymentMethod.INVOICE,
                    noneOf(Condition.class), Directive.HAND_OVER_GOODS, of(CustomerFlag.CONFIRMED_CASH_ON_DELIVERY, CustomerFlag.CONFIRMS_DOSSIER), false));

    public static final State<CustomerDocument> V_CREATED_WAIT_FOR_PICKUP_F010 = new State(START, "V_CREATED_WAIT_FOR_PICKUP_F010",
            new RedTapeStateCharacteristic(DocumentType.ORDER, PaymentMethod.DIRECT_DEBIT,
                    noneOf(Condition.class), Directive.HAND_OVER_GOODS, of(CustomerFlag.CONFIRMED_CASH_ON_DELIVERY), false),
            new RedTapeStateCharacteristic(DocumentType.ORDER, PaymentMethod.INVOICE,
                    noneOf(Condition.class), Directive.HAND_OVER_GOODS, of(CustomerFlag.CONFIRMED_CASH_ON_DELIVERY), false));

    public static final State<CustomerDocument> IV_V_WAIT_FOR_MONEY_OR_EXECUTE_DIRECT_DEBIT = new State("IV_V_WAIT_FOR_MONEY_OR_EXECUTE_DIRECT_DEBIT",
            new Change(IV_CREATED_PREPARE_SHIPPING_F000, DocumentType.INVOICE, of(Condition.SENT), Directive.WAIT_FOR_MONEY).build(),
            new Change(IV_CREATED_PREPARE_SHIPPING_F010, DocumentType.INVOICE, of(Condition.SENT), Directive.WAIT_FOR_MONEY).build(),
            new Change(IV_PREPARE_SHIPPING_F0X1, DocumentType.INVOICE, of(Condition.SENT), Directive.WAIT_FOR_MONEY).build(),
            new Change(V_CREATED_WAIT_FOR_PICKUP_F000, DocumentType.INVOICE, of(Condition.PICKED_UP), Directive.WAIT_FOR_MONEY).build(),
            new Change(V_CREATED_WAIT_FOR_PICKUP_F001, DocumentType.INVOICE, of(Condition.PICKED_UP), Directive.WAIT_FOR_MONEY).build(),
            new Change(V_CREATED_WAIT_FOR_PICKUP_F010, DocumentType.INVOICE, of(Condition.PICKED_UP), Directive.WAIT_FOR_MONEY).build(),
            new Change(V_CREATED_WAIT_FOR_PICKUP_F011, DocumentType.INVOICE, of(Condition.PICKED_UP), Directive.WAIT_FOR_MONEY).build());

    public static final State<CustomerDocument> CANCELED = new State(END, "CANCELED",
            new Change(CREATED_CAPITAL_ASSET, of(Condition.CANCELED), Directive.NONE).build(),
            new Change(CREATED_RETURNS, of(Condition.CANCELED), Directive.NONE).build(),
            new Permutation() {
                {
                    init = new RedTapeStateCharacteristic(DocumentType.ORDER, null, of(Condition.CANCELED), Directive.NONE, null, false);
                    flagss = asSet(noneOf(CustomerFlag.class), of(CustomerFlag.CONFIRMED_CASH_ON_DELIVERY), of(CustomerFlag.CONFIRMS_DOSSIER), of(CustomerFlag.CONFIRMED_CASH_ON_DELIVERY, CustomerFlag.CONFIRMS_DOSSIER));
                    paymentMethods = of(PaymentMethod.ADVANCE_PAYMENT, PaymentMethod.CASH_ON_DELIVERY, PaymentMethod.DIRECT_DEBIT, PaymentMethod.INVOICE);
                    permuteDispatch = true;
                }
            }.build());

    public static final State<CustomerDocument> SALE_COMPLETED = new State(END, "SALE_COMPLETED",
            new Change(I_HAND_OVER_GOODS, of(Condition.PICKED_UP), Directive.NONE).build(),
            new Change(II_PREPARE_SHIPPING, DocumentType.INVOICE, of(Condition.SENT), Directive.NONE).build(),
            new Change(III_WAIT_FOR_MONEY, of(Condition.PAID), Directive.NONE).build(),
            new Change(IV_V_WAIT_FOR_MONEY_OR_EXECUTE_DIRECT_DEBIT, of(Condition.PAID), Directive.NONE).build());

    public static final State<CustomerDocument> COMPLAINT_CREATED = new State("COMPLAINT",
            new Change(SALE_COMPLETED, DocumentType.COMPLAINT, Directive.WAIT_FOR_COMPLAINT_COMPLETION).build());

    public static final State<CustomerDocument> COMPLAINT_ABORTED = new State(END, "COMPLAINT_ABORT",
            new Change(COMPLAINT_CREATED, of(Condition.WITHDRAWN), Directive.NONE).build(),
            new Change(COMPLAINT_CREATED, of(Condition.REJECTED), Directive.NONE).build());

    public static final State<CustomerDocument> COMPLAINT_ACCEPTED = new State(END, "COMPLAINT_ACCEPTED",
            new Change(COMPLAINT_CREATED, of(Condition.ACCEPTED), Directive.CREATE_CREDIT_MEMO_OR_ANNULATION_INVOICE).build());

    public static final State<CustomerDocument> CREDIT_MEMO_CREATED = new State("CREDIT_MEMO_CREATED",
            new Change(SALE_COMPLETED, DocumentType.CREDIT_MEMO, Directive.BALANCE_REPAYMENT).build(),
            new Change(COMPLAINT_ACCEPTED, DocumentType.CREDIT_MEMO, Directive.BALANCE_REPAYMENT).build());

    public static final State<CustomerDocument> ANNULATION_INVOICE_CREATED = new State("ANNULATION_INVOICE_CREATED",
            new Change(SALE_COMPLETED, DocumentType.ANNULATION_INVOICE, Directive.BALANCE_REPAYMENT).build(),
            new Change(COMPLAINT_ACCEPTED, DocumentType.ANNULATION_INVOICE, Directive.BALANCE_REPAYMENT).build());

    public static final State<CustomerDocument> REPAYMENT_BALLANCED = new State(END, "REPAYMENT_BALLANCED",
            new Change(CREDIT_MEMO_CREATED, of(Condition.REPAYMENT_BALANCED), Directive.NONE).build(),
            new Change(ANNULATION_INVOICE_CREATED, of(Condition.REPAYMENT_BALANCED), Directive.NONE).build());

    @SafeVarargs
    private static <T> Set<Set<T>> asSet(Set<T>... in) {
        return new HashSet<>(asList(in));
    }
}
