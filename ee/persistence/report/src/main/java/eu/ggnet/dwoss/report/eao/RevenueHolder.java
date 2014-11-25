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
package eu.ggnet.dwoss.report.eao;

import java.util.Date;

import eu.ggnet.dwoss.rules.*;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Value holder for revenue reporting.
 * Needed her to be use in a named Query.
 * <p>
 * @author oliver.guenther
 */
@Data
@AllArgsConstructor
public class RevenueHolder {

    private Date reportingDate;

    private DocumentType documentType;

    private SalesChannel salesChannel;

    private TradeName contractor;

    private double price;

    private double purchasePrice;

}
