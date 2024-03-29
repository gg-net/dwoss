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
package eu.ggnet.dwoss.rights.ee.eao;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import eu.ggnet.dwoss.rights.ee.assist.Rights;
import eu.ggnet.dwoss.rights.ee.entity.Operator;
import eu.ggnet.dwoss.core.system.persistence.AbstractEao;

import com.querydsl.jpa.impl.JPAQuery;

import static eu.ggnet.dwoss.rights.ee.entity.QOperator.operator;

/**
 *
 * @author oliver.guenther
 */
@Stateless
public class OperatorEao extends AbstractEao<Operator> {

    
    @Inject
    @Rights
    private EntityManager em;

    public OperatorEao() {
        super(Operator.class);
    }

    public OperatorEao(EntityManager em) {
        this();
        this.em = em;
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public Operator findByUsername(String username) {
        return new JPAQuery<Operator>(em).from(operator).where(operator.username.eq(username)).fetchOne();
    }

}
