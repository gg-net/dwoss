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

import eu.ggnet.dwoss.common.api.values.ProductGroup;
import eu.ggnet.dwoss.common.api.values.TradeName;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author lucas.huelsen
 */
public class ProductGroupWrapper implements DataWrapper {

    @Getter
    private final ProductGroup productGroup;

    @Getter
    private final TradeName tradeName;

    private final TreeItem<DataWrapper> item;

    @Setter
    private boolean loading = false;

    public ProductGroupWrapper(ProductGroup productGroup, TradeName tradeName, TreeItem<DataWrapper> item) {
        this.productGroup = productGroup;
        this.tradeName = tradeName;
        this.item = item;
    }

    public boolean isLoading() {
        return loading;
    }

    @Override
    public String getName() {
        return productGroup.name();
    }

}
