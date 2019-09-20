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
package eu.ggnet.dwoss.uniqueunit.ee.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;

import eu.ggnet.dwoss.common.ee.BaseEntity;

/**
 * A history to keep track of price changes in {@link UniqueUnit}s and {@link Product}s
 * <p>
 * @author pascal.perau
 */
@Entity
@SuppressWarnings("PersistenceUnitPresent")
public class PriceHistory extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue
    private long id;

    @Version
    private short optLock;

    private PriceType type;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "fromDate")
    private Date date;

    private double price;

    @Lob
    @Column(length = 65536)
    private String comment;

    public PriceHistory() {
    }
    
    public PriceHistory(PriceType type, double price, Date date, String comment) {
        this.type = type;
        this.date = date;
        this.price = price;
        this.comment = comment;
    }

    //<editor-fold defaultstate="collapsed" desc="getter/setter">
    public String getComment() {
        return comment;
    }
    
    public void setComment(String comment) {
        this.comment = comment;
    }
    
    @Override
    public long getId() {
        return id;
    }
    
    public short getOptLock() {
        return optLock;
    }
    
    public PriceType getType() {
        return type;
    }
    
    public Date getDate() {
        return date;
    }
    
    public double getPrice() {
        return price;
    }
    //</editor-fold>
    
}
