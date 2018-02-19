/*
 * Copyright (C) 2017 GG-Net GmbH
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
package eu.ggnet.dwoss.uniqueunit.ui.categoryProduct;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import eu.ggnet.dwoss.uniqueunit.api.PicoProduct;
import eu.ggnet.dwoss.uniqueunit.ee.entity.dto.CategoryProductDto;
import eu.ggnet.dwoss.uniqueunit.ee.entity.CategoryProduct;
import eu.ggnet.dwoss.uniqueunit.ee.entity.Product;

/**
 * MapStruct Mapper for CategroyProduct and DTO.
 *
 * @author oliver.guenther
 */
@Mapper
public interface CategoryProductFxMapper {

    CategoryProductFxMapper INSTANCE = Mappers.getMapper(CategoryProductFxMapper.class);

    CategoryProductFx from(CategoryProduct cp);

    void update(@MappingTarget CategoryProductFx fx, CategoryProduct cp);

    CategoryProductDto to(CategoryProductFx fx);

    default PicoProduct map(Product p) {
        return new PicoProduct(p.getId(), p.getTradeName().getName() + " " + p.getName());
    }

}
