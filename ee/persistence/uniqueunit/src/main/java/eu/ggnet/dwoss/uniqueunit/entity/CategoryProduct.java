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
 * A human defined collection of products to be viewed together.
 *
 * @author oliver.guenther
 */
@Entity
@SuppressWarnings("PersistenceUnitPresent")
@EqualsAndHashCode(of = {"id"}, callSuper = false)
public class CategoryProduct extends AbstractUnitProduct implements Serializable {

    @Getter
    @Id
    @GeneratedValue
    private long id;

    @Getter
    @Version
    private short optLock;

    /**
     * The name of the Product.
     * <p>
     * It sounds weird but, it can happen, that a Spec overwrites the value
     */
    @Getter
    @Setter
    @Basic(optional = false)
    private String name;

    @Getter
    @Setter
    @Basic
    @Column(length = 65536)
    @Lob
    private String description;

    @NotNull
    @OneToMany(cascade = {MERGE, REFRESH, PERSIST, DETACH}, mappedBy = "categoryProduct")
    List<Product> products = new ArrayList<>(); // Package private for bidirectional handling.

    public void add(Product product) {
        if ( product == null ) return;
        product.setCategoryProduct(this);
    }

    public void remove(Product unit) {
        if ( unit == null ) return;
        unit.setCategoryProduct(null);
    }

    @Override
    public String toString() {
        return "CategoryProduct{" + "id=" + id + ", optLock=" + optLock + ", name=" + name + ", description=" + description + '}';
    }

}
