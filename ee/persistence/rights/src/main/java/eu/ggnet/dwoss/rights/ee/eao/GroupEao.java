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
package eu.ggnet.dwoss.rights.ee.eao;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import eu.ggnet.dwoss.core.system.persistence.AbstractEao;
import eu.ggnet.dwoss.rights.ee.assist.Rights;
import eu.ggnet.dwoss.rights.ee.entity.Persona;

import com.querydsl.jpa.impl.JPAQuery;

import static eu.ggnet.dwoss.rights.ee.entity.QPersona.persona;

/**
 * Entity access object for {@link Group} entities.
 * <p/>
 * Currently only used for testing purposes.
 *
 * @author mirko.schulze
 */
@Stateless
public class GroupEao extends AbstractEao<Persona> {

    @Inject
    @Rights
    private EntityManager em;

    public GroupEao() {
        super(Persona.class);
    }

    public GroupEao(EntityManager em) {
        this();
        this.em = em;
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    /**
     * Searches the database for the {@link Group} with the submitted name.
     *
     * @param name name of the Group
     * @return Group - the found entity or null
     */
    public Persona findByName(String name) {
        return new JPAQuery<Persona>(em).from(persona).where(persona.name.eq(name)).fetchOne();
    }

}


