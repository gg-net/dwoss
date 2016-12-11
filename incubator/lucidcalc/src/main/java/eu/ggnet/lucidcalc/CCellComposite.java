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

import java.util.HashSet;
import java.util.Set;

/**
 * A Composite of cells and Metainformations.
 */
public class CCellComposite {

    protected Set<CCell> cells;
    protected Set<CColumnView> columnViews;
    protected Set<CRowView> rowViews;

    public CCellComposite() {
        cells = new HashSet<CCell>();
        columnViews = new HashSet<CColumnView>();
        rowViews = new HashSet<CRowView>();
    }

    public CCellComposite(Set<CCell> cells) {
        this.cells = cells;
    }

    public CCellComposite(Set<CCell> cells, Set<CColumnView> columnViews, Set<CRowView> rowViews) {
        if (cells != null) this.cells = cells;
        if (columnViews != null) this.columnViews = columnViews;
        if (rowViews != null) this.rowViews = rowViews;
    }

    public Set<CCell> getCells() {
        return cells;
    }

    public Set<CColumnView> getColumnViews() {
        return columnViews;
    }

    public Set<CRowView> getRowViews() {
        return rowViews;
    }
}
