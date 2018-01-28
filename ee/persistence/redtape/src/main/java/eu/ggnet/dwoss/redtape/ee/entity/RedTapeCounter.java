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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Version;

import eu.ggnet.dwoss.rules.DocumentType;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Holds values to create and re-use identifiers.
 * <p/>
 * @author pascal.perau
 */
@Entity
@IdClass(value = RedTapeCounter.Key.class)
@EqualsAndHashCode(of = {"type", "prefix"})
@ToString(exclude = "optLock")
public class RedTapeCounter implements Serializable {

    @EqualsAndHashCode
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class Key implements Serializable {

        private DocumentType type;

        private String prefix;
    }

    @Id
    @Getter
    private DocumentType type;

    @Id
    @Column(name = "counterPrefix")
    @Getter
    private String prefix;

    @Column(name = "counterValue")
    @Getter
    @Setter
    private long value;

    @Version
    private long optLock;

    public RedTapeCounter() {
    }

    public RedTapeCounter(DocumentType idType, String prefixString) {
        this.prefix = prefixString;
        this.type = idType;
    }
}
