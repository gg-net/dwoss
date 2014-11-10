package eu.ggnet.dwoss.misc.op.listings;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import eu.ggnet.dwoss.rules.ProductGroup;
import eu.ggnet.dwoss.rules.TradeName;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *
 * @author oliver.guenther
 */
@Data
@EqualsAndHashCode
public class StackedLine implements IStackedLine {

    private boolean used = true;

    // Unused
    private String warranty;

    private String name;

    private int amount;

    // removeable, unused
    private double retailerPrice;

    // removeable, only used in processing
    private double customerPrice;

    // removeable, only used in processing
    private double roundedTaxedCustomerPrice;

    private String description;

    private String manufacturerName;

    private String manufacturerPartNo;

    // removeable, unused
    private String comment;

    private String commodityGroupName;

    private ProductGroup group;

    private TradeName brand;

    private URL imageUrl;

    private String customerPriceLabel;

    private List<IStackedLineUnit> units;

    @Override
    public boolean isNew() {
        return !used;
    }

    public void add(IStackedLineUnit u) {
        if ( units == null ) units = new ArrayList<>();
        units.add(u);
    }

    @Override
    public int compareTo(IStackedLine other) {
        if ( other == null ) return 1;
        if ( !this.getManufacturerName().equals(other.getManufacturerName()) ) return this.getManufacturerName().compareTo(other.getManufacturerName());
        if ( !this.getCommodityGroupName().equals(other.getCommodityGroupName()) ) return this.getCommodityGroupName().compareTo(other.getCommodityGroupName());
        if ( !this.getName().equals(other.getName()) ) return this.getName().compareTo(other.getName());
        if ( !this.getManufacturerPartNo().equals(other.getManufacturerPartNo()) ) return this.getManufacturerPartNo().compareTo(other.getManufacturerPartNo());
        return 0;
    }
}
