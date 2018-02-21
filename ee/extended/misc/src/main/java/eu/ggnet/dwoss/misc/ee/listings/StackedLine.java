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
package eu.ggnet.dwoss.misc.ee.listings;

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
public class StackedLine implements Comparable<StackedLine> {

    private boolean used = true;

    private String name;

    private int amount;

    private String description;

    private String manufacturerName;

    private String manufacturerPartNo;

    private String commodityGroupName;

    private ProductGroup group;

    private TradeName brand;

    private URL imageUrl;

    private String customerPriceLabel;

    private List<StackedLineUnit> units;

    public boolean isNew() {
        return !used;
    }

    public void add(StackedLineUnit u) {
        if ( units == null ) units = new ArrayList<>();
        units.add(u);
    }

    @Override
    public int compareTo(StackedLine other) {
        if ( other == null ) return 1;
        if ( !this.getManufacturerName().equals(other.getManufacturerName()) ) return this.getManufacturerName().compareTo(other.getManufacturerName());
        if ( !this.getCommodityGroupName().equals(other.getCommodityGroupName()) ) return this.getCommodityGroupName().compareTo(other.getCommodityGroupName());
        if ( !this.getName().equals(other.getName()) ) return this.getName().compareTo(other.getName());
        if ( !this.getManufacturerPartNo().equals(other.getManufacturerPartNo()) ) return this.getManufacturerPartNo().compareTo(other.getManufacturerPartNo());
        return 0;
    }

    public void normaize() {
        if ( description != null ) description = description.replaceAll("&", "&amp;");
        if ( manufacturerName != null ) manufacturerName = manufacturerName.replaceAll("&", "&amp;");
        if ( manufacturerPartNo != null ) manufacturerPartNo = manufacturerPartNo.replaceAll("&", "&amp;");
        if ( commodityGroupName != null ) commodityGroupName = commodityGroupName.replaceAll("&", "&amp;");
    }

}