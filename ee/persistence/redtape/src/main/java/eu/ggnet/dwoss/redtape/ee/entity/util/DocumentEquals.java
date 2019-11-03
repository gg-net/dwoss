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
package eu.ggnet.dwoss.redtape.ee.entity.util;

import java.util.*;

import org.apache.commons.lang3.time.DateUtils;

import eu.ggnet.dwoss.core.common.values.PositionType;
import eu.ggnet.dwoss.redtape.ee.entity.Document;
import eu.ggnet.dwoss.redtape.ee.entity.Position;

import static eu.ggnet.dwoss.redtape.ee.entity.util.DocumentEquals.Property.*;
/**
 * A Helper class to build different forms of Equalities.
 *
 * @author oliver.guenther
 */
public class DocumentEquals {

    public static enum Property {
        ID, TYPE, ACTIVE, ACTUAL, IDENTIFIER, HISTORY, PREDECESSOR, SETTLEMENTS, DOSSIER, SHIPPING_ADDRESS, INVOICE_ADDRESS, DIRECTIVE, FLAGS, CLOSED, CONDITIONS, TAXTYPE
    }

    private final Set<Property> properties = EnumSet.allOf(Property.class);

    private final Set<PositionType> positionTypes = EnumSet.allOf(PositionType.class);

    private boolean positionOrder = true;

    public DocumentEquals ignore(Property ... properties) {
        this.properties.removeAll(Arrays.asList(properties));
        return this;
    }

    public DocumentEquals ignoreAddresses() {
        properties.remove(INVOICE_ADDRESS);
        properties.remove(SHIPPING_ADDRESS);
        return this;
    }

    /**
     * Enables to ignore some Positions of a type, implies to ignore the order of the positions also.
     *
     * @param types the types to ignore.
     * @return this
     */
    public DocumentEquals ignorePositions(PositionType... types) {
        positionOrder = false;
        positionTypes.removeAll(Arrays.asList(types));
        return this;
    }

    public DocumentEquals igonrePositionOrder() {
        positionOrder = false;
        return this;
    }


