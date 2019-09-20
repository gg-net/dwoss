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
package eu.ggnet.dwoss.misc.ee.movement;

import java.util.*;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * A line for the movement reports
 */
// TODO: Model used in jasperreports, modify with control.
public class MovementLine {

    public static List<MovementLine> makeSamples() {
        List<MovementLine> result = new ArrayList<>();
        String lager1 = "Laden (Manhagener Allee)";
        String lager2 = "Lager (Strusbek)";
        String invoiceAddress = "Max Mustermann\n"
                + "Lange Straße 22\n"
                + "22221 Waldweld";
        String deliveryAddress = "Rote Florra e.V.\n"
                + "Max Mustermann\n"
                + "Sternschanze 123\n"
                + "21000 Hamburt\n";
        String c1 = "Ein langer\n"
                + "mehrzeiliger\n"
                + "Komentar.";
        MovementLine l = new MovementLine(1, "Eine Bemerkung", "SP231", "", invoiceAddress, deliveryAddress, "Vorkasse");
        l.addMovementSubline(new MovementSubline(1, "Acer Aspire 7550 (Notebook)", "22001", lager1, true));
        l.addMovementSubline(new MovementSubline(1, "Acer Aspire X3100 (Aspire PC)", "43551", lager1, true));
        l.addMovementSubline(new MovementSubline(1, "Acer Veriton L640 (Commercial PC)", "99421", lager1, true));
        l.addMovementSubline(new MovementSubline(1, "Acer TravelMate 1000", "10001", lager1, false));
        result.add(l);

        l = new MovementLine(1, c1, "DW00485", c1, invoiceAddress, deliveryAddress, "Nachnahme");
        l.addMovementSubline(new MovementSubline(1, "Acer Aspire 4810TG-12G32 (Notebook)", "20103", lager1, true));
        result.add(l);

        l = new MovementLine(2, "", "DW00351", "Fehler im Lager", invoiceAddress, deliveryAddress, "Vorkasse");
        l.addMovementSubline(new MovementSubline(1, "Packard Bell EasyNote 8922 (Notebook)", "148551", lager1, true));
        l.addMovementSubline(new MovementSubline(1, "eMachines E730 (Notebook)", "149521", lager2, false));
        result.add(l);

        return result;
    }

    private long customerId;

    private String customerComment;

    private String dossierIdentifier;

    private String invoiceAddress;

    private String deliveryAddress;

    private String comment;

    private String paymentMethod;

    private List<MovementSubline> movementSublines = new ArrayList<>();

    public MovementLine() {
    }
    
    /**
     * All Parameter Constructor.
     *
     * @param customerId        the id of the customer
     * @param customerComment   the comment for the customer
     * @param dossierIdentifier the business transaction id
     * @param comment           the comment of the business transaction
     * @param invoiceAddress    the invoice address
     * @param deliveryAddress   the delivery address
     * @param paymentMethod     the payment method
     */
    public MovementLine(int customerId, String customerComment, String dossierIdentifier, String comment,
                        String invoiceAddress, String deliveryAddress, String paymentMethod) {
        this.customerId = customerId;
        this.customerComment = customerComment;
        this.dossierIdentifier = dossierIdentifier;
        this.invoiceAddress = invoiceAddress;
        this.deliveryAddress = deliveryAddress;
        this.comment = comment;
        this.paymentMethod = paymentMethod;
    }

    //<editor-fold defaultstate="collapsed" desc="getter/setter">
    public long getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(long customerId) {
        this.customerId = customerId;
    }
    
    public String getCustomerComment() {
        return customerComment;
    }
    
    public void setCustomerComment(String customerComment) {
        this.customerComment = customerComment;
    }
    
    public String getDossierIdentifier() {
        return dossierIdentifier;
    }
    
    public void setDossierIdentifier(String dossierIdentifier) {
        this.dossierIdentifier = dossierIdentifier;
    }
    
    public String getInvoiceAddress() {
        return invoiceAddress;
    }
    
    public void setInvoiceAddress(String invoiceAddress) {
        this.invoiceAddress = invoiceAddress;
    }
    
    public String getDeliveryAddress() {
        return deliveryAddress;
    }
    
    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }
    
    public String getComment() {
        return comment;
    }
    
    public void setComment(String comment) {
        this.comment = comment;
    }
    
    public String getPaymentMethod() {
        return paymentMethod;
    }
    
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    
    public List<MovementSubline> getMovementSublines() {
        return movementSublines;
    }
    
    public void setMovementSublines(List<MovementSubline> movementSublines) {
        this.movementSublines = movementSublines;
    }
    //</editor-fold>
    
    public void addMovementSubline(MovementSubline line) {
        movementSublines.add(line);
    }

    //<editor-fold defaultstate="collapsed" desc="equals and hashCode of all">
    // TODO: Not sure if needed, only here though lombok removal.
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 17 * hash + (int)(this.customerId ^ (this.customerId >>> 32));
        hash = 17 * hash + Objects.hashCode(this.customerComment);
        hash = 17 * hash + Objects.hashCode(this.dossierIdentifier);
        hash = 17 * hash + Objects.hashCode(this.invoiceAddress);
        hash = 17 * hash + Objects.hashCode(this.deliveryAddress);
        hash = 17 * hash + Objects.hashCode(this.comment);
        hash = 17 * hash + Objects.hashCode(this.paymentMethod);
        hash = 17 * hash + Objects.hashCode(this.movementSublines);
        return hash;
    }
    
    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        final MovementLine other = (MovementLine)obj;
        if ( this.customerId != other.customerId ) return false;
        if ( !Objects.equals(this.customerComment, other.customerComment) ) return false;
        if ( !Objects.equals(this.dossierIdentifier, other.dossierIdentifier) ) return false;
        if ( !Objects.equals(this.invoiceAddress, other.invoiceAddress) ) return false;
        if ( !Objects.equals(this.deliveryAddress, other.deliveryAddress) ) return false;
        if ( !Objects.equals(this.comment, other.comment) ) return false;
        if ( !Objects.equals(this.paymentMethod, other.paymentMethod) ) return false;
        if ( !Objects.equals(this.movementSublines, other.movementSublines) ) return false;
        return true;
    }
    //</editor-fold>
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
