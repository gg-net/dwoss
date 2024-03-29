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
package eu.ggnet.dwoss.redtape.ee.entity;

import eu.ggnet.dwoss.core.common.values.PositionType;
import eu.ggnet.dwoss.core.common.values.PaymentMethod;
import eu.ggnet.dwoss.core.common.values.DocumentType;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

import eu.ggnet.dwoss.core.system.persistence.BaseEntity;
import eu.ggnet.dwoss.redtape.ee.entity.Document.Condition;
import eu.ggnet.dwoss.redtape.ee.entity.Document.Directive;
import eu.ggnet.dwoss.core.system.persistence.EagerAble;

import static eu.ggnet.dwoss.core.common.values.DocumentType.*;

/**
 * The Dossier.
 * <p>
 * Rules for a valid Dossier:
 * <ul>
 * <li>Must have at least one active {@link Document} of {@link DocumentType#ORDER} or {@link DocumentType#INVOICE}</li>
 * <li>May have at most one active {@link Document} of {@link DocumentType#ORDER} and one active {@link Document} of {@link DocumentType#INVOICE}</li>
 * <li>May have multiple active {@link Document} of {@link DocumentType#CREDIT_MEMO}</li>
 * </ul>
 * <p>
 * @has 1 - 2 Address
 * @has 1 - n Dossier.Directive
 * @has m - n Dossier.Condition
 * @has 1 - n DirectiveHistory
 * @has 1 - n ConditionHistory
 * @has 1 - n Document
 * @author bastian.venz, oliver.guenther, pascal.perau
 */
@Entity
@NamedQuery(name = "Dossier.byCustomerId", query = "select d from Dossier d where d.customerId = ?1")
@NamedQuery(name = "Dossier.byCustomerIdAndClosed", query = "select d from Dossier as d where d.customerId = ?1 and d.closed = ?2 ORDER BY d.identifier DESC")
@NamedQuery(name = "Dossier.byDossierIds", query = "select d from Dossier d where d.id in (?1)")
@NamedQuery(name = "Dossier.byClosed", query = "select d from Dossier as d where d.closed = ?1")
@NamedQuery(name = "Dossier.byIdentifier", query = "select d from Dossier d where d.identifier like ?1 ORDER BY d.identifier DESC")
@NamedQuery(name = "Dossier.allDescending", query = "select d from Dossier d ORDER BY d.id DESC")
@SuppressWarnings("PersistenceUnitPresent")
public class Dossier extends BaseEntity implements Serializable, EagerAble {

    /**
     * Comperator for an inverse order using the actual date of the first active document.
     */
    public static final Comparator<Dossier> ORDER_INVERSE_ACTIVE_ACTUAL = (Dossier o1, Dossier o2) -> {
        Document d1 = o1.getActiveDocuments().get(0);
        Document d2 = o2.getActiveDocuments().get(0);
        if ( d1.equals(d2) ) return 0;
        if ( d1.getActual().equals(d2.getActual()) ) {
            if ( d1.getConditions().contains(Document.Condition.CANCELED) ) return +1;
            return -1;
        }
        return d2.getActual().compareTo(d1.getActual());
    };

    public static NavigableSet<Long> toIds(Collection<Dossier> dossiers) {
        NavigableSet<Long> result = new TreeSet<>();
        for (Dossier dos : dossiers) {
            result.add(dos.getId());
        }
        return result;
    }

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private long id;

    @Version
    private short optLock = 0;

