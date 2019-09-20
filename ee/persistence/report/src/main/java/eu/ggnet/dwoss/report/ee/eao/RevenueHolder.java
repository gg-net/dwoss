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
package eu.ggnet.dwoss.report.ee.eao;

import java.util.Date;

import org.apache.commons.lang3.builder.ToStringBuilder;

import eu.ggnet.dwoss.common.api.values.*;

/**
 * Value holder for revenue reporting.
 * Needed her to be use in a named Query.
 * <p>
 * @author oliver.guenther
 */
public class RevenueHolder {

    public final Date reportingDate;

    public final DocumentType documentType;

    public final SalesChannel salesChannel;

    public final TradeName contractor;

    public final double price;

    public final double purchasePrice;

    public RevenueHolder(Date reportingDate, DocumentType documentType, SalesChannel salesChannel, TradeName contractor, double price, double purchasePrice) {
        this.reportingDate = reportingDate;
        this.documentType = documentType;
        this.salesChannel = salesChannel;
        this.contractor = contractor;
        this.price = price;
        this.purchasePrice = purchasePrice;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
