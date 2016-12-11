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
 * Represents a simple Formula which can handle Dynamic References
 * new SFormula(CCellReference , "/", CCellReference)
 * new SFormula(CCellReference , "+", CCellReference, "+" , CellReference)
 *
 * Also implements the SAction for static formulas, so it is possible to add a Formula to an STable
 */
public class SFormula implements IFormula {

    private final Object[] elements;

    /**
     * Constructor with Formula Elements.
     * Only Strings and DynamicRefrences are allowed.
     *
     * @param elems
     */
    public SFormula(Object... elems) {
        this.elements = elems;
    }

    /**
     * Hint: Useful to manipulate something later in the code
     *
     * @return the elements
     */
    public Object[] getElements() {
        return elements;
    }

    @Override
    public String toRawFormula() {
        StringBuilder sb = new StringBuilder();
        for (Object elem : elements) {
            if ( elem instanceof CCellReference ) {
                CCellReference ref = (CCellReference)elem;
                // +1 is needed because calc allways start with 1 not with 0
                sb.append(toColumnLetter(ref.getColumnIndex())).append(ref.getRowIndex() + 1);
            } else {
                sb.append(elem);
            }
        }
        return sb.toString();
    }

    private String toColumnLetter(int columnIndex) {
        return String.valueOf((char)(columnIndex + 65));
    }
}
