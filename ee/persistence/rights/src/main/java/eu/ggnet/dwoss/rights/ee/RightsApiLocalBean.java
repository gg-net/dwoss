/*
 * Copyright (C) 2020 GG-Net GmbH
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
package eu.ggnet.dwoss.rights.ee;

import java.util.*;

import javax.ejb.Stateless;
import javax.inject.Inject;

import eu.ggnet.dwoss.rights.api.AtomicRight;
import eu.ggnet.dwoss.rights.api.RightsApiLocal;
import eu.ggnet.dwoss.rights.ee.eao.OperatorEao;
import eu.ggnet.dwoss.rights.ee.entity.Operator;

/**
 *
 * @author oliver.guenther
 */
@Stateless
public class RightsApiLocalBean implements RightsApiLocal {

    @Inject
    private OperatorEao eao;

    @Override
    public Set<AtomicRight> findByName(String username) {
        return Optional.ofNullable(eao.findByUsername(username))
                .map(Operator::getAllActiveRights)
                .orElse(EnumSet.noneOf(AtomicRight.class));
    }

}
