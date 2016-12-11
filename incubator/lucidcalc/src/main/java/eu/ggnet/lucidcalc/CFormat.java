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
 * The Format for any TemplateElement.
 * The Hierarchy is as follows (First overrides last):
 * <nl>
 * <li>Cell</li>
 * <li>Line</li>
 * <li>Column</li>
 * <li>Sheet</li>
 * <li>Document</li>
 * <li><italic>Default</italic></li>
 * </nl>
 * Null values mean no override.
 */
public class CFormat {

    public static enum FontStyle {

        BOLD, ITALIC, BOLD_ITALIC, NORMAL
    }

    public static enum HorizontalAlignment {

        LEFT, CENTER, RIGHT
    }

    public static enum VerticalAlignment {

        TOP, MIDDLE, BOTTOM
    }

    public static enum Representation {

        DEFAULT, TEXT, PERCENT_INTEGER, PERCENT_FLOAT, SHORT_DATE, CURRENCY_EURO
    }

    /**
     * Fills values in the primary format which are null with values of the secondary format
     *
     * @param primary the primary format
     * @param secondary the secondary format
     * @return a combination of both
     */
    public static CFormat combine(CFormat primary, CFormat secondary) {
        if (primary == null && secondary == null) return null;
        else if (secondary == null) return primary;
        else if (primary == null) return secondary;
        return primary.fillNull(secondary);
    }

    private String name;
    private Integer size;
    private FontStyle style;
    private Color foreground;
    private Color background;
    private HorizontalAlignment horizontalAlignment;
    private VerticalAlignment verticalAlignment;
    private Representation representation;
    private CBorder border;
    private Boolean wrap;

    public CFormat(String name, Integer size, FontStyle style, Color foreground, Color background, HorizontalAlignment horizontalAlignment,
            VerticalAlignment verticalAlignment, Representation representation, CBorder border, Boolean wrap) {
        this.name = name;
        this.size = size;
        this.style = style;
        this.foreground = foreground;
        this.background = background;
        this.horizontalAlignment = horizontalAlignment;
        this.verticalAlignment = verticalAlignment;
        this.representation = representation;
        this.border = border;
        this.wrap = wrap;
    }

    public CFormat(FontStyle style, Color foreground, Color background, HorizontalAlignment horizontalAlignment, VerticalAlignment verticalAlignment) {
        this(null, null, style, foreground, background, horizontalAlignment, verticalAlignment, null, null, null);
    }

    public CFormat(FontStyle style, Color foreground, Color background, HorizontalAlignment horizontalAlignment, VerticalAlignment verticalAlignment, Representation representation, CBorder border) {
        this(null, null, style, foreground, background, horizontalAlignment, verticalAlignment, representation, border, null);
    }
    
    public CFormat(FontStyle style, Color foreground, Color background, HorizontalAlignment horizontalAlignment, VerticalAlignment verticalAlignment, Representation representation) {
        this(null, null, style, foreground, background, horizontalAlignment, verticalAlignment, representation, null, null);
    }

    public CFormat(FontStyle style, Color foreground, Color background, HorizontalAlignment horizontalAlignment, CBorder border) {
        this(null, null, style, foreground, background, horizontalAlignment, null, null, border, null);
    }

    public CFormat(FontStyle style, Representation representation) {
        this(null, null, style, null, null, null, null, representation, null, null);
    }

    public CFormat(Color foreground, Color background) {
        this(null, null, null, foreground, background, null, null, null, null, null);
    }

    public CFormat(Color foreground, Color background, HorizontalAlignment horizontalAlignment) {
        this(null, null, null, foreground, background, horizontalAlignment, null, null, null, null);
    }

    public CFormat(Color foreground, Color background, CBorder border) {
        this(null, null, null, foreground, background, null, null, null, border, null);
    }

    public CFormat(String name, int size) {
        this(name, size, null, null, null, null, null, null, null, null);
    }

    public CFormat(String name, int size, CBorder border) {
        this(name, size, null, null, null, null, null, null, border, null);
    }

    public CFormat(FontStyle fontStyle) {
        this(null, null, fontStyle, null, null, null, null, null, null, null);
    }

    public CFormat(CBorder border) {
        this(null, null, null, null, null, null, null, null, border, null);
    }

    public CFormat(HorizontalAlignment horizontalAlignment, Representation representation) {
        this(null, null, null, null, null, horizontalAlignment, null, representation, null, null);
    }

    public CFormat(HorizontalAlignment horizontalAlignment) {
        this(null, null, null, null, null, horizontalAlignment, null, null, null, null);
    }

