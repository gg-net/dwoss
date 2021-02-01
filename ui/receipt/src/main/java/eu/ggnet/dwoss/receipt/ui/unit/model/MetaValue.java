/*
 * Copyright (C) 2021 GG-Net GmbH
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
package eu.ggnet.dwoss.receipt.ui.unit.model;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import eu.ggnet.dwoss.receipt.ui.unit.chain.ChainLink;

/**
 *
 * @author oliver.guenther
 */
public class MetaValue<T> {

    private T value;

    private List<ChainLink<T>> chain;

    private final UnitSurvey survey = new UnitSurvey();

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public List<ChainLink<T>> getChain() {
        return chain;
    }

    public void setChain(List<ChainLink<T>> chain) {
        this.chain = chain;
    }

    public UnitSurvey getSurvey() {
        return survey;
    }

    public boolean isSet() {
        if ( value == null ) return false;
        if ( value instanceof String ) return !StringUtils.isBlank((String)value);
        return true;
    }

}
