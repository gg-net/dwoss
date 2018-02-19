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
package eu.ggnet.dwoss.uniqueunit.ee.entity.dto;

import org.mapstruct.*;

import eu.ggnet.dwoss.uniqueunit.ee.entity.CategoryProduct;

/**
 * MapStruct Mapper for CategroyProduct and DTO.
 *
 * @author oliver.guenther
 */
@Mapper(componentModel = "cdi", uses = PicoProductMapper.class)
public interface CategoryProductMapper {

    @Mappings({
        @Mapping(target = "prices", ignore = true)
        , // The history is created on set with a comment, unmappable
        @Mapping(target = "priceHistory", ignore = true)
    })
    void update(@MappingTarget CategoryProduct entity, CategoryProductDto dto);

}
