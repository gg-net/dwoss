/*
 * Copyright (C) 2024 GG-Net GmbH
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
package eu.ggnet.dwoss.receipt.ui.tryout.stub;

import java.util.*;
import java.util.stream.Collectors;

import eu.ggnet.dwoss.core.common.values.ProductGroup;
import eu.ggnet.dwoss.core.common.values.tradename.TradeName;
import eu.ggnet.dwoss.spec.api.SpecApi;
import eu.ggnet.dwoss.spec.ee.entity.ProductFamily;
import eu.ggnet.dwoss.spec.ee.entity.ProductSeries;


/**
 *
 * @author oliver.guenther
 */
public class SpecApiStub implements SpecApi {

    private final Set<ProductSeries> serieses;

    public SpecApiStub(Set<ProductSeries> serieses) {
        this.serieses = serieses;
    }
    
    @Override
    public boolean hasSpec(long productId) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public List<NameId> findProductSeries(TradeName brand, ProductGroup group) {
        return serieses.stream()
                .filter(s -> s.getBrand() == brand && s.getGroup() == group)
                .map(s -> new SpecApi.NameId(s.getId(), s.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public List<NameId> findProductFamilies(long seriesId) {
        return serieses.stream().filter(s -> s.getId() == seriesId)
                .findAny()
                .orElse(new ProductSeries())
                .getFamilys().stream()
                .map(f -> new NameId(f.getId(), f.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public List<NameId> findProductModels(long familyId) {
        return serieses.stream().flatMap(s -> s.getFamilys().stream())
        .filter(f -> f.getId() == familyId)
                .findAny()
                .orElse(new ProductFamily())
                .getModels().stream()
                .map(m -> new NameId(m.getId(), m.getName()))
                .collect(Collectors.toList());
    }

}
