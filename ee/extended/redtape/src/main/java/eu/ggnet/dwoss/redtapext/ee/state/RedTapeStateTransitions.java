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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import eu.ggnet.dwoss.redtape.ee.entity.Document.Condition;
import eu.ggnet.dwoss.redtape.ee.entity.Document.Directive;
import eu.ggnet.dwoss.redtapext.ee.state.RedTapeStateTransition.Hint;
import eu.ggnet.statemachine.State.Type;

import eu.ggnet.dwoss.rights.api.AtomicRight;
import eu.ggnet.dwoss.rules.DocumentType;
import eu.ggnet.dwoss.rules.PaymentMethod;
import eu.ggnet.dwoss.rules.CustomerFlag;

import static java.util.EnumSet.*;

/**
 * Transition for RedTape.
 * This list all the possible business options for crossing form stat a to stat b
 * <p>
 * <p>
 * Stats can be set or added:
 * Dossier -- Rekord/Akte
 * Directive -- Weisungen
 * PaymentMethod -- Bezahlverfahren
 * DocumentType (INVOICE/COMPLAINT .. ) (RECHNUNG / BESCHWERDE ... ) -- Typ des Dokuments
 * or
 * Condition (PAID/PICKED_UP/CANCELED/ ... (BEZAHLT/ABGEHOLT/ABGEBROCHEN/VERSAND/ ... ) -- Zustand/Bedingung
 * <p>
 * <p/>
 * @author oliver.guenther
 */
public class RedTapeStateTransitions {

    /**
     * Sets {@link Type#INVOICE}, {@link Directive#HAND_OVER_GOODS} and adds {@link Condition#PAID}.
     */
    public final static RedTapeStateTransition I_PAY_AND_INVOICE = new RedTapeStateTransition(
            "I_PAY_AND_INVOICE",
            "Bezahlt und Rechnung erstellen",
            "Der Auftrag ist bezahlt und eine Rechnung wurde erstellt.", of(Hint.CREATES_INVOICE, Hint.ADDS_SETTLEMENT)) {
        @Override
        public void apply(CustomerDocument instance) {
            instance.getDocument().add(Condition.PAID);
            instance.getDocument().setType(DocumentType.INVOICE);
            instance.getDocument().setDirective(Directive.HAND_OVER_GOODS);
        }
    };

    /**
     * Sets {@link Type#INVOICE}, {@link Directive#NONE} and adds {@link Condition#PICKED_UP} {@link Condition#PAID}
     */
    public final static RedTapeStateTransition I_PAY_AND_INVOICE_PICK_UP = new RedTapeStateTransition(
            "I_PAY_AND_INVOICE_PICK_UP",
            "Bezahlt,Abgeholt und Rechnung erstellen",
            "Der Auftrag ist bezahlt, die Ware wurde abgeholt/übergeben und eine Rechnung wird erstellt.",
            of(Hint.CREATES_INVOICE, Hint.ADDS_SETTLEMENT, Hint.UNIT_LEAVES_STOCK)) {
        @Override
        public void apply(CustomerDocument instance) {
            instance.getDocument().add(Condition.PAID);
            instance.getDocument().add(Condition.PICKED_UP);
            instance.getDocument().setType(DocumentType.INVOICE);
            instance.getDocument().setDirective(Directive.NONE);
        }
    };

    /**
     * Sets {@link Type#INVOICE} and {@link Directive#HAND_OVER_GOODS}.
     */
    public final static RedTapeStateTransition I_INVOICE = new RedTapeStateTransition(
            "I_INVOICE",
            "Rechnung erstellt",
            "Es wurde eine Rechnung erstellt.", of(Hint.CREATES_INVOICE)) {
        @Override
        public void apply(CustomerDocument instance) {
            instance.getDocument().setType(DocumentType.INVOICE);
            instance.getDocument().setDirective(Directive.HAND_OVER_GOODS);
        }
    };

    /**
     * Sets {@link Directive#PREPARE_SHIPPING} and adds {@link Condition#PAID}.
     */
    public final static RedTapeStateTransition II_PAY = new RedTapeStateTransition(
            "II_PAY",
            "Bezahlt",
            "Der Auftrag ist bezahlt, die Ware kann nun versendet werden.", of(Hint.ADDS_SETTLEMENT)) {
        @Override
        public void apply(CustomerDocument instance) {
            instance.getDocument().add(Condition.PAID);
            instance.getDocument().setDirective(Directive.PREPARE_SHIPPING);
        }
    };

    /**
     * Sets {@link Condition#PAID}, {@link Directive#CREATE_INVOICE}.
     */
    public final static RedTapeStateTransition I_PAY = new RedTapeStateTransition(
            "I_PAY",
            "Bezahlt",
            "Der Auftrag ist bezahlt, eine Rechnung muss noch erstellt werden.", of(Hint.ADDS_SETTLEMENT)) {
        @Override
        public void apply(CustomerDocument instance) {
            instance.getDocument().add(Condition.PAID);
            instance.getDocument().setDirective(Directive.CREATE_INVOICE);
        }
    };

    /**
     * Sets {@link Directive#WAIT_FOR_MONEY}.
     */
    public final static RedTapeStateTransition II_BRIEF = new RedTapeStateTransition(
            "II_BRIEF",
            "Informiert",
            "Der Kunde wurde über den Auftrag via eMail, Post oder direkt informiert. Es wird auf Geldeingang gewartet.", of(Hint.SENDED_INFORMATION)) {
        @Override
        public void apply(CustomerDocument instance) {
            instance.getDocument().setDirective(Directive.WAIT_FOR_MONEY);
        }
    };

