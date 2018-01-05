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
package eu.ggnet.dwoss.uniqueunit.ui.product;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import eu.ggnet.dwoss.uniqueunit.api.PicoUnit;
import eu.ggnet.dwoss.uniqueunit.assist.UnitCollectionDto;
import eu.ggnet.dwoss.uniqueunit.entity.*;
import eu.ggnet.dwoss.uniqueunit.entity.UniqueUnit.Identifier;

/**
 * MapStruct Mapper for UnitCollection and DTO.
 *
 * @author jens.papenhagen
 */
@Mapper
public interface UnitCollectionFxMapper {

    UnitCollectionFxMapper INSTANCE = Mappers.getMapper(UnitCollectionFxMapper.class);

    UnitCollectionFx from(UnitCollection uc);

    void update(@MappingTarget UnitCollectionFx fx, UnitCollection uc);

    UnitCollectionDto to(UnitCollectionFx fx);

    default PicoUnit map(UniqueUnit u) {
        return new PicoUnit(u.getId(), (String)u.getIdentifier(Identifier.SERIAL) + " " + u.getCondition().getNote());
    }

}
