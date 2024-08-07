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

import eu.ggnet.dwoss.core.common.values.ShipmentStatus;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import eu.ggnet.dwoss.core.common.values.tradename.TradeName;
import eu.ggnet.dwoss.core.system.persistence.BaseEntity;
import eu.ggnet.dwoss.stock.api.StockApi;

@Entity
@SuppressWarnings("PersistenceUnitPresent")
public class Shipment extends BaseEntity implements Serializable {


    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
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
    private ShipmentStatus status;
    
    /**
     * Amount of Units on the Shipment, based on the Delivery note.
     */    
    private int amountOfUnits;
    
    /**
     * DefaultConsturctor with status = ANNOUNCED and date = new Date().
     */
    public Shipment() {
        status = ShipmentStatus.ANNOUNCED;
        date = new Date();
        amountOfUnits = 0;
    }

    public Shipment(String shipmentId, TradeName contractor, TradeName defaultManufacturer, ShipmentStatus status) {
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
    
    public ShipmentStatus getStatus() {
        return status;
    }

    public int getAmountOfUnits() {
        return amountOfUnits;
    }

    public void setAmountOfUnits(int amountOfUnits) {
        this.amountOfUnits = amountOfUnits;
    }
    //</editor-fold>
    
    public StockApi.SimpleShipment toSimple() {
        return new StockApi.SimpleShipment(id, shipmentId);
    }
    
    /**
     * Sideeffect, updates Date.
     *
     * @param status
     */
    public void setStatus(ShipmentStatus status) {
        this.date = new Date();
        this.status = status;
    }
   
}
