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
package eu.ggnet.dwoss.mandator.api.service;

import java.util.Arrays;
import java.util.List;

import javax.ejb.Stateless;

import eu.ggnet.dwoss.redtape.ee.entity.Position;
import eu.ggnet.dwoss.util.UserInfoException;
import eu.ggnet.dwoss.util.interactiveresult.Result;
import eu.ggnet.dwoss.redtape.ee.api.UnitPositionHook;

/**
 * Sample implementation of the position service.
 * This service class will allow elaboration of positions based on a variety of parameters.
 * <p>
 * @author pascal.perau
 */
@Stateless
public class SampleHookServiceBean implements UnitPositionHook {

    @Override
    public Result<List<Position>> elaborateUnitPosition(eu.ggnet.dwoss.redtape.ee.entity.Position p, long documentId) throws UserInfoException {
        return new Result<>(Arrays.asList(p));
    }

}
