/*
 * Copyright (C) 2014 GG-Net GmbH
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
package eu.ggnet.dwoss.uniqueunit.ee.eao;

import java.util.Date;

import org.apache.commons.lang3.builder.ToStringBuilder;

import eu.ggnet.dwoss.core.common.values.tradename.TradeName;

/**
 * Holder for Count results.
 * <p>
 * @author oliver.guenther
 */
public class CountHolder {

    public final Date inputDate;

    public final TradeName brand;

    public final TradeName contractor;

    public final long count;

    public CountHolder(Date inputDate, TradeName brand, TradeName contractor, long count) {
        this.inputDate = inputDate;
        this.brand = brand;
        this.contractor = contractor;
        this.count = count;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
    
}
