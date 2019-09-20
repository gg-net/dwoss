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
package eu.ggnet.dwoss.stock.ee.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import eu.ggnet.dwoss.common.api.values.TradeName;
import eu.ggnet.dwoss.common.ee.BaseEntity;

@Entity
@SuppressWarnings("PersistenceUnitPresent")
public class Shipment extends BaseEntity implements Serializable {

    public static enum Status {

        ANNOUNCED, DELIVERED, OPENED, CLOSED, RECEIPT_PLANED
    }

    @Id
    @GeneratedValue
    private long id;

    private String shipmentId;

    @Lob
    @Column(length = 65536)
    private String comment;

    @Temporal(TemporalType.DATE)
    private Date date;

    @NotNull
    private TradeName contractor;

    @NotNull
    private TradeName defaultManufacturer;

    @NotNull
    @Enumerated(EnumType.ORDINAL)
    private Status status;

    /**
     * DefaultConsturctor with status = ANNOUNCED and date = new Date().
     */
    public Shipment() {
        status = Status.ANNOUNCED;
        date = new Date();
    }

    public Shipment(String shipmentId, TradeName contractor, TradeName defaultManufacturer, Status status) {
        this();
        this.shipmentId = shipmentId;
        this.contractor = contractor;
        this.defaultManufacturer = defaultManufacturer;
        this.status = status;
    }

    //<editor-fold defaultstate="collapsed" desc="getter/setter">
    @Override
    public long getId() {
        return id;
    }
    
    public String getShipmentId() {
        return shipmentId;
    }
    
    public void setShipmentId(String shipmentId) {
        this.shipmentId = shipmentId;
    }
    
    public String getComment() {
        return comment;
    }
    
    public void setComment(String comment) {
        this.comment = comment;
    }
    
    public Date getDate() {
        return date;
    }
    
    public void setDate(Date date) {
        this.date = date;
    }
    
    public TradeName getContractor() {
        return contractor;
    }
    
    public void setContractor(TradeName contractor) {
        this.contractor = contractor;
    }
    
    public TradeName getDefaultManufacturer() {
        return defaultManufacturer;
    }
    
    public void setDefaultManufacturer(TradeName defaultManufacturer) {
        this.defaultManufacturer = defaultManufacturer;
    }
    
    public Status getStatus() {
        return status;
    }
    //</editor-fold>
    
    /**
     * Sideeffect, updates Date.
     *
     * @param status
     */
    public void setStatus(Status status) {
        this.date = new Date();
        this.status = status;
    }

}
