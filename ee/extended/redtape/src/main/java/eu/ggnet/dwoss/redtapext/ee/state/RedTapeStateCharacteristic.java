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

import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import eu.ggnet.dwoss.redtape.ee.entity.Document;
import eu.ggnet.dwoss.redtape.ee.entity.Document.Condition;
import eu.ggnet.dwoss.redtape.ee.entity.Document.Directive;
import eu.ggnet.dwoss.rules.DocumentType;
import eu.ggnet.dwoss.common.api.values.PaymentMethod;
import eu.ggnet.dwoss.common.api.values.CustomerFlag;

import eu.ggnet.statemachine.State;
import eu.ggnet.statemachine.StateCharacteristic;

/**
 * The Characteristic of a Document.
 *
 * @author oliver.guenther
 */
public class RedTapeStateCharacteristic implements StateCharacteristic<CustomerDocument> {

    /**
     * Helper class for creating multiple States.
     * Is considered to be used like:
     * <pre>
     * new Permutation() {{
     *    init = new RedTapeStateCharacteristic(Type.ORDER, PaymentMethod.ADVANCE_PAYMENT, of(Condition.CREATED), null, null, false);
     *    flagss = asList(noneOf(Flag.class), of(Flag.CONFIRMED_CASH_ON_DELIVERY), of(Flag.CONFIRMS_DOSSIER), of(Flag.CONFIRMED_CASH_ON_DELIVERY, Flag.CONFIRMS_DOSSIER));
     *    directives = asList(Directive.SEND_ORDER_DOCUMENT, Directive.WAIT_3_DAYS_FOR_MONEY, Directive.WAIT_10_DAYS_FOR_MONEY);
     * }}.build());
     * </pre>
     */
    public static class Permutation {

        protected RedTapeStateCharacteristic init;

        protected Set<PaymentMethod> paymentMethods;

        protected Set<Set<CustomerFlag>> flagss;

        protected Set<Directive> directives;

        protected Set<Set<Condition>> conditionss;

        protected boolean permuteDispatch = false;

        public Collection<RedTapeStateCharacteristic> build() {
            Set<RedTapeStateCharacteristic> result = new HashSet<>();
            if ( paymentMethods == null ) {
                paymentMethods = new HashSet<>();
                paymentMethods.add(init.getPaymentMethod());
            }
            if ( flagss == null ) {
                flagss = new HashSet<>();
                flagss.add(init.getCustomerFlags());
            }
            if ( directives == null ) {
                directives = new HashSet<>();
                directives.add(init.getDirective());
            }
            if ( conditionss == null ) {
                conditionss = new HashSet<>();
                conditionss.add(init.getConditions());
            }
            for (PaymentMethod paymentMethod : paymentMethods) {
                for (Set<CustomerFlag> flags : flagss) {
                    for (Directive directive : directives) {
                        for (Set<Condition> conditions : conditionss) {
                            if ( !permuteDispatch ) {
                                result.add(new RedTapeStateCharacteristic(init.getType(), paymentMethod, conditions, directive, flags, init.isDispatch()));
                            } else {
                                result.add(new RedTapeStateCharacteristic(init.getType(), paymentMethod, conditions, directive, flags, false));
                                result.add(new RedTapeStateCharacteristic(init.getType(), paymentMethod, conditions, directive, flags, true));
                            }
                        }
                    }
                }
            }
            return result;
        }
    }

    public static class Change {

        private State<CustomerDocument> init;

        private DocumentType type;

        private PaymentMethod paymentMethod;

        private Set<Document.Condition> conditions;

        private Document.Directive directive;

        private CustomerFlag customerFlag;

        private Boolean dispatch;

        public Change(State<CustomerDocument> state, Directive directive) {
            this(state, null, null, null, directive, null, null);
        }

        public Change(State<CustomerDocument> state, CustomerFlag flag, Directive directive) {
            this(state, null, null, null, directive, null, null);
        }

        public Change(State<CustomerDocument> state, DocumentType type, Directive directive) {
            this(state, type, null, null, directive, null, null);
        }

        public Change(State<CustomerDocument> state, DocumentType type, Set<Condition> conditions, Directive directive) {
            this(state, type, null, conditions, directive, null, null);
        }

        public Change(State<CustomerDocument> state, Set<Condition> conditions, Directive directive) {
            this(state, null, null, conditions, directive, null, null);
        }

