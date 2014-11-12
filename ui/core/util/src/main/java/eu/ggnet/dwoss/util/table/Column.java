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
package eu.ggnet.dwoss.util.table;

/**
 *
 */
public class Column<T> {

    private String headline = "";
    private boolean editable = false;
    private int preferredWidth = 10;
    private Class<?> clazz = Object.class;
    private IColumnGetAction get = null;
    private IColumnGetSetAction set = null;

    public Column(String headline, boolean editable, int preferredWidth, Class<?> clazz, IColumnGetAction action) {
        this.headline = headline;
        this.editable = editable;
        this.preferredWidth = preferredWidth;
        this.clazz = clazz;
        if (action instanceof IColumnGetAction) {
            this.get = (IColumnGetAction) action;
        }
        if (action instanceof IColumnGetSetAction) {
            this.set = (IColumnGetSetAction) action;
        }
    }

    public boolean isEditable() {
        return editable;
    }

    public String getHeadline() {
        return headline;
    }

    public int getPreferredWidth() {
        return preferredWidth;
    }

    public Object getValue(int row) {
        return get.getValue(row);
    }

    public void setValue(int row, Object value) {
        if (!isEditable()) return;
        if (set == null) return;
        set.setValue(row, value);
    }

    public Class<?> getColumnClass() {
        return clazz;
    }
}
