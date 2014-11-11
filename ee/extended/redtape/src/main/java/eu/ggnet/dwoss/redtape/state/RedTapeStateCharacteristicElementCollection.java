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
package eu.ggnet.dwoss.redtape.state;

import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import eu.ggnet.dwoss.redtape.entity.Document;
import eu.ggnet.dwoss.redtape.entity.Document.Condition;
import eu.ggnet.dwoss.redtape.entity.Document.Directive;

import eu.ggnet.dwoss.rules.DocumentType;
import eu.ggnet.dwoss.rules.PaymentMethod;
import eu.ggnet.dwoss.rules.CustomerFlag;

import eu.ggnet.statemachine.StateCharacteristic;

/**
 *
 * @author oliver.guenther
 */
public class RedTapeStateCharacteristicElementCollection {

    public void removeAll(RedTapeStateCharacteristicElementCollection toRemove) {
        dispatches.removeAll(toRemove.dispatches);
        types.removeAll(toRemove.types);
        paymentMethods.removeAll(toRemove.paymentMethods);
        conditions.removeAll(toRemove.conditions);
        customerFlags.removeAll(toRemove.customerFlags);
        directives.removeAll(toRemove.directives);
    }

    public static <T> RedTapeStateCharacteristicElementCollection union(Collection<StateCharacteristic<T>> scs) {
        RedTapeStateCharacteristicElementCollection result = null;
        for (StateCharacteristic sc : scs) {
            if ( !(sc instanceof RedTapeStateCharacteristic) ) throw new IllegalArgumentException(sc + " not Instance of DocumentCharacteristic");
            RedTapeStateCharacteristic dc = (RedTapeStateCharacteristic)sc;
            if ( result == null ) {
                result = new RedTapeStateCharacteristicElementCollection();
            }
            result.dispatches.add(dc.isDispatch());
            result.types.add(dc.getType());
            result.paymentMethods.add(dc.getPaymentMethod());
            result.directives.add(dc.getDirective());
            result.conditions.addAll(dc.getConditions());
            result.customerFlags.addAll(dc.getCustomerFlags());
        }
        return result;
    }

    public static <T> RedTapeStateCharacteristicElementCollection intersection(Collection<StateCharacteristic<T>> scs) {
        RedTapeStateCharacteristicElementCollection result = null;
        for (StateCharacteristic sc : scs) {
            if ( !(sc instanceof RedTapeStateCharacteristic) ) throw new IllegalArgumentException(sc + " not Instance of DocumentCharacteristic");
            RedTapeStateCharacteristic dc = (RedTapeStateCharacteristic)sc;
            if ( result == null ) {
                result = new RedTapeStateCharacteristicElementCollection();
                result.dispatches.add(dc.isDispatch());
                result.types.add(dc.getType());
                result.paymentMethods.add(dc.getPaymentMethod());
                result.directives.add(dc.getDirective());
                result.conditions.addAll(dc.getConditions());
                result.customerFlags.addAll(dc.getCustomerFlags());
            }
            if ( !result.dispatches.contains(dc.isDispatch()) ) result.dispatches.clear();
            result.types.retainAll(EnumSet.of(dc.getType()));
            result.directives.retainAll(EnumSet.of(dc.getDirective()));
            result.paymentMethods.retainAll(EnumSet.of(dc.getPaymentMethod()));
            result.conditions.retainAll(dc.getConditions());
            result.customerFlags.retainAll(dc.getCustomerFlags());
        }
        return result;
    }

    private Set<Boolean> dispatches = new HashSet<>();

    private EnumSet<DocumentType> types = EnumSet.noneOf(DocumentType.class);

    private EnumSet<PaymentMethod> paymentMethods = EnumSet.noneOf(PaymentMethod.class);

    private EnumSet<Document.Condition> conditions = EnumSet.noneOf(Document.Condition.class);

    private EnumSet<CustomerFlag> customerFlags = EnumSet.noneOf(CustomerFlag.class);

    private EnumSet<Document.Directive> directives = EnumSet.noneOf(Document.Directive.class);

    public Set<Boolean> getDispatches() {
        return dispatches;
    }

    public Set<String> getFormatedDispatches() {
        Set<String> result = new HashSet<>();
        if ( dispatches.contains(true) ) result.add("DISPATCH");
        if ( dispatches.contains(false) ) result.add("PICKUP");
        return result;
    }

    public EnumSet<DocumentType> getTypes() {
        return types;
    }

    public EnumSet<PaymentMethod> getPaymentMethods() {
        return paymentMethods;
    }

    public EnumSet<Condition> getConditions() {
        return conditions;
    }

    public EnumSet<CustomerFlag> getCustomerFlags() {
        return customerFlags;
    }

    public EnumSet<Directive> getDirectives() {
        return directives;
    }

}
