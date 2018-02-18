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
package eu.ggnet.dwoss.spec.ee.entity;

import eu.ggnet.dwoss.rules.ProductGroup;
import eu.ggnet.dwoss.rules.TradeName;



/**
 * Creates Offline Elements.
 *
 * @author oliver.guenther
 */
public class SpecOfflineConstructor {

    private static SpecOfflineConstructor instance;

    public long counter = 0;

    public static SpecOfflineConstructor getInstance() {
        if (instance == null) {
            instance = new SpecOfflineConstructor();
        }
        return instance;
    }

    public BasicSpec newBasicSpec() {
        return new BasicSpec(counter++);
    }

    public Monitor newMonitor() {
      return new Monitor(counter++);
    }

    public ProductFamily newProductFamily() {
        return new ProductFamily(counter++);
    }

    public ProductModel newProductModel() {
        return new ProductModel(counter++);
    }

    public ProductSeries newProductSeries() {
        return new ProductSeries(counter++);
    }

    public ProductSeries newProductSeries(TradeName brand, ProductGroup group, String name) {
        ProductSeries series = new ProductSeries(counter++);
        series.setBrand(brand);
        series.setGroup(group);
        series.setName(name);
        return series;
    }

}
