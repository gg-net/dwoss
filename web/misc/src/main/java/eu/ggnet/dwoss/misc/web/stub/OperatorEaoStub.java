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
package eu.ggnet.dwoss.misc.web.stub;

import java.util.Arrays;
import java.util.List;

import javax.enterprise.inject.Alternative;

import eu.ggnet.dwoss.rights.api.AtomicRight;
import eu.ggnet.dwoss.rights.ee.eao.OperatorEao;
import eu.ggnet.dwoss.rights.ee.entity.Operator;

/**
 *
 * @author oliver.guenther
 */
@Alternative
public class OperatorEaoStub extends OperatorEao {

    @Override
    public List<Operator> findAll() {
        Operator operator = new Operator("Hans");
        operator.add(AtomicRight.CREATE_ANNULATION_INVOICE);
        operator.add(AtomicRight.CREATE_COMPLAINT);
        return Arrays.asList(operator, new Operator("Peter"));
    }

}
