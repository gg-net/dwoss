/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.ggnet.dwoss.misc.op.listings;

import java.util.Date;

import lombok.Data;

/**
 *
 * @author oliver.guenther
 */
@Data
public class StackedLineUnit implements IStackedLineUnit, Comparable<IStackedLineUnit> {

    private String warranty;

    private String refurbishedId;

    // remove, unused
    private double retailerPrice;

    private double customerPrice;

    private double roundedTaxedCustomerPrice;

    private String accessories;

    private String comment;

    private String conditionLevelDescription;

    private Date mfgDate;

    private String serial;
    
    private Date warrentyTill;

    @Override
    public int compareTo(IStackedLineUnit other) {
        if ( other == null ) return 1;
        if ( !this.getConditionLevelDescription().equals(other.getConditionLevelDescription()) )
            return this.getConditionLevelDescription().compareTo(other.getConditionLevelDescription());
        if ( this.getCustomerPrice() != other.getCustomerPrice() ) return (int)(this.getCustomerPrice() - other.getCustomerPrice());
        return 0;
    }
}
