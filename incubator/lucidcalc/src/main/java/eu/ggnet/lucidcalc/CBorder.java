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

import java.awt.Color;

/**
 *
 */
public class CBorder {

    public static enum LineStyle {

        NONE, THIN, MEDIUM, DASHED, DOTTED, THICK, DOUBLE, HAIR, MEDIUM_DASHED, DASH_DOT, MEDIUM_DASH_DOT, DASH_DOT_DOT, MEDIUM_DASH_DOT_DOT, SLANTED_DASH_DOT
    }

    private Color color;
    private LineStyle lineStyle;

    public CBorder(Color color, LineStyle lineStyle) {
        this.lineStyle = lineStyle;
        this.color = color;
    }

    public CBorder(Color color) {
        this(color, LineStyle.THIN);
    }

    public Color getColor() {
        return color;
    }

    public LineStyle getLineStyle() {
        return lineStyle;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final CBorder other = (CBorder) obj;
        if (this.lineStyle != other.lineStyle) return false;
        if (this.color != other.color && (this.color == null || !this.color.equals(other.color))) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 13 * hash + (this.lineStyle != null ? this.lineStyle.hashCode() : 0);
        hash = 13 * hash + (this.color != null ? this.color.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "CBorder{" + "color=" + color + ", lineStyle=" + lineStyle + '}';
    }
}
