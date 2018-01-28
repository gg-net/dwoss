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

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * A Class to Represent an Interval of Dates to realize the reminding of customers.
 * 
 * @author oliver.guenther
 */
@Embeddable
public class Reminder implements Serializable {
    
    @NotNull
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Column(columnDefinition="DATETIME")
    private Date reminded;

    @NotNull
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Column(columnDefinition="DATETIME")
    private Date expiration;
    
    @NotNull
    @Size(min=1)
    private String arranger;
    
    public Reminder() {}

    public Reminder(Date reminded, Date expiration, String arranger) {
        this.reminded = reminded;
        this.expiration = expiration;
        this.arranger = arranger;
    }

    public Date getReminded() {
        return reminded;
    }

    public void setReminded(Date reminded) {
        this.reminded = reminded;
    }

    public Date getExpiration() {
        return expiration;
    }

    public void setExpiration(Date expiration) {
        this.expiration = expiration;
    }

    public String getArranger() {
        return arranger;
    }

    public void setArranger(String arranger) {
        this.arranger = arranger;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.reminded);
        hash = 79 * hash + Objects.hashCode(this.expiration);
        hash = 79 * hash + Objects.hashCode(this.arranger);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        final Reminder other = (Reminder)obj;
        if ( !Objects.equals(this.reminded, other.reminded) ) return false;
        if ( !Objects.equals(this.expiration, other.expiration) ) return false;
        if ( !Objects.equals(this.arranger, other.arranger) ) return false;
        return true;
    }
    
    @Override
    public String toString() {
        return "Reminder{" + "reminded=" + reminded + ", expiration=" + expiration + ", arranger=" + arranger + '}';
    }
    
}
