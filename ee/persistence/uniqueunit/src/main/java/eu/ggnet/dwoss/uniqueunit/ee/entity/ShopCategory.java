/*
 * Copyright (C) 2023 GG-Net GmbH
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

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import eu.ggnet.dwoss.core.system.persistence.BaseEntity;

/**
 * Shop categroy.
 *
 * @author oliver.guenther
 */
@Entity
@SuppressWarnings("PersistenceUnitPresent")
public class ShopCategory extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private long id;

    @Version
    private short optLock;

    @Basic(optional = false)
    @NotNull
    private String name;

    private int shopId;

    public ShopCategory() {
    }

    @Override
    public long getId() {
        return id;
    }

    public short getOptLock() {
        return optLock;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getShopId() {
        return shopId;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
    }

    public eu.ggnet.dwoss.uniqueunit.api.ShopCategory toApi() {
        return new eu.ggnet.dwoss.uniqueunit.api.ShopCategory.Builder()
                .id(getId())
                .name(getName())
                .shopId(getShopId())
                .build();
    }
    
    @Override
    public String toString() {
        return "ShopCategory{" + "id=" + id + ", name=" + name + ", shopId=" + shopId + '}';
    }

}