        public Change(State<CustomerDocument> state, PaymentMethod paymentMethod, boolean dispatch) {
            this(state, null, paymentMethod, null, null, null, dispatch);
        }

        public Change(State<CustomerDocument> state, boolean dispatch) {
            this(state, null, null, null, null, null, dispatch);
        }

        public Change(State<CustomerDocument> init,
                      DocumentType type,
                      PaymentMethod paymentMethod,
                      Set<Document.Condition> conditions,
                      Directive directive,
                      CustomerFlag customerFlag,
                      Boolean dispatch) {
            this.init = init;
            this.type = type;
            this.paymentMethod = paymentMethod;
            this.conditions = conditions;
            this.directive = directive;
            this.customerFlag = customerFlag;
            this.dispatch = dispatch;
        }

        public Boolean getDispatch() {
            return dispatch;
        }

        public Collection<RedTapeStateCharacteristic> build() {
            Set<RedTapeStateCharacteristic> result = new HashSet<>();
            for (StateCharacteristic<CustomerDocument> o : init.getCharacteristics()) {
                RedTapeStateCharacteristic rsc = (RedTapeStateCharacteristic)o; // Not nice, but ok.
                Set<Condition> newConditions = EnumSet.noneOf(Condition.class);
                newConditions.addAll(rsc.getConditions());
                if ( conditions != null ) newConditions.addAll(conditions);
                Set<CustomerFlag> newFlags = EnumSet.noneOf(CustomerFlag.class);
                newFlags.addAll(rsc.getCustomerFlags());
                if ( customerFlag != null ) newFlags.add(customerFlag);
                result.add(new RedTapeStateCharacteristic(
                        (type == null ? rsc.getType() : type),
                        (paymentMethod == null ? rsc.getPaymentMethod() : paymentMethod),
                        newConditions,
                        (directive == null ? rsc.getDirective() : directive),
                        newFlags,
                        (dispatch == null ? rsc.isDispatch() : dispatch)));
            }
            return result;
        }
    }

    public RedTapeStateCharacteristic(DocumentType type, PaymentMethod paymentMethod, Set<Document.Condition> conditions, Directive directive,
                                      Set<CustomerFlag> customerFlags, boolean dispatch) {
        this.dispatch = dispatch;
        this.type = type;
        this.paymentMethod = paymentMethod;
        this.directive = directive;
        if ( conditions != null ) this.conditions.addAll(conditions);
        if ( customerFlags != null ) this.customerFlags.addAll(customerFlags);
    }

    private boolean dispatch;

    private DocumentType type;

    private PaymentMethod paymentMethod;

    private Set<Document.Condition> conditions = EnumSet.noneOf(Document.Condition.class);

    private Document.Directive directive;

    private Set<CustomerFlag> customerFlags = EnumSet.noneOf(CustomerFlag.class);

    public boolean isDispatch() {
        return dispatch;
    }

    public DocumentType getType() {
        return type;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public Set<Condition> getConditions() {
        return EnumSet.copyOf(conditions);
    }

    public Set<CustomerFlag> getCustomerFlags() {
        return EnumSet.copyOf(customerFlags);
    }

    public Directive getDirective() {
        return directive;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 13 * hash + (this.dispatch ? 1 : 0);
        hash = 13 * hash + (this.type != null ? this.type.hashCode() : 0);
        hash = 13 * hash + (this.paymentMethod != null ? this.paymentMethod.hashCode() : 0);
        hash = 13 * hash + Objects.hashCode(this.conditions);
        hash = 13 * hash + (this.directive != null ? this.directive.hashCode() : 0);
        hash = 13 * hash + Objects.hashCode(this.customerFlags);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        final RedTapeStateCharacteristic other = (RedTapeStateCharacteristic)obj;
        if ( this.dispatch != other.dispatch ) return false;
        if ( this.type != other.type ) return false;
        if ( this.paymentMethod != other.paymentMethod ) return false;
        if ( !Objects.equals(this.conditions, other.conditions) ) return false;
        if ( this.directive != other.directive ) return false;
        if ( !Objects.equals(this.customerFlags, other.customerFlags) ) return false;
        return true;
    }

    @Override
    public String toString() {
        return "RedTapeStateCharacteristic{" + "dispatch=" + dispatch + ", type=" + type + ", paymentMethod=" + paymentMethod + ", conditions=" + conditions + ", directive=" + directive + ", customerFlags=" + customerFlags + '}';
    }
}