    /**
     * Sets {@link Directive#WAIT_FOR_MONEY_REMINDED}
     */
    public final static RedTapeStateTransition BRIEF_10_DAYS = new RedTapeStateTransition(
            "I_BRIEF_10_DAYS",
            "Informiert",
            "Der Kunde wurde über den Auftrag via eMail, Post oder direkt informiert. Es wird 10 Tage auf Geldeingang gewartet. Dies kann eine Erinnerung sein.", of(Hint.SENDED_INFORMATION)) {
        @Override
        public void apply(CustomerDocument instance) {
            instance.getDocument().setDirective(Directive.WAIT_FOR_MONEY_REMINDED);
        }
    };

    /**
     * Sets {@link Directive#NONE} and adds {@link Condition#PICKED_UP}
     */
    public final static RedTapeStateTransition I_PICK_UP = new RedTapeStateTransition(
            "I_PICK_UP",
            "Abgeholt",
            "Die Ware wurde abgeholt.", of(Hint.UNIT_LEAVES_STOCK)) {
        @Override
        public void apply(CustomerDocument instance) {
            instance.getDocument().add(Condition.PICKED_UP);
            instance.getDocument().setDirective(Directive.NONE);
        }
    };

    /**
     * Sets {@link PaymentMethod#DIRECT_DEBIT} and adds {@link Condition#PAID}.
     */
    public final static RedTapeStateTransition I_SET_DIRECT_DEBIT = new RedTapeStateTransition(
            "I_SET_DIRECT_DEBIT",
            "Zahlung: Lastschrift",
            "Die Zahlungsmodalität wird auf Lastschrift geändert.", of(Hint.CHANGES_PAYMENT_METHOD_TO_DIRECT_DEBIT)) {
        @Override
        public void apply(CustomerDocument instance) {
            instance.getDocument().getDossier().setPaymentMethod(PaymentMethod.DIRECT_DEBIT);
            instance.getDocument().setDirective(Directive.HAND_OVER_GOODS);
        }
    };

    /**
     * Sets {@link PaymentMethod#INVOICE} and adds {@link Directive#HAND_OVER_GOODS}.
     */
    public final static RedTapeStateTransition I_SET_INVOICE = new RedTapeStateTransition(
            "I_SET_INVOICE",
            "Zahlung: Rechnung",
            "Die Zahlungsmodalität wird auf Rechnung geändert.", of(Hint.CHANGES_PAYMENT_METHOD_TO_INVOICE)) {
        @Override
        public void apply(CustomerDocument instance) {
            instance.getDocument().getDossier().setPaymentMethod(PaymentMethod.INVOICE);
            instance.getDocument().setDirective(Directive.HAND_OVER_GOODS);
        }
    };

    /**
     * Sets {@link Directive#NONE} and adds {@link Condition#CANCELED}
     */
    public final static RedTapeStateTransition CANCEL = new RedTapeStateTransition(
            "CANCEL",
            "Stornieren",
            "Der Kunde wünscht diesen Auftrag nicht mehr, vollständig stornieren.") {
        @Override
        public void apply(CustomerDocument instance) {
            instance.getDocument().add(Condition.CANCELED);
            instance.getDocument().setDirective(Directive.NONE);
        }
    };

    /**
     * Sets {@link Directive#CREATE_INVOICE} and adds {@link Condition#SENT}
     */
    public final static RedTapeStateTransition II_SEND_WITHOUT_INVOICE = new RedTapeStateTransition(
            "II_SEND_WITHOUT_INVOICE",
            "Versendet",
            "Alle Positionen des Auftrags wurden versendet, eine Rechnung muss noch erstellt werden.", of(Hint.UNIT_LEAVES_STOCK)) {
        @Override
        public void apply(CustomerDocument instance) {
            instance.getDocument().add(Condition.SENT);
            instance.getDocument().setDirective(Directive.CREATE_INVOICE);
        }
    };

    /**
     * Sets {@link Type#INVOICE}, {@link Directive#PREPARE_SHIPPING}.
     */
    public final static RedTapeStateTransition II_INVOICE_UNSHIPPED = new RedTapeStateTransition(
            "II_INVOICE_UNSHIPPED",
            "Rechnung erstellen",
            "Der Auftrag wird zur Rechnung weitergeführt, die Ware muss noch versendet werden.", of(Hint.CREATES_INVOICE)) {
        @Override
        public void apply(CustomerDocument instance) {
            instance.getDocument().setType(DocumentType.INVOICE);
            instance.getDocument().setDirective(Directive.PREPARE_SHIPPING);
        }
    };

    /**
     * Sets {@link Type#INVOICE}, {@link Directive#NONE}.
     */
    public final static RedTapeStateTransition II_INVOICE = new RedTapeStateTransition(
            "II_INVOICE",
            "Rechnung erstellen",
            "Der Auftrag wird zur Rechnung weitergeführt. Verkauf abgeschlossen.", of(Hint.CREATES_INVOICE)) {
        @Override
        public void apply(CustomerDocument instance) {
            instance.getDocument().setType(DocumentType.INVOICE);
            instance.getDocument().setDirective(Directive.NONE);
        }
    };

    /**
     * Sets {@link Directive#NONE} and adds {@link Condition#SENT}.
     */
    public final static RedTapeStateTransition II_SEND = new RedTapeStateTransition(
            "II_SEND",
            "Versendet",
            "Alle Positionen des Auftrags wurden versendet. Verkauf abgeschlossen.", of(Hint.UNIT_LEAVES_STOCK)) {
        @Override
        public void apply(CustomerDocument instance) {
            instance.getDocument().add(Condition.SENT);
            instance.getDocument().setDirective(Directive.NONE);
        }
    };

    /**
     * Sets {@link Type#INVOICE}, {@link Directive#NONE} and adds {@link Condition#SENT}.
     */
    public final static RedTapeStateTransition II_SEND_AND_INVOICE = new RedTapeStateTransition(
            "II_SEND_AND_INVOICE",
            "Versendet und Rechnung erstellen",
            "Alle Positionen des Auftrags wurden versendet, der Auftrag wird zu einer Rechnung weiter geführt", of(Hint.CREATES_INVOICE, Hint.UNIT_LEAVES_STOCK)) {
        @Override
        public void apply(CustomerDocument instance) {
            instance.getDocument().add(Condition.SENT);
            instance.getDocument().setType(DocumentType.INVOICE);
            instance.getDocument().setDirective(Directive.NONE);
        }
    };

