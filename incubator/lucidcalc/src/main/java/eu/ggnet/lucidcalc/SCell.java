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

/**
 * A simple cell without position information
 */
public class SCell implements CCellReference {

    private CFormat format;
    private Object value;
    private CCellReference reference;

    public SCell(Object value) {
        this.value = value;
    }

    public SCell(Object value, CFormat format) {
        this.format = format;
        this.value = value;
    }

    public CFormat getFormat() {
        return format;
    }

    public Object getValue() {
        return value;
    }

    public void setReference(CCellReference reference) {
        this.reference = reference;
    }

    @Override
    public int getRowIndex() {
        return reference == null ? -1 : reference.getRowIndex();
    }

    @Override
    public int getColumnIndex() {
        return reference == null ? -1 : reference.getColumnIndex();
    }
}
