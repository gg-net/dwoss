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
package eu.ggnet.dwoss.misc.op.listings;

import java.net.URL;
import java.util.List;

import eu.ggnet.dwoss.rules.ProductGroup;
import eu.ggnet.dwoss.rules.TradeName;

/**
 * Stacked line report interface for sales listings. A stacked line has information for multible sublines {@link IStackedLine#getUnits()}
 */
// TODO: Combine ISimpleLine, IStackedLine and IStackedLineUnit in a correct hirachy
public interface IStackedLine extends Comparable<IStackedLine> {

	boolean isNew();

	String getWarranty();

	String getName();

	int getAmount();

	double getRetailerPrice();

	double getCustomerPrice();

	double getRoundedTaxedCustomerPrice();

	String getCustomerPriceLabel();

	String getDescription();

	String getManufacturerName();

	String getManufacturerPartNo();

	String getComment();

	String getCommodityGroupName();

        ProductGroup getGroup();
        
        TradeName getBrand();
        
	URL getImageUrl();

	/**
	 * Returns optional Details about Units, may be null
	 *
	 * @return optional Details about Units, may be null
	 */
	List<IStackedLineUnit> getUnits();

}