    /**
     * Sets {@link Directive#PREPARE_SHIPPING} and adds {@link PaymentMethod#DIRECT_DEBIT}.
     */
    public final static RedTapeStateTransition II_SET_DIRECT_DEBIT_F0X0 = new RedTapeStateTransition(
            "II_SET_DIRECT_DEBIT_F0X0",
            "Zahlung: Lastschrift",
            "Die Zahlungsmodalitäten auf Lastschrift ändern.", of(Hint.CHANGES_PAYMENT_METHOD_TO_DIRECT_DEBIT)) {
        @Override
        public void apply(CustomerDocument instance) {
            instance.getDocument().getDossier().setPaymentMethod(PaymentMethod.DIRECT_DEBIT);
            instance.getDocument().setDirective(Directive.PREPARE_SHIPPING);
        }
    };

    /**
     * Sets {@link Directive#SEND_ORDER} and adds {@link PaymentMethod#DIRECT_DEBIT}.
     */
    public final static RedTapeStateTransition II_SET_DIRECT_DEBIT_F0X1 = new RedTapeStateTransition(
            "II_SET_DIRECT_DEBIT_F0X1",
            "Zahlung: Lastschrift",
            "Die Zahlungsmodalitäten auf Lastschrift ändern.", of(Hint.CHANGES_PAYMENT_METHOD_TO_DIRECT_DEBIT)) {
        @Override
        public void apply(CustomerDocument instance) {
            instance.getDocument().getDossier().setPaymentMethod(PaymentMethod.DIRECT_DEBIT);
            instance.getDocument().setDirective(Directive.SEND_ORDER);
        }
    };

    /**
     * Sets {@link Directive#PREPARE_SHIPPING} and adds {@link PaymentMethod#INVOICE}.
     */
    public final static RedTapeStateTransition II_SET_INVOICE_F0X0 = new RedTapeStateTransition(
            "II_SET_INVOICE_F0X0",
            "Zahlung: Rechnung",
            "Die Zahlungsmodalitäten auf Rechnung ändern.", of(Hint.CHANGES_PAYMENT_METHOD_TO_INVOICE)) {
        @Override
        public void apply(CustomerDocument instance) {
            instance.getDocument().getDossier().setPaymentMethod(PaymentMethod.INVOICE);
            instance.getDocument().setDirective(Directive.PREPARE_SHIPPING);
        }
    };

    /**
     * Sets {@link Directive#SEND_ORDER} and adds {@link PaymentMethod#INVOICE}.
     */
    public final static RedTapeStateTransition II_SET_INVOICE_F0X1 = new RedTapeStateTransition(
            "II_SET_INVOICE_F0X1",
            "Zahlung: Rechnung",
            "Die Zahlungsmodalitäten auf Rechnung ändern.", of(Hint.CHANGES_PAYMENT_METHOD_TO_INVOICE)) {
        @Override
        public void apply(CustomerDocument instance) {
            instance.getDocument().getDossier().setPaymentMethod(PaymentMethod.INVOICE);
            instance.getDocument().setDirective(Directive.SEND_ORDER);
        }
    };

    /**
     * Sets {@link Directive#WAIT_FOR_PAYMENT_CONTRACT_CONFIRMATION}.
     */
    public final static RedTapeStateTransition SEND_PAYMENT_CONTRACT = new RedTapeStateTransition(
            "SEND_PAYMENT_CONTRACT",
            "Nachnahmebedingungen zugesendet",
            "Der Kunde wurde über die Nachnahmebedingungen informiert. Es wird auf eine Bestätigung gewartet.", of(Hint.SENDED_INFORMATION)) {
        @Override
        public void apply(CustomerDocument instance) {
            instance.getDocument().setDirective(Directive.WAIT_FOR_PAYMENT_CONTRACT_CONFIRMATION);
        }
    };

    /**
     * Sets {@link Directive#PREPARE_SHIPPING} and add {@link CustomerFlag#CONFIRMED_CASH_ON_DELIVERY} .
     */
    public final static RedTapeStateTransition III_CONFIRM_PAYMENT_CONTRACT_F000 = new RedTapeStateTransition(
            "III_CONFIRM_PAYMENT_CONTRACT_F000",
            "Nachnahmebedingungen bestätigt",
            "Der Kunde hat die Nachnahmebedingungen erhalten und bestätigt.") {
        @Override
        public void apply(CustomerDocument instance) {
            instance.getCustomerFlags().add(CustomerFlag.CONFIRMED_CASH_ON_DELIVERY);
            instance.getDocument().setDirective(Directive.PREPARE_SHIPPING);
        }
    };

    /**
     * Sets {@link Directive#SEND_ORDER} and add {@link CustomerFlag#CONFIRMED_CASH_ON_DELIVERY} .
     */
    public final static RedTapeStateTransition III_CONFIRM_PAYMENT_CONTRACT_F001 = new RedTapeStateTransition(
            "III_CONFIRM_PAYMENT_CONTRACT_F001",
            "Nachnahme bestätigt",
            "Der Kunde hat die Nachnahmebedingungen erhalten und bestätigt.") {
        @Override
        public void apply(CustomerDocument instance) {
            instance.getCustomerFlags().add(CustomerFlag.CONFIRMED_CASH_ON_DELIVERY);
            instance.getDocument().setDirective(Directive.SEND_ORDER);
        }
    };

