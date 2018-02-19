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
import org.mapstruct.factory.Mappers;

import eu.ggnet.dwoss.uniqueunit.ee.entity.UnitCollection;

/**
 * MapStruct Mapper for UnitCollection and DTO.
 *
 * @author oliver.guenther
 */
@Mapper(componentModel = "cdi", uses = PicoProductMapper.class)
public interface UnitCollectionMapper {

    UnitCollectionMapper INSTANCE = Mappers.getMapper(UnitCollectionMapper.class);

    @Mappings({
        // This is/will be already in the entity,
        @Mapping(target = "product", ignore = true)
        , // add/remove of units is done via another method. so no change here
        @Mapping(target = "units", ignore = true)
        , // Done manual, after all. Need a comment of the change.
        @Mapping(target = "prices", ignore = true)
        , // The history is created on set with a comment, unmappable
        @Mapping(target = "priceHistories", ignore = true)
    })
    void update(@MappingTarget UnitCollection entity, UnitCollectionDto dto);

}
