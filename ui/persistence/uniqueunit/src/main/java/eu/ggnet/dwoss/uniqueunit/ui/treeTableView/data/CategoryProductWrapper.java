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

import eu.ggnet.dwoss.uniqueunit.entity.CategoryProduct;

/**
 *
 * @author lucas.huelsen
 */
public class CategoryProductWrapper implements DataWrapper {

    private final TreeItem<DataWrapper> item;

    private boolean loading = false;

    private final CategoryProduct cp;

    public CategoryProductWrapper(TreeItem<DataWrapper> item, CategoryProduct cp) {
        this.item = item;
        this.cp = cp;
    }

    public boolean isLoading() {
        return loading;
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
    }

    @Override
    public String getName() {
        return cp.getName();
    }

}