    /**
     * Sets {@link Directive#PREPARE_SHIPPING} and add {@link Condition#CONFIRMED}.
     */
    public final static RedTapeStateTransition III_IV_ORDER_CONFIRMED = new RedTapeStateTransition(
            "III_IV_ORDER_CONFIRMED",
            "Versenden",
            "Der Kunde hat den Auftrag bestätigt und angenommen. Der Auftrag wird zum Versand vorbereitet.") {
        @Override
        public void apply(CustomerDocument instance) {
            instance.getDocument().setDirective(Directive.PREPARE_SHIPPING);
            instance.getDocument().add(Condition.CONFIRMED);
        }
    };

    /**
     * Sets {@link Directive#WAIT_FOR_ORDER_CONFIRMATION}.
     */
    public final static RedTapeStateTransition III_IV_SEND_ORDER = new RedTapeStateTransition(
            "III_IV_SEND_ORDER",
            "Informiert",
            "Der Kunde wurde über den Auftrag informiert. Es wird auf eine Bestätigung gewartet.", of(Hint.SENDED_INFORMATION)) {
        @Override
        public void apply(CustomerDocument instance) {
            instance.getDocument().setDirective(Directive.WAIT_FOR_ORDER_CONFIRMATION);
        }
    };

    /**
     * Sets {@link Type#INVOICE}, {@link Directive#WAIT_FOR_MONEY} and adds {@link Condition#SENT}.
     */
    public final static RedTapeStateTransition III_IV_SEND_AND_INVOICE = new RedTapeStateTransition(
            "III_IV_SEND_AND_INVOICE",
            "Versendet und Rechnung erstellt",
            "Alle Positionen des Auftrags wurden versendet, der Auftrag wird zu einer Rechnung weiter geführt. Es wird nun auf den Zahlungseingang gewartet.", of(Hint.CREATES_INVOICE, Hint.UNIT_LEAVES_STOCK)) {
        @Override
        public void apply(CustomerDocument instance) {
            instance.getDocument().add(Condition.SENT);
            instance.getDocument().setType(DocumentType.INVOICE);
            instance.getDocument().setDirective(Directive.WAIT_FOR_MONEY);
        }
    };

    /**
     * Sets {@link Type#INVOICE}, {@link Directive#WAIT_FOR_MONEY} and adds {@link Condition#PICKED_UP}.
     */
    public final static RedTapeStateTransition V_HAND_OVER_AND_INVOICE = new RedTapeStateTransition(
            "V_HAND_OVER_AND_INVOICE",
            "Abgeholt und Rechnung erzeugt",
            "Alle Positionen des Auftrags wurden abgeholt, der Auftrag wird zu einer Rechnung weiter geführt.\n"
            + "Es wird nun auf den Zahlungseingang gewartet, bzw. es muss nun die Lastschrift durchgeführt werden.", of(Hint.CREATES_INVOICE, Hint.UNIT_LEAVES_STOCK)) {
        @Override
        public void apply(CustomerDocument instance) {
            instance.getDocument().add(Condition.PICKED_UP);
            instance.getDocument().setType(DocumentType.INVOICE);
            instance.getDocument().setDirective(Directive.WAIT_FOR_MONEY);
        }
    };

    /**
     * Sets {@link Directive#NONE} and adds {@link Condition#PAID}.
     */
    public final static RedTapeStateTransition III_IV_V_PAY = new RedTapeStateTransition(
            "III_IV_V_PAY",
            "Bezahlt/Abgebucht",
            "Die Zahlung ist eingegangen oder der Lastschriftauftrag wurde durchgeführt.", of(Hint.ADDS_SETTLEMENT)) {
        @Override
        public void apply(CustomerDocument instance) {
            instance.getDocument().add(Condition.PAID);
            instance.getDocument().setDirective(Directive.NONE);
        }
    };

    /**
     * Sets {@link Directive#SEND_CASH_ON_DELIVERY_CONTRACT} and adds {@link PaymentMethod#CASH_ON_DELIVERY}.
     */
    public final static RedTapeStateTransition II_SET_CASH_ON_DELIVERY_F01X = new RedTapeStateTransition(
            "II_SET_CASH_ON_DELIVERY_F01X",
            "Zahlung: Nachnahme",
            "Die Zahlungsmodalität auf Nachnahme ändern.", of(Hint.CHANGES_PAYMENT_METHOD_TO_CASH_ON_DELIVERY)) {
        @Override
        public void apply(CustomerDocument instance) {
            instance.getDocument().getDossier().setPaymentMethod(PaymentMethod.CASH_ON_DELIVERY);
            instance.getDocument().setDirective(Directive.SEND_CASH_ON_DELIVERY_CONTRACT);
        }
    };

    /**
     * Sets {@link Directive#PREPARE_SHIPPING} and adds {@link PaymentMethod#CASH_ON_DELIVERY}.
     */
    public final static RedTapeStateTransition II_SET_CASH_ON_DELIVERY_F010 = new RedTapeStateTransition(
            "II_SET_CASH_ON_DELIVERY_F010",
            "Zahlung: Nachnahme",
            "Die Zahlungsmodalität auf Nachnahme ändern.", of(Hint.CHANGES_PAYMENT_METHOD_TO_CASH_ON_DELIVERY)) {
        @Override
        public void apply(CustomerDocument instance) {
            instance.getDocument().getDossier().setPaymentMethod(PaymentMethod.CASH_ON_DELIVERY);
            instance.getDocument().setDirective(Directive.PREPARE_SHIPPING);
        }
    };

    /**
     * Sets {@link Directive#SEND_ORDER} and adds {@link PaymentMethod#CASH_ON_DELIVERY}.
     */
    public final static RedTapeStateTransition II_SET_CASH_ON_DELIVERY_F011 = new RedTapeStateTransition(
            "II_SET_CASH_ON_DELIVERY_F011",
            "Zahlung: Nachnahme",
            "Die Zahlungsmodalität auf Nachnahme ändern.", of(Hint.CHANGES_PAYMENT_METHOD_TO_CASH_ON_DELIVERY)) {
        @Override
        public void apply(CustomerDocument instance) {
            instance.getDocument().getDossier().setPaymentMethod(PaymentMethod.CASH_ON_DELIVERY);
            instance.getDocument().setDirective(Directive.SEND_ORDER);
        }
    };

