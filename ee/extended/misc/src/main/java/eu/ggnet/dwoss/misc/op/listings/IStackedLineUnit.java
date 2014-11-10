package eu.ggnet.dwoss.misc.op.listings;

import java.util.Date;

/**
 * Stacked subline for report interface for sales listings.
 */
// TODO: Combine ISimpleLine, IStackedLine and IStackedLineUnit in a correct hirachy
public interface IStackedLineUnit {

	String getWarranty();

	String getRefurbishedId();

	double getRetailerPrice();

	double getCustomerPrice();

	double getRoundedTaxedCustomerPrice();

        public String getAccessories();

	public String getComment();

	public String getConditionLevelDescription();

	public Date getMfgDate();

	public String getSerial();

}
