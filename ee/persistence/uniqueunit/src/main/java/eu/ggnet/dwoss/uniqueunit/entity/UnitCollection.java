/*
 * Copyright (C) 2017 GG-Net GmbH
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
package eu.ggnet.dwoss.uniqueunit.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import lombok.*;

import static javax.persistence.CascadeType.*;

/**
 * Represents a collection of units from the same product but not only the product.
 *
 * @author oliver.guenther
 */
@Entity
@Getter
@EqualsAndHashCode(of = {"id"}, callSuper = false)
@SuppressWarnings("PersistenceUnitPresent")
public class UnitCollection extends AbstractUnitProduct implements Serializable {

    @Id
    @GeneratedValue
    private long id;

    @Version
    private short optLock;

    /**
     * Should always be seen like: product.name - unitcollection.nameExtenstion.
     */
    @Setter
    @Basic(optional = false)
    private String nameExtension;

    /**
     * Should always be seen like: product.description - unitcollection.descriptionExtension.
     */
    @Setter
    @Basic
    @Column(length = 65536)
    @Lob
    private String descriptionExtension;

    @NotNull
    @ManyToOne(cascade = {PERSIST, REFRESH, DETACH, MERGE})
    private Product product;

    @NotNull
    @OneToMany(cascade = {MERGE, REFRESH, PERSIST, DETACH}, mappedBy = "unitCollection")
    List<UniqueUnit> units = new ArrayList<>(); // Package private for bidirectional handling.

    /**
     * Sets the {@link Product} in consideration of equalancy and bidirectional
     * behaviour.
     * <p>
     * @param product
     */
    @SuppressWarnings("null")
    public void setProduct(Product product) {
        if ( product == null && this.product == null ) {
            return;
        }
        if ( this.product != null && this.product.equals(product) ) {
            return;
        }
        if ( this.product != null ) {
            this.product.unitCollections.remove(this);
        }
        if ( product != null ) {
            product.unitCollections.add(this);
        }
        this.product = product;
    }

    public void addUnit(UniqueUnit unit) {
        if ( unit == null ) return;
        unit.setUnitCollection(this);
    }

    public void removeUnit(UniqueUnit unit) {
        if ( unit == null ) return;
        unit.setUnitCollection(null);
    }

    @Override
    public String toString() {
        String productString = null;
        if ( product != null ) {
            productString = "[" + product.getPartNo() + "]" + product.getTradeName() + " " + product.getName();
        }

        return "UnitCollection{" + "id=" + id + ", optLock=" + optLock + ", nameExtension=" + nameExtension + ", descriptionExtension=" + descriptionExtension
                + ", product=" + productString + '}';
    }


}