    /**
     * Sets {@link Directive#SEND_ORDER} and adds {@link PaymentMethod#ADVANCE_PAYMENT}.
     */
    public final static RedTapeStateTransition III_SET_ADVANCE_PAYMENT = new RedTapeStateTransition(
            "III_SET_ADVANCE_PAYMENT",
            "Zahlung: Vorkasse",
            "Die Zahlungsmodalität auf Vorkasse ändern.", of(Hint.CHANGES_PAYMENT_METHOD_TO_ADVENCED_PAYMENT)) {
        @Override
        public void apply(CustomerDocument instance) {
            instance.getDocument().getDossier().setPaymentMethod(PaymentMethod.ADVANCE_PAYMENT);
            instance.getDocument().setDirective(Directive.SEND_ORDER);
        }
    };

    /**
     * Sets {@link Directive#DIRECT_DEBIT} and adds {@link PaymentMethod#PREPARE_SHIPPING}.
     */
    public final static RedTapeStateTransition III_SET_DIRECT_DEBIT_F0X0 = new RedTapeStateTransition(
            "III_SET_DIRECT_DEBIT_F0X0",
            "Zahlung: Lastschrift",
            "Die Zahlungsmodalität auf Lastschrift ändern.", of(Hint.CHANGES_PAYMENT_METHOD_TO_DIRECT_DEBIT)) {
        @Override
        public void apply(CustomerDocument instance) {
            instance.getDocument().getDossier().setPaymentMethod(PaymentMethod.DIRECT_DEBIT);
            instance.getDocument().setDirective(Directive.PREPARE_SHIPPING);
        }
    };

    /**
     * Sets {@link Directive#SEND_ORDER} and adds {@link PaymentMethod#DIRECT_DEBIT}.
     */
    public final static RedTapeStateTransition III_SET_DIRECT_DEBIT_F0X1 = new RedTapeStateTransition(
            "III_SET_DIRECT_DEBIT_F0X1",
            "Zahlung: Lastschrift",
            "Die Zahlungsmodalität auf Lastschrift ändern.", of(Hint.CHANGES_PAYMENT_METHOD_TO_DIRECT_DEBIT)) {
        @Override
        public void apply(CustomerDocument instance) {
            instance.getDocument().getDossier().setPaymentMethod(PaymentMethod.DIRECT_DEBIT);
            instance.getDocument().setDirective(Directive.SEND_ORDER);
        }
    };

    /**
     * Sets {@link Directive#PREPARE_SHIPPING} and adds {@link PaymentMethod#INVOICE}.
     */
    public final static RedTapeStateTransition III_SET_INVOICE_F0X0 = new RedTapeStateTransition(
            "III_SET_INVOICE_F0X0",
            "Zahlung: Rechnung",
            "Die Zahlungsmodalität auf Rechnung ändern.", of(Hint.CHANGES_PAYMENT_METHOD_TO_INVOICE)) {
        @Override
        public void apply(CustomerDocument instance) {
            instance.getDocument().getDossier().setPaymentMethod(PaymentMethod.INVOICE);
            instance.getDocument().setDirective(Directive.PREPARE_SHIPPING);
        }
    };

    /**
     * Sets {@link Directive#INVOICE} and adds {@link PaymentMethod#SEND_ORDER}.
     */
    public final static RedTapeStateTransition III_SET_INVOICE_F0X1 = new RedTapeStateTransition(
            "III_SET_INVOICE_F0X1",
            "Zahlung: Rechnung",
            "Die Zahlungsmodalität auf Rechnung ändern.", of(Hint.CHANGES_PAYMENT_METHOD_TO_INVOICE)) {
        @Override
        public void apply(CustomerDocument instance) {
            instance.getDocument().getDossier().setPaymentMethod(PaymentMethod.INVOICE);
            instance.getDocument().setDirective(Directive.SEND_ORDER);
        }
    };

    /**
     * Sets {@link Directive#SEND_ORDER} and adds {@link PaymentMethod#DIRECT_DEBIT}.
     */
    public final static RedTapeStateTransition SET_DIRECT_DEBIT = new RedTapeStateTransition(
            "SET_DIRECT_DEBIT",
            "Zahlung: Lastschrift",
            "Die Zahlungsmodalität auf Vorkasse ändern, Kunde Auftrag neu zusenden.", of(Hint.CHANGES_PAYMENT_METHOD_TO_DIRECT_DEBIT)) {
        @Override
        public void apply(CustomerDocument instance) {
            instance.getDocument().getDossier().setPaymentMethod(PaymentMethod.DIRECT_DEBIT);
            instance.getDocument().setDirective(Directive.SEND_ORDER);
        }
    };

    /**
     * Sets {@link Directive#SEND_ORDER} and got dispatch.
     */
    public final static RedTapeStateTransition I_SET_DISPATCH = new RedTapeStateTransition(
            "I_SET_DISPATCH",
            "Versandauftrag",
            "Den Auftrag in einen Versandauftrag umwandeln.") {
        @Override
        public void apply(CustomerDocument instance) {
            instance.getDocument().getDossier().setDispatch(true);
            instance.getDocument().setDirective(Directive.SEND_ORDER);
        }
    };

    /**
     * Sets {@link Directive#WAIT_FOR_MONEY} and got not dispatch.
     */
    public final static RedTapeStateTransition II_SET_PICK_UP = new RedTapeStateTransition(
            "II_SET_PICK_UP",
            "Abholauftrag",
            "Den Auftrag in einen Abholauftrag umwandeln.") {
        @Override
        public void apply(CustomerDocument instance) {
            instance.getDocument().getDossier().setDispatch(false);
            instance.getDocument().setDirective(Directive.WAIT_FOR_MONEY);
        }
    };

