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

import java.util.Arrays;

/**
 * This is a shortcut to add a Formula to a table directly as action
 *
 */
public class SFormulaAction extends SFormula implements SAction<Object> {

    public SFormulaAction(Object... elems) {
        super(elems);
    }

    @Override
    public Object getValue(int relativeColumnIndex, int relativeRowIndex, int absoluteColumnIndex, int absoluteRowIndex, Object lineModel) {
        Object[]elements = Arrays.copyOf(super.getElements(),super.getElements().length);
        for (int i = 0; i < elements.length; i++) {
            Object elem = elements[i];
            if ( elem instanceof SSelfRowReference ) {
                SSelfRowReference selfRow = (SSelfRowReference)elem;
                elements[i] = new CCellReferenceAdapter(absoluteRowIndex,selfRow.getColumnIndex());
            }
        }
        return new SFormula(elements);
    }

    @Override
    public CFormat getFormat(int relativeColumnIndex, int relativeRowIndex, int absoluteColumnIndex, int absoluteRowIndex, Object lineModel) {
        return null;
    }
}
