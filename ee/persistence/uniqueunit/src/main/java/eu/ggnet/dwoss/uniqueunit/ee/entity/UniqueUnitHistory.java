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

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringExclude;
import org.slf4j.LoggerFactory;

/**
 * Represents a history element for a {@link UniqueUnit}.
 * <p>
 * @has 1 - 1 Type
 * @author pascal.perau
 */
@Entity
@SuppressWarnings("PersistenceUnitPresent")
public class UniqueUnitHistory implements Serializable, Comparable<UniqueUnitHistory> {

    @Id
    @GeneratedValue
    private long id;

    @Basic
    @Lob
    @Column(length = 65536)
    private String comment;

    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Column(columnDefinition = "DATETIME")
    private Date occurence;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, optional = false)
    @ToStringExclude
    UniqueUnit uniqueUnit;

    /**
     * Id bases Constructor, do not use in production environment
     *
     * @param id
     * @param occurence
     * @param comment
     */
    public UniqueUnitHistory(int id, Date occurence, String comment) {
        LoggerFactory.getLogger(UniqueUnitHistory.class).warn("Creating UniqueUnitHistory with id={}, use only in tests.",id);
        this.id = id;
        this.comment = comment;
        this.occurence = occurence;
    }

    public UniqueUnitHistory(String comment) {
        this.comment = comment;
        this.occurence = new Date();
    }

    public UniqueUnitHistory(Date occurence, String comment) {
        this.comment = comment;
        this.occurence = occurence;
    }

    public UniqueUnitHistory() {
    }

    public long getId() {
        return id;
    }

    public UniqueUnit getUniqueUnit() {
        return uniqueUnit;
    }
    
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
    
    public Date getOccurence() {
        return occurence;
    }

    public void setOccurence(Date occurence) {
        this.occurence = occurence;
    }
    
    @Override
    public int compareTo(UniqueUnitHistory o) {
        if ( this.equals(o) ) return 0;
        if ( this.occurence.equals(o.occurence) ) return (this.hashCode() - o.hashCode()) > 0 ? -1 : 1;
        return this.occurence.compareTo(o.occurence);
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
