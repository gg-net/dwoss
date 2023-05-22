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

import javax.persistence.*;

/**
 * Shop categroy.
 *
 * @author oliver.guenther
 */
public class ShopCategory {

    @Id
    @GeneratedValue
    private long id;

    @Version
    private short optLock;

    @Basic(optional = false)
    private String name;

    private int shopId;

    public ShopCategory() {
    }

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

    @Override
    public String toString() {
        return "ShopCategory{" + "id=" + id + ", name=" + name + ", shopId=" + shopId + '}';
    }

}
