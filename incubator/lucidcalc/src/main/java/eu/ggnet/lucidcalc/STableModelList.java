/* 
 * Copyright (C) 2014 GG-Net GmbH - Oliver Günther.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; If not, see <http://www.gnu.org/licenses/>.
 */
package eu.ggnet.lucidcalc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.validation.constraints.NotNull;

/**
 *
 */
public class STableModelList<T> implements STableModel {

    @NotNull
    private List<T> rows;

    public STableModelList(T[] rows) {
        this.rows = Arrays.asList(rows);
    }

    public STableModelList(Collection<T> rows) {
        this.rows = new ArrayList<T>(rows);
    }

    @Override
    public T getRow(int row) {
       return rows.get(row);
    }

    @Override
    public int getRowCount() {
        return rows.size();
    }

}
