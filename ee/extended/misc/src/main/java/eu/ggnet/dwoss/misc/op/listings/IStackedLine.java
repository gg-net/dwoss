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