    /**
     * Checks if two documents are equal with the given relaxations.
     *
     * @param d1 Document one
     * @param d2 Document two
     * @return true if both are equal
     */
    public boolean equals(Document d1, Document d2) {
        return equalsMessage(d1, d2) == null;
    }

/**
 * Checks if two documents are not equal returning a string containing the message what is not equal or null.
 *
 * @param d1 Document one
 * @param d2 Document two
 * @return null if equal or a string describing the difference.
 */
    public String equalsMessage(Document d1, Document d2) {
        if ( d1 == null ) return "d1 is null";
        if ( d2 == null ) return "d2 is null";
        if ( properties.contains(SETTLEMENTS) ) if ( !Objects.equals(d1.getSettlements(), d2.getSettlements()) ) return "Settlements are not equal, d1=" + d1.getSettlements() + ", d2=" + d2.getSettlements();
        if ( properties.contains(ACTUAL) ) {
            if (d1.getActual() == null && d2.getActual() != null)  return "Actual is not equal, d1=" + d1.getActual() + ", d2=" + d2.getActual();
            if (d1.getActual() != null && d2.getActual() == null)  return "Actual is not equal, d1=" + d1.getActual() + ", d2=" + d2.getActual();
            if ( !DateUtils.isSameDay(d1.getActual(), d2.getActual()) ) return "Actual is not equal, d1=" + d1.getActual() + ", d2=" + d2.getActual();
        }
        if ( properties.contains(IDENTIFIER) ) if ( !Objects.equals(d1.getIdentifier(), d2.getIdentifier()) ) return "Identifier is not equal, d1=" + d1.getIdentifier() + ", d2=" + d2.getIdentifier();
        if ( properties.contains(CONDITIONS) ) if ( !Objects.equals(d1.getConditions(), d2.getConditions()) ) return "Conditions are not equal, d1=" + d1.getConditions() + ", d2=" + d2.getConditions();
        if ( properties.contains(TYPE) ) if ( d1.getType() != d2.getType() ) return "Type is not equal, d1=" + d1.getType() + ", d2=" + d2.getType();
        if ( properties.contains(DOSSIER) ) if ( !d1.getDossier().equals(d2.getDossier()) ) return "Dossier is not equal, d1=" + d1.getDossier() + ", d2=" + d2.getDossier();
        if ( properties.contains(ID) ) if ( d1.getId() != d2.getId() ) return "Id is not equal, d1=" + d1.getId() + ", d2=" + d2.getId();
        if ( properties.contains(ACTIVE) ) if ( d1.isActive() != d2.isActive() ) return "Active is not equal, d1=" + d1.isActive() + ", d2=" + d2.isActive();
        if ( properties.contains(HISTORY) ) if ( !Objects.equals(d1.getHistory(), d2.getHistory()) ) return "History is not equal, d1=" + d1.getHistory() + ", d2=" + d2.getHistory();
        if ( properties.contains(PREDECESSOR) ) if ( !Objects.equals(d1.getPredecessor(), d2.getPredecessor()) ) return "Predecessor is not equal, d1=" + d1.getPredecessor() + ", d2=" + d2.getPredecessor();
        if ( properties.contains(FLAGS) ) if ( !Objects.equals(d1.getFlags(), d2.getFlags()) ) return "Flags is not equal, d1=" + d1.getFlags() + ", d2=" + d2.getFlags();
        if ( properties.contains(DIRECTIVE)) if ( d1.getDirective() != d2.getDirective() ) return "Directive is not equal, d1=" + d1.getDirective() + ", d2=" + d2.getDirective();
        if ( properties.contains(INVOICE_ADDRESS)) if ( !Objects.equals(d1.getInvoiceAddress(), d2.getInvoiceAddress()) ) return "Invoiceaddress is not equal, d1=" + d1.getInvoiceAddress() + ", d2=" + d2.getInvoiceAddress();
        if ( properties.contains(SHIPPING_ADDRESS)) if ( !Objects.equals(d1.getShippingAddress(), d2.getShippingAddress()) ) return "Shippingaddress is not equal, d1=" + d1.getShippingAddress() + ", d2=" + d2.getShippingAddress();
        if ( properties.contains(CLOSED) ) if ( d1.isClosed() != d2.isClosed() ) return "Closed is not equal, d1=" + d1.isClosed() + ", d2=" + d2.isClosed();
        if ( properties.contains(TAXTYPE) ) if ( d1.getTaxType() != d2.getTaxType() )
                return "TaxType is not equal, d1=" + d1.getTaxType() + ", d2=" + d2.getTaxType();
        if ( positionTypes.size() == PositionType.values().length ) if ( d1.getPositions().size() != d2.getPositions().size() ) return "Positions.size is not equal, d1=" + d1.getPositions().size() + ", d2=" + d2.getPositions().size();
        if ( positionTypes.size() == PositionType.values().length && positionOrder ) {
            Iterator<Position> p1 = new TreeSet<>(d1.getPositions().values()).iterator();
            Iterator<Position> p2 = new TreeSet<>(d2.getPositions().values()).iterator();
            while (p1.hasNext()) {
                Position p1p = p1.next();
                Position p2p = p2.next();
                if ( !p1p.equalsContent(p2p) ) return "Positions are not equal d1=" + p1p + ", d2=" + p2p;
            }
        } else {
            List<Position> d1Poss = new ArrayList<>();
            List<Position> d2Poss = new ArrayList<>();
            for (Position pos : d1.getPositions().values()) {
                if ( positionTypes.contains(pos.getType()) ) d1Poss.add(pos);
            }
            for (Position pos : d2.getPositions().values()) {
                if ( positionTypes.contains(pos.getType()) ) d2Poss.add(pos);
            }
            if ( d1Poss.size() != d2Poss.size() ) return "Posistions subSiz is not equal d1=" + d1Poss.size() + ", d2=" + d2Poss.size()
                    + ", evaluating only types " + positionTypes;
            for (Position pos1 : d1Poss) {
                boolean existAndIsEqual = false;
                for (Position pos2 : d2Poss) {
                    if ( pos1.equalsContentWithoutId(pos2) ) existAndIsEqual = true;
                }
                if ( !existAndIsEqual ) return "Subpositions are not equal d1=" + d1Poss + ", d2=" + d2Poss;
            }
        }
        return null;
    }

}