    /**
     * Sets {@link Directive#PREPARE_SHIPPING} and got dispatch.
     */
    public final static RedTapeStateTransition V_SET_DISPATCH_F0X0 = new RedTapeStateTransition(
            "V_SET_DISPATCH_F0X0",
            "Versandauftrag",
            "Den Auftrag in einen Versandauftrag umwandeln.") {
        @Override
        public void apply(CustomerDocument instance) {
            instance.getDocument().getDossier().setDispatch(true);
            instance.getDocument().setDirective(Directive.PREPARE_SHIPPING);
        }
    };

    /**
     * Sets {@link Directive#SEND_ORDER} and got dispatch.
     */
    public final static RedTapeStateTransition V_SET_DISPATCH_F0X1 = new RedTapeStateTransition(
            "V_SET_DISPATCH_F0X1",
            "Versandauftrag",
            "Den Auftrag in einen Versandauftrag umwandeln.") {
        @Override
        public void apply(CustomerDocument instance) {
            instance.getDocument().getDossier().setDispatch(true);
            instance.getDocument().setDirective(Directive.SEND_ORDER);
        }
    };

    /**
     * Sets {@link Directive#HAND_OVER_GOODS} and got not dispatch.
     */
    public final static RedTapeStateTransition IV_SET_PICK_UP = new RedTapeStateTransition(
            "IV_SET_PICK_UP",
            "Abholauftrag",
            "Den Auftrag in einen Abholauftrag umwandeln.") {
        @Override
        public void apply(CustomerDocument instance) {
            instance.getDocument().getDossier().setDispatch(false);
            instance.getDocument().setDirective(Directive.HAND_OVER_GOODS);
        }
    };

    /**
     * Sets {@link Directive#SEND_ORDER} and adds {@link PaymentMethod#ADVANCE_PAYMENT}.
     */
    public final static RedTapeStateTransition IV_SET_ADVANCE_PAYMENT = new RedTapeStateTransition(
            "IV_SET_ADVANCE_PAYMENT",
            "Zahlung: Vorkasse",
            "Die Zahlungsmodalität auf Vorkasse ändern.", of(Hint.CHANGES_PAYMENT_METHOD_TO_ADVENCED_PAYMENT)) {
        @Override
        public void apply(CustomerDocument instance) {
            instance.getDocument().getDossier().setPaymentMethod(PaymentMethod.ADVANCE_PAYMENT);
            instance.getDocument().setDirective(Directive.SEND_ORDER);
        }
    };

    /**
     * Sets {@link Directive#SEND_CASH_ON_DELIVERY_CONTRACT} and adds {@link PaymentMethod#CASH_ON_DELIVERY}.
     */
    public final static RedTapeStateTransition IV_SET_CASH_ON_DELIVERY_F00X = new RedTapeStateTransition(
            "IV_SET_CASH_ON_DELIVERY_F00X",
            "Zahlung: Nachnahme",
            "Die Zahlungsmodalität auf Nachnahme ändern.", of(Hint.CHANGES_PAYMENT_METHOD_TO_CASH_ON_DELIVERY)) {
        @Override
        public void apply(CustomerDocument instance) {
            instance.getDocument().getDossier().setPaymentMethod(PaymentMethod.CASH_ON_DELIVERY);
            instance.getDocument().setDirective(Directive.SEND_CASH_ON_DELIVERY_CONTRACT);
        }
    };

    /**
     * Sets {@link Directive#PREPARE_SHIPPING} and adds {@link PaymentMethod#CASH_ON_DELIVERY}.
     */
    public final static RedTapeStateTransition IV_SET_CASH_ON_DELIVERY_F010 = new RedTapeStateTransition(
            "IV_SET_CASH_ON_DELIVERY_F010",
            "Zahlung: Nachnahme",
            "Die Zahlungsmodalität auf Nachnahme ändern.", of(Hint.CHANGES_PAYMENT_METHOD_TO_CASH_ON_DELIVERY)) {
        @Override
        public void apply(CustomerDocument instance) {
            instance.getDocument().getDossier().setPaymentMethod(PaymentMethod.CASH_ON_DELIVERY);
            instance.getDocument().setDirective(Directive.PREPARE_SHIPPING);
        }
    };

    /**
     * Sets {@link Directive#SEND_ORDER} and adds {@link PaymentMethod#CASH_ON_DELIVERY}.
     */
    public final static RedTapeStateTransition IV_SET_CASH_ON_DELIVERY_F011 = new RedTapeStateTransition(
            "IV_SET_CASH_ON_DELIVERY_F011",
            "Zahlung: Nachnahme",
            "Die Zahlungsmodalität auf Nachnahme ändern.", of(Hint.CHANGES_PAYMENT_METHOD_TO_CASH_ON_DELIVERY)) {
        @Override
        public void apply(CustomerDocument instance) {
            instance.getDocument().getDossier().setPaymentMethod(PaymentMethod.CASH_ON_DELIVERY);
            instance.getDocument().setDirective(Directive.SEND_ORDER);
        }
    };

    /**
     * Sets {@link Directive#SEND_ORDER} and adds {@link PaymentMethod#INVOICE}.
     */
    public final static RedTapeStateTransition IV_SET_INVOICE_F0X1 = new RedTapeStateTransition(
            "IV_SET_INVOICE_F0X1",
            "Zahlung: Rechnung",
            "Die Zahlungsmodalität auf Rechnung ändern.", of(Hint.CHANGES_PAYMENT_METHOD_TO_INVOICE)) {
        @Override
        public void apply(CustomerDocument instance) {
            instance.getDocument().getDossier().setPaymentMethod(PaymentMethod.INVOICE);
            instance.getDocument().setDirective(Directive.SEND_ORDER);
        }
    };

