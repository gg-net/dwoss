/*
 * Copyright (C) 2018 GG-Net GmbH
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
package eu.ggnet.dwoss.spec.ee;

import java.util.List;

import eu.ggnet.dwoss.core.common.values.ProductGroup;
import eu.ggnet.dwoss.core.common.values.tradename.TradeName;

import jakarta.ejb.Remote;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import eu.ggnet.dwoss.spec.api.SpecApi;
import eu.ggnet.dwoss.spec.ee.eao.*;

/**
 *
 * @author oliver.guenther
 */
@Stateless
@Remote(SpecApi.class)
public class SpecApiBean implements SpecApi {

    @Inject
    private ProductSpecEao eao;

    @Inject
    private ProductSeriesEao psEao;

    @Inject
    private ProductModelEao pmEao;

    @Inject
    private ProductFamilyEao pfEao;

    @Override
    public boolean hasSpec(long productId) {
        return eao.findByProductId(productId) != null;
    }

    @Override
    public List<NameId> findProductSeries(TradeName brand, ProductGroup group) {
        return psEao.findAsNameId(brand, group);
    }

    @Override
    public List<NameId> findProductFamilies(long seriesId) {
        return pfEao.findAsNameId(seriesId);
    }

    @Override
    public List<NameId> findProductModels(long familyId) {
        return pmEao.findAsNameId(familyId);
    }

}
