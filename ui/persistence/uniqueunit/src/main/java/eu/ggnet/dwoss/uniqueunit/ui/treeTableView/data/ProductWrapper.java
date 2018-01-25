/*
 * Copyright (C) 2018 GG-Net GmbH
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
package eu.ggnet.dwoss.uniqueunit.ui.treeTableView.data;

import javafx.scene.control.TreeItem;

import eu.ggnet.dwoss.uniqueunit.entity.Product;

import lombok.Setter;

/**
 *
 * @author lucas.huelsen
 */
public class ProductWrapper implements DataWrapper {

    private final TreeItem<DataWrapper> item;

    private final Product product;

    @Setter
    private boolean loading = false;

    public ProductWrapper(TreeItem<DataWrapper> item, Product product) {
        this.item = item;
        this.product = product;
    }

    public boolean isLoading() {
        return loading;
    }

    @Override
    public String getName() {
        return product.getName();
    }

}