    /**
     * Sets {@link Directive#PREPARE_SHIPPING} and adds {@link PaymentMethod#INVOICE}.
     */
    public final static RedTapeStateTransition IV_SET_INVOICE_F0X0 = new RedTapeStateTransition(
            "IV_SET_INVOICE_F0X0",
            "Zahlung: Rechnung",
            "Die Zahlungsmodalität auf Rechnung ändern. IVb", of(Hint.CHANGES_PAYMENT_METHOD_TO_INVOICE)) {
        @Override
        public void apply(CustomerDocument instance) {
            instance.getDocument().getDossier().setPaymentMethod(PaymentMethod.INVOICE);
            instance.getDocument().setDirective(Directive.PREPARE_SHIPPING);
        }
    };

    /**
     * Sets {@link Directive#SEND_ORDER} and adds {@link PaymentMethod#DIRECT_DEBIT}.
     */
    public final static RedTapeStateTransition IV_SET_DIRECT_DEBIT_F0X1 = new RedTapeStateTransition(
            "IV_SET_DIRECT_DEBIT_F0X1",
            "Zahlung: Lastschrift",
            "Die Zahlungsmodalität auf Lastschrift ändern.", of(Hint.CHANGES_PAYMENT_METHOD_TO_DIRECT_DEBIT)) {
        @Override
        public void apply(CustomerDocument instance) {
            instance.getDocument().getDossier().setPaymentMethod(PaymentMethod.DIRECT_DEBIT);
            instance.getDocument().setDirective(Directive.SEND_ORDER);
        }
    };

    /**
     * Sets {@link Directive#PREPARE_SHIPPING} and adds {@link PaymentMethod#DIRECT_DEBIT}.
     */
    public final static RedTapeStateTransition IV_SET_DIRECT_DEBIT_F0X0 = new RedTapeStateTransition(
            "IV_SET_DIRECT_DEBIT_F0X0",
            "Zahlung: Lastschrift",
            "Die Zahlungsmodalität auf Lastschrift ändern.", of(Hint.CHANGES_PAYMENT_METHOD_TO_DIRECT_DEBIT)) {
        @Override
        public void apply(CustomerDocument instance) {
            instance.getDocument().getDossier().setPaymentMethod(PaymentMethod.DIRECT_DEBIT);
            instance.getDocument().setDirective(Directive.PREPARE_SHIPPING);
        }
    };

    /**
     * Sets {@link Directive#WAIT_FOR_MONEY} and adds {@link PaymentMethod#ADVANCE_PAYMENT}.
     */
    public final static RedTapeStateTransition V_SET_ADVANCE_PAYMENT = new RedTapeStateTransition(
            "V_SET_ADVANCE_PAYMENT",
            "Zahlung: Vorkasse",
            "Die Zahlungsmodalität auf Vorkasse ändern.", of(Hint.CHANGES_PAYMENT_METHOD_TO_ADVENCED_PAYMENT)) {
        @Override
        public void apply(CustomerDocument instance) {
            instance.getDocument().getDossier().setPaymentMethod(PaymentMethod.ADVANCE_PAYMENT);
            instance.getDocument().setDirective(Directive.WAIT_FOR_MONEY);
        }
    };

    /**
     * Sets {@link Directive#HAND_OVER_GOODS} and adds {@link PaymentMethod#INVOICE}.
     */
    public final static RedTapeStateTransition V_SET_INVOICE = new RedTapeStateTransition(
            "V_SET_INVOICE",
            "Zahlung: Rechnung",
            "Die Zahlungsmodalität auf Rechnung ändern. Va", of(Hint.CHANGES_PAYMENT_METHOD_TO_INVOICE)) {
        @Override
        public void apply(CustomerDocument instance) {
            instance.getDocument().getDossier().setPaymentMethod(PaymentMethod.INVOICE);
            instance.getDocument().setDirective(Directive.HAND_OVER_GOODS);
        }
    };

    /**
     * Sets {@link Directive#HAND_OVER_GOODS} and adds {@link PaymentMethod#DIRECT_DEBIT}.
     */
    public final static RedTapeStateTransition V_SET_DIRECT_DEBIT = new RedTapeStateTransition(
            "V_SET_DIRECT_DEBIT",
            "Zahlung: Lastschrift",
            "Die Zahlungsmodalität auf Lastschrift ändern.", of(Hint.CHANGES_PAYMENT_METHOD_TO_DIRECT_DEBIT)) {
        @Override
        public void apply(CustomerDocument instance) {
            instance.getDocument().getDossier().setPaymentMethod(PaymentMethod.DIRECT_DEBIT);
            instance.getDocument().setDirective(Directive.HAND_OVER_GOODS);
        }
    };

    /**
     * Sets {@link Type#COMPLAINT} and {@link Directive#WAIT_FOR_COMPLAINT_COMPLETION}.
     */
    public final static RedTapeStateTransition CREATE_COMPLAINT = new RedTapeStateTransition(
            "CREATE_COMPLAINT",
            "Reklamation angemeldet",
            "Ein Kunde möchte ein Gerät reklamieren.", of(Hint.CREATES_COMPLAINT), AtomicRight.CREATE_COMPLAINT) {
        @Override
        public void apply(CustomerDocument instance) {
            instance.getDocument().setType(DocumentType.COMPLAINT);
            instance.getDocument().setDirective(Directive.WAIT_FOR_COMPLAINT_COMPLETION);
        }
    };

    /**
     * Sets {@link Directive#WAIT_FOR_COMPLAINT_COMPLETION} and {@link Condition#REJECTED}.
     */
    public final static RedTapeStateTransition REJECT_COMPLAINT = new RedTapeStateTransition(
            "REJECT_COMPLAINT",
            "Reklamation ablehnen",
            "Die Reklamation ist nicht berechtigt und wird abgelehnt.", noneOf(Hint.class), AtomicRight.UPDATE_ANNULATION_INVOICE_TO_ABORT) {
        @Override
        public void apply(CustomerDocument instance) {
            instance.getDocument().add(Condition.REJECTED);
            instance.getDocument().setDirective(Directive.NONE);
        }
    };

