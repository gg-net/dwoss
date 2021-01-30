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

import java.util.Objects;
import java.util.function.Function;

public class PojoColumn<T> {

    private String headline = "";

    private int preferredWidth = 10;

    private Class<?> clazz = Object.class;

    private String propertyName;

    private Function<T, ?> accessor;

    /**
     *
     * @param headline
     * @param editable
     * @param preferredWidth
     * @param clazz
     * @param propertyName
     * @deprecated Use Function constructor.
     */
    @Deprecated
    public PojoColumn(String headline, boolean editable, int preferredWidth, Class<?> clazz, String propertyName) {
        this.headline = headline;
        this.preferredWidth = preferredWidth;
        this.clazz = clazz;
        this.propertyName = propertyName;
    }

    public <R> PojoColumn(String headline, int preferredWidth, Class<R> clazz, Function<T, R> accessor) {
        this.headline = Objects.requireNonNull(headline, "headline must not be null");
        this.preferredWidth = preferredWidth;
        this.clazz = clazz;
        this.accessor = accessor;
    }

    public String getHeadline() {
        return headline;
    }

    public int getPreferredWidth() {
        return preferredWidth;
    }

    public Object getValue(T elem) {
        if ( propertyName != null ) return PojoUtil.getValue(propertyName, elem);
        if ( accessor != null ) return accessor.apply(elem);
        throw new IllegalStateException("Neither propertyName nor accessor are set");
    }

    public Class<?> getColumnClass() {
        return clazz;
    }

}
