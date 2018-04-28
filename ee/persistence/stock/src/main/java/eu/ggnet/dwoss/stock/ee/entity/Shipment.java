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
import eu.ggnet.dwoss.util.persistence.entity.Identifiable;

import lombok.*;

@Entity
@Getter
@ToString
public class Shipment implements Serializable, Identifiable {

    public static enum Status {

        ANNOUNCED, DELIVERED, OPENED, CLOSED, RECEIPT_PLANED
    }

    @Id
    @GeneratedValue
    private long id;

    @Setter
    private String shipmentId;

    @Lob
    @Column(length = 65536)
    @Setter
    private String comment;

    @Temporal(TemporalType.DATE)
    @Setter
    private Date date;

    @NotNull
    @Setter
    private TradeName contractor;

    @NotNull
    @Setter
    private TradeName defaultManufacturer;

    @NotNull
    @Enumerated(EnumType.ORDINAL)
    private Status status;

    private static final long serialVersionUID = 1L;

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
