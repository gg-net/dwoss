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

import lombok.*;

/**
 * A history to keep track of price changes in {@link UniqueUnit}s and {@link Product}s
 * <p>
 * @author pascal.perau
 */
@Entity
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString
@SuppressWarnings("PersistenceUnitPresent")
public class PriceHistory implements Serializable {

    @Getter
    @Id
    @GeneratedValue
    private long id;

    @Version
    private short optLock;

    @Getter
    private PriceType type;

    @Getter
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "fromDate")
    private Date date;

    @Getter
    private double price;

    @Lob
    @Getter
    @Setter
    @Column(length = 65536)
    private String comment;

    public PriceHistory(PriceType type, double price, Date date, String comment) {
        this.type = type;
        this.date = date;
        this.price = price;
        this.comment = comment;
    }

}