    /**
     * Sets {@link Directive#NONE} and {@link Condition#WITHDRAWN}.
     */
    public final static RedTapeStateTransition WITHDRAW_COMPLAINT = new RedTapeStateTransition(
            "WITHDRAW_COMPLAINT",
            "Reklamation zurückgezogen",
            "Der Kunde hat die Reklamation zurückgezogen.", noneOf(Hint.class), AtomicRight.UPDATE_ANNULATION_INVOICE_TO_WITHDRAW) {
        @Override
        public void apply(CustomerDocument instance) {
            instance.getDocument().add(Condition.WITHDRAWN);
            instance.getDocument().setDirective(Directive.NONE);
        }
    };

    /**
     * Sets {@link Directive#CREATE_CREDIT_MEMO_OR_ANNULATION_INVOICE} and {@link Condition#ACCEPTED}.
     */
    public final static RedTapeStateTransition ACCEPT_COMPLAINT = new RedTapeStateTransition(
            "ACCEPT_COMPLAINT",
            "Reklamation akzeptieren",
            "Die Reklamation des Kunden ist berechtigt, sie wird akzeptiert.", noneOf(Hint.class), AtomicRight.UPDATE_ANNULATION_INVOICE_TO_ACCEPT) {
        @Override
        public void apply(CustomerDocument instance) {
            instance.getDocument().add(Condition.ACCEPTED);
            instance.getDocument().setDirective(Directive.CREATE_CREDIT_MEMO_OR_ANNULATION_INVOICE);
        }
    };

    /**
     * Sets {@link Type#ANNULATION_INVOICE}, {@link Directive#BALANCE_REPAYMENT}.
     */
    public final static RedTapeStateTransition CREATE_ANNULATION_INVOICE = new RedTapeStateTransition(
            "CREATE_ANNULATION_INVOICE",
            "Stornorechnung",
            "Eine Stornorechnung erstellen", of(Hint.CREATES_ANNULATION_INVOICE), AtomicRight.CREATE_ANNULATION_INVOICE) {
        @Override
        public void apply(CustomerDocument instance) {
            instance.getDocument().setType(DocumentType.ANNULATION_INVOICE);
            instance.getDocument().setDirective(Directive.BALANCE_REPAYMENT);
        }
    };

    /**
     * Sets {@link Type#CREDIT_MEMO}, {@link Directive#BALANCE_REPAYMENT}.
     */
    public final static RedTapeStateTransition CREATE_CREDIT_MEMO = new RedTapeStateTransition(
            "CREATE_CREDIT_MEMO",
            "Gutschrift",
            "Eine Gutschrift erstellen", of(Hint.CREATES_CREDIT_MEMO), AtomicRight.CREATE_CREDITMEMO) {
        @Override
        public void apply(CustomerDocument instance) {
            instance.getDocument().setType(DocumentType.CREDIT_MEMO);
            instance.getDocument().setDirective(Directive.BALANCE_REPAYMENT);
        }
    };

    /**
     * Sets {@link Directive#NONE} and {@link Condition#REPAYMENT_BALANCED}.
     */
    public final static RedTapeStateTransition BALANCED_REPAYMENT = new RedTapeStateTransition(
            "BALANCED_CREDIT_MEMO",
            "Abschlusszahlung erfolgt",
            "Alle Forderungen der Gutschrift wurden ausgeglichen", noneOf(Hint.class), AtomicRight.UPDATE_ANNULATION_INVOICE_TO_BALANCED) {
        @Override
        public void apply(CustomerDocument instance) {
            instance.getDocument().add(Condition.REPAYMENT_BALANCED);
            instance.getDocument().setDirective(Directive.NONE);
        }
    };

    /**
     * Sets {@link Directive#NONE} and {@link Condition#PICKED_UP}.
     */
    public final static RedTapeStateTransition COMPLETE_RETURN_CAPITAL_ASSET = new RedTapeStateTransition(
            "COMPLETE_RETURN_CAPITAL_ASSET",
            "Ware übergeben",
            "Die Ware wurde übergeben") {
        @Override
        public void apply(CustomerDocument instance) {
            instance.getDocument().add(Condition.PICKED_UP);
            instance.getDocument().setDirective(Directive.NONE);
        }
    };

    /**
     * Set the HashSet for adding Shipping costs.
     */
    public final static Set<RedTapeStateTransition> ADD_SHIPPING_COSTS = new HashSet<>(Arrays.asList(
            I_SET_DISPATCH,
            II_SET_CASH_ON_DELIVERY_F010, II_SET_CASH_ON_DELIVERY_F011, II_SET_CASH_ON_DELIVERY_F01X,
            III_SET_ADVANCE_PAYMENT, III_SET_DIRECT_DEBIT_F0X0, III_SET_DIRECT_DEBIT_F0X1, III_SET_INVOICE_F0X0, III_SET_INVOICE_F0X1,
            IV_SET_CASH_ON_DELIVERY_F00X, IV_SET_CASH_ON_DELIVERY_F010, IV_SET_CASH_ON_DELIVERY_F011,
            V_SET_DISPATCH_F0X0, V_SET_DISPATCH_F0X1));

    /**
     * Set the HashSet for removing Shipping costs.
     */
    public final static Set<RedTapeStateTransition> REMOVE_SHIPPING_COSTS = new HashSet<>(Arrays.asList(
            II_SET_PICK_UP, IV_SET_PICK_UP));

    /**
     * Set the HashSet for create Invoice.
     */
    public final static Set<RedTapeStateTransition> CREATES_INVOICE = new HashSet<>(Arrays.asList(
            I_INVOICE, I_PAY_AND_INVOICE,
            II_INVOICE, II_INVOICE_UNSHIPPED, II_SEND_AND_INVOICE,
            III_IV_SEND_AND_INVOICE,
            V_HAND_OVER_AND_INVOICE));

}
