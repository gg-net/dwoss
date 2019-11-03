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
import java.util.Objects;

import javax.persistence.*;

import org.apache.commons.lang3.builder.ToStringBuilder;

import eu.ggnet.dwoss.core.common.values.DocumentType;

/**
 * Holds values to create and re-use identifiers.
 * <p>
 * @author pascal.perau
 */
@Entity
@IdClass(value = RedTapeCounter.Key.class)
@SuppressWarnings("PersistenceUnitPresent")
public class RedTapeCounter implements Serializable {

    public static class Key implements Serializable {

        private DocumentType type;

        private String prefix;

        public Key(DocumentType type, String prefix) {
            this.type = type;
            this.prefix = prefix;
        }

        public Key() {
        }

        //<editor-fold defaultstate="collapsed" desc="equals and hashCode of all">
        @Override
        public int hashCode() {
            int hash = 3;
            hash = 79 * hash + Objects.hashCode(this.type);
            hash = 79 * hash + Objects.hashCode(this.prefix);
            return hash;
        }
        
        @Override
        public boolean equals(Object obj) {
            if ( this == obj ) return true;
            if ( obj == null ) return false;
            if ( getClass() != obj.getClass() ) return false;
            final Key other = (Key)obj;
            if ( !Objects.equals(this.prefix, other.prefix) ) return false;
            if ( this.type != other.type ) return false;
            return true;
        }
        //</editor-fold>
        
        @Override
        public String toString() {
            return "Key{" + "type=" + type + ", prefix=" + prefix + '}';
        }
                
    }

    @Id
    private DocumentType type;

    @Id
    @Column(name = "counterPrefix")
    private String prefix;

    @Column(name = "counterValue")
    private long value;

    @Version
    private long optLock;

    public RedTapeCounter() {
    }

    public RedTapeCounter(DocumentType idType, String prefixString) {
        this.prefix = prefixString;
        this.type = idType;
    }

    //<editor-fold defaultstate="collapsed" desc="getter/setter">
    public long getValue() {
        return value;
    }
    
    public void setValue(long value) {
        this.value = value;
    }
    
    public DocumentType getType() {
        return type;
    }
    
    public String getPrefix() {
        return prefix;
    }
    
    public long getOptLock() {
        return optLock;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="equals and hashCode of type,prefix">
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + Objects.hashCode(this.type);
        hash = 17 * hash + Objects.hashCode(this.prefix);
        return hash;
    }
    
    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        final RedTapeCounter other = (RedTapeCounter)obj;
        if ( !Objects.equals(this.prefix, other.prefix) ) return false;
        if ( this.type != other.type ) return false;
        return true;
    }
    //</editor-fold>
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
    
}