    public CFormat(HorizontalAlignment horizontalAlignment,VerticalAlignment verticalAlignment) {
        this(null, null, null, null, null, horizontalAlignment, verticalAlignment, null, null, null);
    }

    public CFormat(HorizontalAlignment horizontalAlignment,VerticalAlignment verticalAlignment,CBorder border) {
        this(null, null, null, null, null, horizontalAlignment, verticalAlignment, null, border, null);
    }

    public CFormat(HorizontalAlignment horizontalAlignment,VerticalAlignment verticalAlignment, Boolean wrap) {
        this(null, null, null, null, null, horizontalAlignment, verticalAlignment, null, null, wrap);
    }

    public CFormat(HorizontalAlignment horizontalAlignment,VerticalAlignment verticalAlignment, CBorder border, Boolean wrap) {
        this(null, null, null, null, null, horizontalAlignment, verticalAlignment, null, border, wrap);
    }

    public CFormat(HorizontalAlignment horizontalAlignment,VerticalAlignment verticalAlignment, Representation representation, CBorder border, Boolean wrap) {
        this(null, null, null, null, null, horizontalAlignment, verticalAlignment, representation, border, wrap);
    }

    public CFormat(Representation representation) {
        this(null, null, null, null, null, null, null, representation, null, null);
    }
    
    /**
     * Returns a new CFormat instance, which fills all null fields with values from the defaults.
     * @param defaults
     * @return a new instance
     */
    public CFormat fillNull(CFormat defaults) {
        return new CFormat(
                (name != null ? name : defaults.name),
                (size != null ? size : defaults.size),
                (style != null ? style : defaults.style),
                (foreground != null ? foreground : defaults.foreground),
                (background != null ? background : defaults.background),
                (horizontalAlignment != null ? horizontalAlignment : defaults.horizontalAlignment),
                (verticalAlignment != null ? verticalAlignment : defaults.verticalAlignment),
                (representation != null ? representation : defaults.representation),
                (border != null ? border : defaults.border),
                (wrap != null ? wrap : defaults.wrap)
                );
    }

    public Color getBackground() {
        return background;
    }

    public Color getForeground() {
        return foreground;
    }

    public HorizontalAlignment getHorizontalAlignment() {
        return horizontalAlignment;
    }

    public String getName() {
        return name;
    }

    public Integer getSize() {
        return size;
    }

    public FontStyle getStyle() {
        return style;
    }

    public VerticalAlignment getVerticalAlignment() {
        return verticalAlignment;
    }

    public CBorder getBorder() {
        return border;
    }

    public Representation getRepresentation() {
        return representation;
    }

    public Boolean isWrap() {
        return wrap;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final CFormat other = (CFormat) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) return false;
        if (this.size != other.size && (this.size == null || !this.size.equals(other.size))) return false;
        if (this.style != other.style) return false;
        if (this.foreground != other.foreground && (this.foreground == null || !this.foreground.equals(other.foreground))) return false;
        if (this.background != other.background && (this.background == null || !this.background.equals(other.background))) return false;
        if (this.horizontalAlignment != other.horizontalAlignment) return false;
        if (this.verticalAlignment != other.verticalAlignment) return false;
        if (this.representation != other.representation) return false;
        if (this.border != other.border && (this.border == null || !this.border.equals(other.border))) return false;
        if (this.wrap != other.wrap && (this.wrap == null || !this.wrap.equals(other.wrap))) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 59 * hash + (this.size != null ? this.size.hashCode() : 0);
        hash = 59 * hash + (this.style != null ? this.style.hashCode() : 0);
        hash = 59 * hash + (this.foreground != null ? this.foreground.hashCode() : 0);
        hash = 59 * hash + (this.background != null ? this.background.hashCode() : 0);
        hash = 59 * hash + (this.horizontalAlignment != null ? this.horizontalAlignment.hashCode() : 0);
        hash = 59 * hash + (this.verticalAlignment != null ? this.verticalAlignment.hashCode() : 0);
        hash = 59 * hash + (this.representation != null ? this.representation.hashCode() : 0);
        hash = 59 * hash + (this.border != null ? this.border.hashCode() : 0);
        hash = 59 * hash + (this.wrap != null ? this.wrap.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "CFormat{" + "name=" + name + ", size=" + size + ", style=" + style + ", foreground=" + foreground + ", background=" + background + ", horizontalAlignment=" + horizontalAlignment + ", verticalAlignment=" + verticalAlignment + ", representation=" + representation + ", border=" + border + ", wrap=" + wrap + '}';
    }
}