    @Lob
    @Column(length = 65536)
    private String comment;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "dossier")
    @Valid
    Set<Document> documents = new HashSet<>();

    @Enumerated
    private PaymentMethod paymentMethod;

    @Min(1)
    private long customerId;

    /**
     * Indicates, that this is a dispatch (Versand) dossier.
     */
    private boolean dispatch;

    /**
     * Indicates, that this Dossier has no Documents in a state, which demand something.
     */
    private boolean closed;

    /**
     * This String identifies the Dossier.
     */
    private String identifier;

    @Valid
    @Embedded
    private Reminder reminder;

    public Dossier() {
    }

    public Dossier(PaymentMethod paymentMethod, boolean dispatch, long customerId) {
        this.paymentMethod = paymentMethod;
        this.customerId = customerId;
        this.dispatch = dispatch;
    }

    //<editor-fold defaultstate="collapsed" desc="setter/getter">
    @Override
    public long getId() {
        return id;
    }
    
    public Short getOptLock() {
        return optLock;
    }
    
    public String getComment() {
        return comment;
    }
    
    public void setComment(String comment) {
        this.comment = comment;
    }
    
    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }
    
    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    
    public long getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(long customerId) {
        this.customerId = customerId;
    }
    
    public boolean isDispatch() {
        return dispatch;
    }
    
    public void setDispatch(boolean dispatch) {
        this.dispatch = dispatch;
    }
    
    public String getIdentifier() {
        return identifier;
    }
    
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
    
    public Reminder getReminder() {
        return reminder;
    }
    
    public void setReminder(Reminder reminder) {
        this.reminder = reminder;
    }
    //</editor-fold>
    
    /**
     * Generates a list of all {@link Position#uniqueUnitId}s of {@link PositionType#UNIT} that are relevant to the recent state of the Dossier. <br>
     * If active Documents of {@link DocumentType#BLOCK}, {@link DocumentType#RETURNS} or {@link DocumentType#CAPITAL_ASSET} exist, all uniqueUniteIds are
     * taken.<br>
     * Any of the found positions used in a successor of {@link DocumentType#INVOICE} is subtracted from the list.<br>
     * The following cases will always return an empty list:
     * <ul>
     * <li>The Dossier is closed</li>
     * <li>Only an active {@link Document} of {@link Type#ORDER} exists and is canceled</li>
     * </ul>
     * <p>
     * @return a list of all {@link Position#uniqueUnitId} of {@link Position.Type#UNIT} that are relevant to the recent state of the Dossier.
     */
    public Set<Integer> getRelevantUniqueUnitIds() {
        Set<Integer> unitIds = new HashSet<>();
        if ( closed ) return unitIds;
        if ( getActiveDocuments().size() == 1 && getActiveDocuments().get(0).getConditions().contains(Document.Condition.CANCELED) ) return unitIds;

        Document doc = null;
        if ( !getActiveDocuments(DocumentType.BLOCK).isEmpty() ) doc = getActiveDocuments(DocumentType.BLOCK).get(0);
        else if ( !getActiveDocuments(DocumentType.RETURNS).isEmpty() ) doc = getActiveDocuments(DocumentType.RETURNS).get(0);
        else if ( !getActiveDocuments(DocumentType.CAPITAL_ASSET).isEmpty() ) doc = getActiveDocuments(DocumentType.CAPITAL_ASSET).get(0);
        else if ( getActiveDocuments(DocumentType.INVOICE).isEmpty() ) doc = getActiveDocuments(DocumentType.ORDER).get(0);

        if ( doc != null ) {
            for (Position position : doc.getPositions(PositionType.UNIT).values()) {
                unitIds.add(position.getUniqueUnitId());
            }
            return unitIds;
        }

        // From here on it's clear that a Invoice exists, so only the Repaymend Options must be subtracted.
        unitIds.addAll(getActiveDocuments(DocumentType.INVOICE).get(0).getPositionsUniqueUnitIds());
        Set<Integer> successors = new HashSet<>();
        if ( !getActiveDocuments(DocumentType.CREDIT_MEMO).isEmpty() )
            successors.addAll(getActiveDocuments(DocumentType.CREDIT_MEMO).get(0).getPositionsUniqueUnitIds());
        if ( !getActiveDocuments(DocumentType.ANNULATION_INVOICE).isEmpty() )
            successors.addAll(getActiveDocuments(DocumentType.ANNULATION_INVOICE).get(0).getPositionsUniqueUnitIds());

        for (Integer uuId : successors) {
            unitIds.remove(uuId);
        }
        return unitIds;
    }

    public Dossier add(Document document) {
        if ( document == null ) return this;
        document.setDossier(this);
        return this;
    }

    public Document remove(Document document) {
        if ( document == null ) return null;
        document.setDossier(null);
        return document;
    }

    /**
     * Returns all documents of this dossier in an unmodifiable set.
     *
     * @return all documents of this dossier in an unmodifiable set.
     */
    public Set<Document> getDocuments() {
        return Collections.unmodifiableSet(documents);
    }

    /**
     * Returns all {@link Document} with {@link Document#active}<code>=true</code> and the supplied {@link DocumentType}.
     * <p>
     * @param types the type to filter by.
     * @return a List of Documents.
     */
    public List<Document> getActiveDocuments(DocumentType... types) {
        final List<DocumentType> typesList = (types == null || types.length == 0) ? Arrays.asList(DocumentType.values()) : Arrays.asList(types);
        return documents.stream().filter(Document::isActive).filter(d -> typesList.contains(d.getType())).collect(Collectors.toList());
    }

    public boolean isClosed() {
        return closed;
    }

    /**
     * Returns the most important Document.
     * <p/>
     * @return the most important Document.
     */
    public Document getCrucialDocument() {
        List<DocumentType> typeOrder = Arrays.asList(ANNULATION_INVOICE, CREDIT_MEMO, COMPLAINT);
        for (DocumentType type : typeOrder) {
            for (Document document : getActiveDocuments(type)) {
                if ( !document.isClosed() ) return document;
            }
        }

        if ( !getActiveDocuments(INVOICE).isEmpty() ) return getActiveDocuments(INVOICE).get(0);
        return getActiveDocuments().get(0);
    }

    /**
     * Returns the crucial directive for this dossier.
     *
     * @return the crucial directive
     */
    public Document.Directive getCrucialDirective() {
        if ( !this.getActiveDocuments(DocumentType.CREDIT_MEMO).isEmpty() ) {
            for (Document document : getActiveDocuments(DocumentType.CREDIT_MEMO)) {
                if ( document.getDirective() == Directive.BALANCE_REPAYMENT ) return Directive.BALANCE_REPAYMENT;
            }
            return Directive.NONE;
        }
        if ( !this.getActiveDocuments(DocumentType.ANNULATION_INVOICE).isEmpty() ) {
            for (Document document : getActiveDocuments(DocumentType.ANNULATION_INVOICE)) {
                if ( document.getDirective() == Directive.BALANCE_REPAYMENT ) return Directive.BALANCE_REPAYMENT;
            }
            return Directive.NONE;
        }
        if ( !this.getActiveDocuments(DocumentType.COMPLAINT).isEmpty() ) {
            for (Document document : getActiveDocuments(DocumentType.COMPLAINT)) {
                if ( document.getConditions().contains(Condition.REJECTED) || document.getConditions().contains(Condition.WITHDRAWN) )
                    return this.getActiveDocuments(DocumentType.INVOICE).get(0).getDirective();
            }
            return this.getActiveDocuments(DocumentType.COMPLAINT).get(0).getDirective();
        }
        if ( !this.getActiveDocuments(DocumentType.INVOICE).isEmpty() ) return this.getActiveDocuments(DocumentType.INVOICE).get(0).getDirective();
        if ( !this.getActiveDocuments(DocumentType.ORDER).isEmpty() ) return this.getActiveDocuments(DocumentType.ORDER).get(0).getDirective();
        if ( !getActiveDocuments().isEmpty() ) return getActiveDocuments().get(0).getDirective();
        return Directive.NONE;
    }

    /**
     * Returns true, if two dossiers equal or the changes differ in a way that the states allow them.
     * Allowed Chages are:
     * <ul>
     * <li>If closed only Address and the Reminder</li>
     * <li>If not closed, but the active Invoice/Order is closed only Address and the Reminder</li>
     * <li>Else everything</li>
     * </ul>
     *
     * @param dos the {@link Dossier}
     * @return true, if two dossiers equal or the changes differ in a way that the states allow them.
     */
    public boolean changesAllowed(Dossier dos) {
        boolean restricted = closed;
        if ( !restricted ) {
            if ( !getActiveDocuments(DocumentType.INVOICE).isEmpty() && getActiveDocuments(DocumentType.INVOICE).get(0).isClosed() ) {
                restricted = true;
            }
        }
        if ( !restricted ) {
            if ( !getActiveDocuments(DocumentType.ORDER).isEmpty() && getActiveDocuments(DocumentType.ORDER).get(0).isClosed() ) {
                restricted = true;
            }
        }
        if ( !restricted ) return true;
        // So we are restricted
        if ( this.paymentMethod != dos.paymentMethod ) return false;
        if ( this.customerId != dos.customerId ) return false;
        if ( this.dispatch != dos.dispatch ) return false;
        if ( !Objects.equals(this.identifier, dos.identifier) ) return false;
        return true;
    }

    @Override
    public void fetchEager() {
        for (Document document : documents) {
            document.fetchEager();
        }
    }

    /**
     * This method sets the closed state of a Dossier.
     * <p/>
     * @param closed
     */
    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    @Override
    public String toString() {
        List<String> docs = new ArrayList<>();
        for (Document d : documents) {
            docs.add("Document{" + d.getId() + "," + d.getType() + ", active=" + d.isActive() + "}");
        }
        return "Dossier{" + "id=" + id + ", comment=" + comment + ", documents=" + docs + ", paymentMethod=" + paymentMethod + ", customerId="
                + customerId + ", dispatch=" + dispatch + ", closed=" + closed + ", identifier=" + identifier + ", reminder=" + reminder + '}';
    }

    public String toSimpleLine() {
        StringBuilder sb = new StringBuilder("Dossier{id=");
        sb.append(id).append(",identifier=").append(identifier);
        if ( closed ) sb.append(",closed");
        sb.append(",paymentMethod=").append(paymentMethod);
        sb.append(",directive=").append(getCrucialDirective());
        sb.append("}");
        return sb.toString();
    }

    public String toMultiLine() {
        return toMultiLine(false, true);
    }

    /**
     * Returns a multi line representation of the Dossier.
     * <p>
     * @param showActiveOnly show only active documents.
     * @param showPositions  show also positions.
     * @return a multi line representation of the Dossier
     */
    public String toMultiLine(boolean showActiveOnly, boolean showPositions) {
        StringBuilder sb = new StringBuilder(toSimpleLine());
        for (Document doc : (showActiveOnly ? getActiveDocuments() : documents)) {
            sb.append("\n - ").append(doc.toSimpleLine());
            if ( !showPositions ) continue;
            for (Position pos : doc.getPositions().values()) {
                sb.append("\n   - ").append(pos.toSimpleLine());
            }
        }
        return sb.toString();
    }

}
