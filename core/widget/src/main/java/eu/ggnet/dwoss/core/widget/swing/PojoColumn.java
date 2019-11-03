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
package eu.ggnet.dwoss.core.widget.swing;

import eu.ggnet.dwoss.core.widget.swing.PojoUtil;

public class PojoColumn<T> {

    private String headline = "";
    private boolean editable = false;
    private int preferredWidth = 10;
    private Class<?> clazz = Object.class;
    private String propertyName;

    public PojoColumn(String headline, boolean editable, int preferredWidth, Class<?> clazz, String propertyName) {
        this.headline = headline;
        this.editable = editable;
        this.preferredWidth = preferredWidth;
        this.clazz = clazz;
        this.propertyName = propertyName;
    }

    public boolean isEditable() {
        return false;
    }

    public String getHeadline() {
        return headline;
    }

    public int getPreferredWidth() {
        return preferredWidth;
    }

    public Object getValue(T elem) {
        return PojoUtil.getValue(propertyName, elem);
    }

    public Class<?> getColumnClass() {
        return clazz;
    }

}
